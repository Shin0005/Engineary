package com.example.engineary.repository;

import com.example.engineary.model.Memo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

// Replace.NONEでインメモリを使わずSQLiteの挙動の検証を行う
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MemoRepositoryTest {

    @Autowired
    private MemoRepository memoEntryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Memo buildValidEntry() {
        Memo entry = new Memo();
        entry.setTitle("テストタイトル");
        entry.setContents("テスト内容");
        return entry;
    }

    @Nested
    @DisplayName("@GeneratedValue の検証")
    class DescribeGeneratedValue {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.1: 有効なエンティティを保存した場合、IDが自動採番される")
            void shouldAssignIdAutomaticallyOnSave() {
                Memo entry = buildValidEntry();

                Memo saved = memoEntryRepository.save(entry);
                entityManager.flush();

                assertNotNull(saved.getId());
            }
        }
    }

    @Nested
    @DisplayName("@CreatedDate の検証")
    class DescribeCreatedDate {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.2: saveした場合、createdAtが自動セットされる")
            void shouldSetCreatedAtOnSave() {
                Memo entry = buildValidEntry();

                memoEntryRepository.save(entry);
                entityManager.flush();

                assertNotNull(entry.getCreatedAt());
            }

            @Test
            @DisplayName("No.5: 更新した場合、createdAtは変化しない")
            void shouldNotChangeCreatedAtOnUpdate() throws InterruptedException {
                Memo entry = buildValidEntry();
                memoEntryRepository.save(entry);
                entityManager.flush();

                // SQLiteはミリ秒精度のため、比較前にナノ秒を切り捨てる
                LocalDateTime createdAtBefore = entry.getCreatedAt().truncatedTo(ChronoUnit.MILLIS);

                Thread.sleep(10);
                entry.setTitle("更新後タイトル");
                memoEntryRepository.save(entry);
                entityManager.flush();
                entityManager.refresh(entry);

                LocalDateTime createdAtAfter = entry.getCreatedAt().truncatedTo(ChronoUnit.MILLIS);
                assertEquals(createdAtBefore, createdAtAfter);
            }
        }
    }

    @Nested
    @DisplayName("@UpdateTimestamp の検証")
    class DescribeUpdateTimestamp {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.3: saveした場合、updatedAtが自動セットされる")
            void shouldSetUpdatedAtOnSave() {
                Memo entry = buildValidEntry();

                memoEntryRepository.save(entry);
                entityManager.flush();

                assertNotNull(entry.getUpdatedAt());
            }

            @Test
            @DisplayName("No.4: 更新した場合、updatedAtが更新前より新しくなる")
            void shouldUpdateUpdatedAtOnUpdate() throws InterruptedException {
                Memo entry = buildValidEntry();
                memoEntryRepository.save(entry);
                entityManager.flush();

                LocalDateTime updatedAtBefore = entry.getUpdatedAt();

                Thread.sleep(10);
                entry.setTitle("更新後タイトル");
                memoEntryRepository.save(entry);
                entityManager.flush();
                entityManager.refresh(entry);

                assertTrue(entry.getUpdatedAt().isAfter(updatedAtBefore));
            }
        }
    }

    @Nested
    @DisplayName("@Column(nullable = false) の検証")
    class DescribeNotNullConstraint {

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.6: titleにnullを指定して保存した場合、例外がスローされる")
            void throwsExceptionWhenTitleIsNull() {
                Memo entry = buildValidEntry();
                entry.setTitle(null);

                assertThrows(Exception.class, () -> {
                    memoEntryRepository.save(entry);
                    entityManager.flush();
                });
            }
        }
    }
}
