package com.example.engineary.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.engineary.dto.DiaryEntryRequest;
import com.example.engineary.dto.DiaryEntryResponse;
import com.example.engineary.exception.ResourceNotFoundException;
import com.example.engineary.model.DiaryEntry;
import com.example.engineary.repository.DiaryEntryRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiaryEntryService テスト")
class DiaryEntryServiceTest {

    @Mock
    private DiaryEntryRepository diaryEntryRepository;

    @InjectMocks
    private DiaryEntryService diaryEntryService;

    // ========== テスト用データひな型 ==========

    private DiaryEntry createDiaryEntry(Long id, String title, String contents, int workedTime, LocalDate workedDate) {
        DiaryEntry entry = new DiaryEntry();
        entry.setId(id);
        entry.setTitle(title);
        entry.setContents(contents);
        entry.setWorkedTime(workedTime);
        entry.setWorkedDate(workedDate);
        return entry;
    }

    private DiaryEntryRequest createRequest(String title, String contents, int workedTime, LocalDate workedDate) {
        DiaryEntryRequest req = new DiaryEntryRequest();
        req.setTitle(title);
        req.setContents(contents);
        req.setWorkedTime(workedTime);
        req.setWorkedDate(workedDate);
        return req;
    }

    // ========================================

    @Nested
    @DisplayName("getAllEntries メソッド")
    class DescribeGetAllEntries {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("DBに複数件データがある場合、ページング結果が返る")
            void returnsPageWithMultipleEntries() {
                // Arrange
                Pageable pageable = PageRequest.of(0, 10);
                List<DiaryEntry> entries = List.of(
                        createDiaryEntry(1L, "タイトル1", "内容1", 60, LocalDate.of(2024, 1, 1)),
                        createDiaryEntry(2L, "タイトル2", "内容2", 90, LocalDate.of(2024, 1, 2)),
                        createDiaryEntry(3L, "タイトル3", null, 30, LocalDate.of(2024, 1, 3)));
                Page<DiaryEntry> page = new PageImpl<>(entries, pageable, entries.size());
                when(diaryEntryRepository.findAll(pageable)).thenReturn(page);

                // Act
                Page<DiaryEntryResponse> result = diaryEntryService.getAllEntries(pageable);

                // Assert
                assertEquals(3, result.getContent().size());
                assertEquals(3, result.getTotalElements());
                assertEquals("タイトル1", result.getContent().get(0).getTitle());
            }

            @Test
            @DisplayName("DBにデータが1件だけある場合、1件のResponseが返る")
            void returnsPageWithSingleEntry() {
                // Arrange
                Pageable pageable = PageRequest.of(0, 10);
                List<DiaryEntry> entries = List.of(
                        createDiaryEntry(1L, "一件だけ", "内容", 45, LocalDate.of(2024, 6, 15)));
                Page<DiaryEntry> page = new PageImpl<>(entries, pageable, 1);
                when(diaryEntryRepository.findAll(pageable)).thenReturn(page);

                // Act
                Page<DiaryEntryResponse> result = diaryEntryService.getAllEntries(pageable);

                // Assert
                assertEquals(1, result.getContent().size());
                assertEquals(1L, result.getContent().get(0).getId());
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("DBにデータが0件の場合、空のPageが返る")
            void returnsEmptyPageWhenNoData() {
                // Arrange
                Pageable pageable = PageRequest.of(0, 10);
                Page<DiaryEntry> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
                when(diaryEntryRepository.findAll(pageable)).thenReturn(emptyPage);

                // Act
                Page<DiaryEntryResponse> result = diaryEntryService.getAllEntries(pageable);

                // Assert
                assertTrue(result.getContent().isEmpty());
                assertEquals(0, result.getTotalElements());
            }

            @Test
            @DisplayName("2ページ目を指定した場合、2ページ目のデータが返る")
            void returnsSecondPageCorrectly() {
                // Arrange
                Pageable pageable = PageRequest.of(1, 10);
                List<DiaryEntry> secondPageEntries = List.of(
                        createDiaryEntry(11L, "11件目", "内容11", 60, LocalDate.of(2024, 2, 1)));
                Page<DiaryEntry> page = new PageImpl<>(secondPageEntries, pageable, 11);
                when(diaryEntryRepository.findAll(pageable)).thenReturn(page);

                // Act
                Page<DiaryEntryResponse> result = diaryEntryService.getAllEntries(pageable);

                // Assert
                assertEquals(1, result.getContent().size());
                assertEquals(11L, result.getTotalElements());
                assertEquals(1, result.getNumber()); // ページ番号=1（2ページ目）
            }

            @Test
            @DisplayName("DBに1件しかないのにpage=1を指定した場合、空のPageが返り例外は発生しない")
            void returnsEmptyPageWhenPageIsOutOfRange() {
                // Arrange
                Pageable pageable = PageRequest.of(1, 10); // 2ページ目
                Page<DiaryEntry> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 1); // totalElements=1
                when(diaryEntryRepository.findAll(pageable)).thenReturn(emptyPage);

                // Act & Assert（例外が出ないことを確認）
                assertDoesNotThrow(() -> {
                    Page<DiaryEntryResponse> result = diaryEntryService.getAllEntries(pageable);
                    assertTrue(result.getContent().isEmpty());
                    assertEquals(1, result.getTotalElements()); // 総件数は1のまま
                });
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("size=1で取得した場合、1件のみ返る")
            void returnsSingleEntryWhenPageSizeIsOne() {
                // Arrange
                Pageable pageable = PageRequest.of(0, 1);
                List<DiaryEntry> entries = List.of(
                        createDiaryEntry(1L, "タイトル", "内容", 60, LocalDate.of(2024, 1, 1)));
                Page<DiaryEntry> page = new PageImpl<>(entries, pageable, 3);
                when(diaryEntryRepository.findAll(pageable)).thenReturn(page);

                // Act
                Page<DiaryEntryResponse> result = diaryEntryService.getAllEntries(pageable);

                // Assert
                assertEquals(1, result.getContent().size());
                assertEquals(3, result.getTotalElements());
            }
        }
    }

    // ========================================

    @Nested
    @DisplayName("createDiaryEntry メソッド")
    class DescribeCreateDiaryEntry {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("全フィールドが有効な値の場合、保存されてレスポンスが返る")
            void shouldReturnResponseWhenAllFieldsAreValid() {
                // Arrange
                DiaryEntryRequest request = createRequest("テストタイトル", "テスト内容", 60, LocalDate.of(2024, 1, 1));
                DiaryEntry savedEntity = createDiaryEntry(1L, "テストタイトル", "テスト内容", 60, LocalDate.of(2024, 1, 1));
                when(diaryEntryRepository.save(any(DiaryEntry.class))).thenReturn(savedEntity);

                // Act
                DiaryEntryResponse response = diaryEntryService.createDiaryEntry(request);

                // Assert
                assertNotNull(response);
                assertEquals(1L, response.getId());
                assertEquals("テストタイトル", response.getTitle());
                assertEquals("テスト内容", response.getContents());
                assertEquals(60, response.getWorkedTime());
                assertEquals(LocalDate.of(2024, 1, 1), response.getWorkedDate());
            }

            @Test
            @DisplayName("contentsがnullでも正常に保存される")
            void shouldSaveEntryWhenContentsIsNull() {
                // Arrange
                DiaryEntryRequest request = createRequest("タイトル", null, 30, LocalDate.of(2024, 3, 15));
                DiaryEntry savedEntity = createDiaryEntry(2L, "タイトル", null, 30, LocalDate.of(2024, 3, 15));
                when(diaryEntryRepository.save(any(DiaryEntry.class))).thenReturn(savedEntity);

                // Act
                DiaryEntryResponse response = diaryEntryService.createDiaryEntry(request);

                // Assert
                assertNotNull(response);
                assertNull(response.getContents());
            }

            @Test
            @DisplayName("saveが1回だけ呼ばれる")
            void shouldCallSaveOnce() {
                // Arrange
                DiaryEntryRequest request = createRequest("タイトル", "内容", 60, LocalDate.of(2024, 1, 1));
                DiaryEntry savedEntity = createDiaryEntry(1L, "タイトル", "内容", 60, LocalDate.of(2024, 1, 1));
                when(diaryEntryRepository.save(any(DiaryEntry.class))).thenReturn(savedEntity);

                // Act
                diaryEntryService.createDiaryEntry(request);

                // Assert
                verify(diaryEntryRepository, times(1)).save(any(DiaryEntry.class));
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("repositoryがRuntimeExceptionをスローした場合、例外が伝播する")
            void throwsExceptionWhenRepositoryFails() {
                // Arrange
                DiaryEntryRequest request = createRequest("タイトル", "内容", 60, LocalDate.of(2024, 1, 1));
                when(diaryEntryRepository.save(any(DiaryEntry.class))).thenThrow(new RuntimeException("DB error"));

                // Act & Assert
                assertThrows(RuntimeException.class, () -> diaryEntryService.createDiaryEntry(request));
            }
        }
    }

    // ========================================

    @Nested
    @DisplayName("updateDiaryEntry メソッド")
    class DescribeUpdateDiaryEntry {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("存在するidを指定した場合、各フィールドが更新されsaveが呼ばれる")
            void shouldUpdateAllFieldsWhenEntryExists() {
                // Arrange
                DiaryEntry existing = createDiaryEntry(1L, "旧タイトル", "旧内容", 30, LocalDate.of(2024, 1, 1));
                DiaryEntryRequest request = createRequest("新タイトル", "新内容", 90, LocalDate.of(2024, 6, 1));
                when(diaryEntryRepository.findById(1L)).thenReturn(Optional.of(existing));
                when(diaryEntryRepository.save(any(DiaryEntry.class))).thenReturn(existing);

                // Act
                diaryEntryService.updateDiaryEntry(1L, request);

                // Assert
                verify(diaryEntryRepository, times(1)).save(existing);
                assertEquals("新タイトル", existing.getTitle());
                assertEquals("新内容", existing.getContents());
                assertEquals(90, existing.getWorkedTime());
                assertEquals(LocalDate.of(2024, 6, 1), existing.getWorkedDate());
            }

            @Test
            @DisplayName("戻り値がvoidで、例外がスローされないこと")
            void shouldNotThrowWhenEntryExists() {
                // Arrange
                DiaryEntry existing = createDiaryEntry(1L, "タイトル", "内容", 60, LocalDate.of(2024, 1, 1));
                DiaryEntryRequest request = createRequest("新タイトル", "新内容", 60, LocalDate.of(2024, 1, 1));
                when(diaryEntryRepository.findById(1L)).thenReturn(Optional.of(existing));
                when(diaryEntryRepository.save(any(DiaryEntry.class))).thenReturn(existing);

                // Act & Assert
                assertDoesNotThrow(() -> diaryEntryService.updateDiaryEntry(1L, request));
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("存在しないidを指定した場合、ResourceNotFoundExceptionがスローされる")
            void throwsResourceNotFoundExceptionWhenIdNotExists() {
                // Arrange
                when(diaryEntryRepository.findById(999L)).thenReturn(Optional.empty());
                DiaryEntryRequest request = createRequest("タイトル", "内容", 60, LocalDate.of(2024, 1, 1));

                // Act & Assert
                assertThrows(ResourceNotFoundException.class,
                        () -> diaryEntryService.updateDiaryEntry(999L, request));
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("id=1（最小の正のid）の場合、正常に更新される")
            void shouldUpdateEntryWithMinimumId() {
                // Arrange
                DiaryEntry existing = createDiaryEntry(1L, "タイトル", "内容", 60, LocalDate.of(2024, 1, 1));
                DiaryEntryRequest request = createRequest("更新タイトル", "更新内容", 120, LocalDate.of(2024, 12, 31));
                when(diaryEntryRepository.findById(1L)).thenReturn(Optional.of(existing));
                when(diaryEntryRepository.save(any(DiaryEntry.class))).thenReturn(existing);

                // Act & Assert
                assertDoesNotThrow(() -> diaryEntryService.updateDiaryEntry(1L, request));
                verify(diaryEntryRepository).save(existing);
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("contentsをnullに更新する場合、nullで保存される")
            void shouldUpdateContentsToNull() {
                // Arrange
                DiaryEntry existing = createDiaryEntry(1L, "タイトル", "既存内容", 60, LocalDate.of(2024, 1, 1));
                DiaryEntryRequest request = createRequest("タイトル", null, 60, LocalDate.of(2024, 1, 1));
                when(diaryEntryRepository.findById(1L)).thenReturn(Optional.of(existing));
                when(diaryEntryRepository.save(any(DiaryEntry.class))).thenReturn(existing);

                // Act
                diaryEntryService.updateDiaryEntry(1L, request);

                // Assert
                assertNull(existing.getContents());
                verify(diaryEntryRepository).save(existing);
            }
        }
    }

    // ========================================

    @Nested
    @DisplayName("deleteDiaryEntry メソッド")
    class DescribeDeleteDiaryEntry {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("存在するidを指定した場合、deleteが1回呼ばれる")
            void shouldCallDeleteOnceWhenEntryExists() {
                // Arrange
                DiaryEntry existing = createDiaryEntry(1L, "タイトル", "内容", 60, LocalDate.of(2024, 1, 1));
                when(diaryEntryRepository.findById(1L)).thenReturn(Optional.of(existing));

                // Act
                diaryEntryService.deleteDiaryEntry(1L);

                // Assert
                verify(diaryEntryRepository, times(1)).delete(existing);
            }

            @Test
            @DisplayName("戻り値がvoidで、例外がスローされないこと")
            void shouldNotThrowWhenEntryExists() {
                // Arrange
                DiaryEntry existing = createDiaryEntry(1L, "タイトル", "内容", 60, LocalDate.of(2024, 1, 1));
                when(diaryEntryRepository.findById(1L)).thenReturn(Optional.of(existing));

                // Act & Assert
                assertDoesNotThrow(() -> diaryEntryService.deleteDiaryEntry(1L));
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("存在しないidを指定した場合、ResourceNotFoundExceptionがスローされる")
            void throwsResourceNotFoundExceptionWhenIdNotExists() {
                // Arrange
                when(diaryEntryRepository.findById(999L)).thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(ResourceNotFoundException.class,
                        () -> diaryEntryService.deleteDiaryEntry(999L));
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("id=1（最小の正のid）の場合、正常に削除される")
            void shouldDeleteEntryWithMinimumId() {
                // Arrange
                DiaryEntry existing = createDiaryEntry(1L, "タイトル", "内容", 60, LocalDate.of(2024, 1, 1));
                when(diaryEntryRepository.findById(1L)).thenReturn(Optional.of(existing));

                // Act & Assert
                assertDoesNotThrow(() -> diaryEntryService.deleteDiaryEntry(1L));
                verify(diaryEntryRepository).delete(existing);
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("同じidで2回削除した場合、2回目はResourceNotFoundExceptionがスローされる")
            void throwsExceptionOnSecondDeleteWithSameId() {
                // Arrange
                DiaryEntry existing = createDiaryEntry(1L, "タイトル", "内容", 60, LocalDate.of(2024, 1, 1));
                // 1回目: 存在する、2回目: 存在しない
                when(diaryEntryRepository.findById(1L))
                        .thenReturn(Optional.of(existing))
                        .thenReturn(Optional.empty());

                // Act - 1回目は正常
                assertDoesNotThrow(() -> diaryEntryService.deleteDiaryEntry(1L));

                // Assert - 2回目は例外
                assertThrows(ResourceNotFoundException.class,
                        () -> diaryEntryService.deleteDiaryEntry(1L));
            }
        }
    }
}