package com.example.engineary.repository;

import com.example.engineary.model.DiaryEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

// Replace.NONEでインメモリを使わずSQLiteの挙動の検証を行う
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DiaryEntryRepositoryTest {

    @Autowired
    private DiaryEntryRepository diaryEntryRepository;

    @Autowired
    private TestEntityManager entityManager;

    // 入力値定義
    private DiaryEntry buildValidEntry() {
        DiaryEntry entry = new DiaryEntry();
        entry.setTitle("テストタイトル");
        entry.setContents("テスト内容");
        entry.setWorkedTime(60);
        entry.setWorkedDate(LocalDate.now());
        return entry;
    }

    @Nested
    @DisplayName("@GeneratedValue の検証")
    class DescribeGeneratedValue {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("有効なエンティティを保存した場合、IDが自動採番される")
            void shouldAssignIdAutomaticallyOnSave() {
                DiaryEntry entry = buildValidEntry();

                DiaryEntry saved = diaryEntryRepository.save(entry);
                // 即コミットしassertを実行
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
            @DisplayName("saveした場合、createdAtが自動セットされる")
            void shouldSetCreatedAtOnSave() {
                DiaryEntry entry = buildValidEntry();

                diaryEntryRepository.save(entry);
                entityManager.flush();

                assertNotNull(entry.getCreatedAt());
            }

            @Test
            @DisplayName("更新した場合、createdAtは変化しない")
            void shouldNotChangeCreatedAtOnUpdate() throws InterruptedException {
                // 更新前
                DiaryEntry entry = buildValidEntry();
                diaryEntryRepository.save(entry);
                entityManager.flush();

                // SQLiteはミリ秒精度のため、比較前にナノ秒を切り捨てる
                LocalDateTime createdAtBefore = entry.getCreatedAt().truncatedTo(ChronoUnit.MILLIS);

                // 更新後
                Thread.sleep(10);
                entry.setTitle("更新後タイトル");
                diaryEntryRepository.save(entry);
                entityManager.flush();
                // DBから最新の情報を読み取り更新
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
            @DisplayName("saveした場合、updatedAtが自動セットされる")
            void shouldSetUpdatedAtOnSave() {
                DiaryEntry entry = buildValidEntry();

                diaryEntryRepository.save(entry);
                entityManager.flush();

                assertNotNull(entry.getUpdatedAt());
            }

            @Test
            @DisplayName("更新した場合、updatedAtが更新前より新しくなる")
            void shouldUpdateUpdatedAtOnUpdate() throws InterruptedException {
                DiaryEntry entry = buildValidEntry();
                diaryEntryRepository.save(entry);
                entityManager.flush();

                LocalDateTime updatedAtBefore = entry.getUpdatedAt();

                // 確実に時間をおいて再実行
                Thread.sleep(10);
                entry.setTitle("更新後タイトル");
                diaryEntryRepository.save(entry);
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
            @DisplayName("titleにnullを指定して保存した場合、例外がスローされる")
            void throwsExceptionWhenTitleIsNull() {
                DiaryEntry entry = buildValidEntry();
                entry.setTitle(null);

                assertThrows(Exception.class, () -> {
                    diaryEntryRepository.save(entry);
                    entityManager.flush();
                });
            }
        }
    }
}