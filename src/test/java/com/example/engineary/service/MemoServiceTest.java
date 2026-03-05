package com.example.engineary.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

import com.example.engineary.dto.MemoRequest;
import com.example.engineary.dto.MemoResponse;
import com.example.engineary.exception.ResourceNotFoundException;
import com.example.engineary.model.Memo;
import com.example.engineary.repository.MemoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemoService テスト")
class MemoServiceTest {

    @Mock
    private MemoRepository memoEntryRepository;

    @InjectMocks
    private MemoService memoEntryService;

    // ========== テスト用データひな型 ==========

    private Memo createMemo(Long id, String title, String contents) {
        Memo entry = new Memo();
        entry.setId(id);
        entry.setTitle(title);
        entry.setContents(contents);
        return entry;
    }

    private MemoRequest createRequest(String title, String contents) {
        MemoRequest req = new MemoRequest();
        req.setTitle(title);
        req.setContents(contents);
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
            @DisplayName("No.1: DBに複数件データがある場合、ページング結果が返る")
            void returnsPageWithMultipleEntries() {
                Pageable pageable = PageRequest.of(0, 10);
                List<Memo> entries = List.of(
                        createMemo(1L, "タイトル1", "内容1"),
                        createMemo(2L, "タイトル2", "内容2"),
                        createMemo(3L, "タイトル3", null));
                Page<Memo> page = new PageImpl<>(entries, pageable, entries.size());
                when(memoEntryRepository.findAll(pageable)).thenReturn(page);

                Page<MemoResponse> result = memoEntryService.getAllEntries(pageable);

                assertEquals(3, result.getContent().size());
                assertEquals(3, result.getTotalElements());
                assertEquals("タイトル1", result.getContent().get(0).getTitle());
            }

            @Test
            @DisplayName("No.2: DBにデータが1件だけある場合、1件のResponseが返る")
            void returnsPageWithSingleEntry() {
                Pageable pageable = PageRequest.of(0, 10);
                List<Memo> entries = List.of(
                        createMemo(1L, "一件だけ", "内容"));
                Page<Memo> page = new PageImpl<>(entries, pageable, 1);
                when(memoEntryRepository.findAll(pageable)).thenReturn(page);

                Page<MemoResponse> result = memoEntryService.getAllEntries(pageable);

                assertEquals(1, result.getContent().size());
                assertEquals(1L, result.getContent().get(0).getId());
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("No.3: DBにデータが0件の場合、空のPageが返る")
            void returnsEmptyPageWhenNoData() {
                Pageable pageable = PageRequest.of(0, 10);
                Page<Memo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
                when(memoEntryRepository.findAll(pageable)).thenReturn(emptyPage);

                Page<MemoResponse> result = memoEntryService.getAllEntries(pageable);

                assertTrue(result.getContent().isEmpty());
                assertEquals(0, result.getTotalElements());
            }

            @Test
            @DisplayName("No.4: 2ページ目を指定した場合、2ページ目のデータが返る")
            void returnsSecondPageCorrectly() {
                Pageable pageable = PageRequest.of(1, 10);
                List<Memo> secondPageEntries = List.of(
                        createMemo(11L, "11件目", "内容11"));
                Page<Memo> page = new PageImpl<>(secondPageEntries, pageable, 11);
                when(memoEntryRepository.findAll(pageable)).thenReturn(page);

                Page<MemoResponse> result = memoEntryService.getAllEntries(pageable);

                assertEquals(1, result.getContent().size());
                assertEquals(11L, result.getTotalElements());
                assertEquals(1, result.getNumber());
            }

            @Test
            @DisplayName("No.5: DBに1件しかないのにpage=1を指定した場合、空のPageが返り例外は発生しない")
            void returnsEmptyPageWhenPageIsOutOfRange() {
                Pageable pageable = PageRequest.of(1, 10);
                Page<Memo> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 1);
                when(memoEntryRepository.findAll(pageable)).thenReturn(emptyPage);

                assertDoesNotThrow(() -> {
                    Page<MemoResponse> result = memoEntryService.getAllEntries(pageable);
                    assertTrue(result.getContent().isEmpty());
                    assertEquals(1, result.getTotalElements());
                });
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("No.6: size=1で取得した場合、1件のみ返る")
            void returnsSingleEntryWhenPageSizeIsOne() {
                Pageable pageable = PageRequest.of(0, 1);
                List<Memo> entries = List.of(
                        createMemo(1L, "タイトル", "内容"));
                Page<Memo> page = new PageImpl<>(entries, pageable, 3);
                when(memoEntryRepository.findAll(pageable)).thenReturn(page);

                Page<MemoResponse> result = memoEntryService.getAllEntries(pageable);

                assertEquals(1, result.getContent().size());
                assertEquals(3, result.getTotalElements());
            }
        }
    }

    // ========================================

    @Nested
    @DisplayName("createMemo メソッド")
    class DescribeCreateMemo {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.7: 全フィールドが有効な値の場合、保存されてレスポンスが返る")
            void shouldReturnResponseWhenAllFieldsAreValid() {
                MemoRequest request = createRequest("テストタイトル", "テスト内容");
                Memo savedEntity = createMemo(1L, "テストタイトル", "テスト内容");
                when(memoEntryRepository.save(any(Memo.class))).thenReturn(savedEntity);

                MemoResponse response = memoEntryService.createMemo(request);

                assertNotNull(response);
                assertEquals(1L, response.getId());
                assertEquals("テストタイトル", response.getTitle());
                assertEquals("テスト内容", response.getContents());
            }

            @Test
            @DisplayName("No.8: contentsがnullでも正常に保存される")
            void shouldSaveEntryWhenContentsIsNull() {
                MemoRequest request = createRequest("タイトル", null);
                Memo savedEntity = createMemo(2L, "タイトル", null);
                when(memoEntryRepository.save(any(Memo.class))).thenReturn(savedEntity);

                MemoResponse response = memoEntryService.createMemo(request);

                assertNotNull(response);
                assertNull(response.getContents());
            }

            @Test
            @DisplayName("No.9: saveが1回だけ呼ばれる")
            void shouldCallSaveOnce() {
                MemoRequest request = createRequest("タイトル", "内容");
                Memo savedEntity = createMemo(1L, "タイトル", "内容");
                when(memoEntryRepository.save(any(Memo.class))).thenReturn(savedEntity);

                memoEntryService.createMemo(request);

                verify(memoEntryRepository, times(1)).save(any(Memo.class));
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.10: repositoryがRuntimeExceptionをスローした場合、例外が伝播する")
            void throwsExceptionWhenRepositoryFails() {
                MemoRequest request = createRequest("タイトル", "内容");
                when(memoEntryRepository.save(any(Memo.class))).thenThrow(new RuntimeException("DB error"));

                assertThrows(RuntimeException.class, () -> memoEntryService.createMemo(request));
            }
        }
    }

    // ========================================

    @Nested
    @DisplayName("updateMemo メソッド")
    class DescribeUpdateMemo {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.11: 存在するidを指定した場合、各フィールドが更新されsaveが呼ばれる")
            void shouldUpdateAllFieldsWhenEntryExists() {
                Memo existing = createMemo(1L, "旧タイトル", "旧内容");
                MemoRequest request = createRequest("新タイトル", "新内容");
                when(memoEntryRepository.findById(1L)).thenReturn(Optional.of(existing));
                when(memoEntryRepository.save(any(Memo.class))).thenReturn(existing);

                memoEntryService.updateMemo(1L, request);

                verify(memoEntryRepository, times(1)).save(existing);
                assertEquals("新タイトル", existing.getTitle());
                assertEquals("新内容", existing.getContents());
            }

            @Test
            @DisplayName("No.12: 戻り値がvoidで、例外がスローされないこと")
            void shouldNotThrowWhenEntryExists() {
                Memo existing = createMemo(1L, "タイトル", "内容");
                MemoRequest request = createRequest("新タイトル", "新内容");
                when(memoEntryRepository.findById(1L)).thenReturn(Optional.of(existing));
                when(memoEntryRepository.save(any(Memo.class))).thenReturn(existing);

                assertDoesNotThrow(() -> memoEntryService.updateMemo(1L, request));
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.13: 存在しないidを指定した場合、ResourceNotFoundExceptionがスローされる")
            void throwsResourceNotFoundExceptionWhenIdNotExists() {
                when(memoEntryRepository.findById(999L)).thenReturn(Optional.empty());
                MemoRequest request = createRequest("タイトル", "内容");

                assertThrows(ResourceNotFoundException.class,
                        () -> memoEntryService.updateMemo(999L, request));
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("No.15: id=1（最小の正のid）の場合、正常に更新される")
            void shouldUpdateEntryWithMinimumId() {
                Memo existing = createMemo(1L, "タイトル", "内容");
                MemoRequest request = createRequest("更新タイトル", "更新内容");
                when(memoEntryRepository.findById(1L)).thenReturn(Optional.of(existing));
                when(memoEntryRepository.save(any(Memo.class))).thenReturn(existing);

                assertDoesNotThrow(() -> memoEntryService.updateMemo(1L, request));
                verify(memoEntryRepository).save(existing);
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("No.16: contentsをnullに更新する場合、nullで保存される")
            void shouldUpdateContentsToNull() {
                Memo existing = createMemo(1L, "タイトル", "既存内容");
                MemoRequest request = createRequest("タイトル", null);
                when(memoEntryRepository.findById(1L)).thenReturn(Optional.of(existing));
                when(memoEntryRepository.save(any(Memo.class))).thenReturn(existing);

                memoEntryService.updateMemo(1L, request);

                assertNull(existing.getContents());
                verify(memoEntryRepository).save(existing);
            }
        }
    }

    // ========================================

    @Nested
    @DisplayName("deleteMemo メソッド")
    class DescribeDeleteMemo {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.17: 存在するidを指定した場合、deleteが1回呼ばれる")
            void shouldCallDeleteOnceWhenEntryExists() {
                Memo existing = createMemo(1L, "タイトル", "内容");
                when(memoEntryRepository.findById(1L)).thenReturn(Optional.of(existing));

                memoEntryService.deleteMemo(1L);

                verify(memoEntryRepository, times(1)).delete(existing);
            }

            @Test
            @DisplayName("No.18: 戻り値がvoidで、例外がスローされないこと")
            void shouldNotThrowWhenEntryExists() {
                Memo existing = createMemo(1L, "タイトル", "内容");
                when(memoEntryRepository.findById(1L)).thenReturn(Optional.of(existing));

                assertDoesNotThrow(() -> memoEntryService.deleteMemo(1L));
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.19: 存在しないidを指定した場合、ResourceNotFoundExceptionがスローされる")
            void throwsResourceNotFoundExceptionWhenIdNotExists() {
                when(memoEntryRepository.findById(999L)).thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class,
                        () -> memoEntryService.deleteMemo(999L));
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("No.20: id=1（最小の正のid）の場合、正常に削除される")
            void shouldDeleteEntryWithMinimumId() {
                Memo existing = createMemo(1L, "タイトル", "内容");
                when(memoEntryRepository.findById(1L)).thenReturn(Optional.of(existing));

                assertDoesNotThrow(() -> memoEntryService.deleteMemo(1L));
                verify(memoEntryRepository).delete(existing);
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("No.21: 同じidで2回削除した場合、2回目はResourceNotFoundExceptionがスローされる")
            void throwsExceptionOnSecondDeleteWithSameId() {
                Memo existing = createMemo(1L, "タイトル", "内容");
                when(memoEntryRepository.findById(1L))
                        .thenReturn(Optional.of(existing))
                        .thenReturn(Optional.empty());

                assertDoesNotThrow(() -> memoEntryService.deleteMemo(1L));

                assertThrows(ResourceNotFoundException.class,
                        () -> memoEntryService.deleteMemo(1L));
            }
        }
    }
}
