package com.example.engineary.mapper;

import com.example.engineary.dto.DiaryEntryRequest;
import com.example.engineary.dto.DiaryEntryResponse;
import com.example.engineary.model.DiaryEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiaryEntryMapper")
class DiaryEntryMapperTest {

    // ===========================
    // テストデータ生成ヘルパー
    // ===========================

    private DiaryEntryRequest buildRequest(String title, String contents, Integer workedTime, LocalDate workedDate) {
        DiaryEntryRequest req = new DiaryEntryRequest();
        req.setTitle(title);
        req.setContents(contents);
        req.setWorkedTime(workedTime);
        req.setWorkedDate(workedDate);
        return req;
    }

    private DiaryEntry buildEntity(Long id, String title, String contents, Integer workedTime, LocalDate workedDate) {
        DiaryEntry entity = new DiaryEntry();
        entity.setId(id);
        entity.setTitle(title);
        entity.setContents(contents);
        entity.setWorkedTime(workedTime);
        entity.setWorkedDate(workedDate);
        return entity;
    }

    // ===========================
    // toEntity
    // ===========================

    @Nested
    @DisplayName("toEntity")
    class DescribeToEntity {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.1: 全フィールドが有効な値の場合、各フィールドが正しくマッピングされたEntityが返る")
            void shouldMapAllFieldsCorrectly() {
                LocalDate date = LocalDate.of(2024, 1, 1);
                DiaryEntryRequest req = buildRequest("テスト", "内容", 60, date);

                DiaryEntry result = DiaryEntryMapper.toEntity(req);

                assertEquals("テスト", result.getTitle());
                assertEquals("内容", result.getContents());
                assertEquals(60, result.getWorkedTime());
                assertEquals(date, result.getWorkedDate());
            }

            @Test
            @DisplayName("No.2: contentsがnullの場合、contents=nullのEntityが返る")
            void shouldMapWithNullContents() {
                DiaryEntryRequest req = buildRequest("テスト", null, 30, LocalDate.of(2024, 6, 15));

                DiaryEntry result = DiaryEntryMapper.toEntity(req);

                assertNull(result.getContents());
                assertEquals("テスト", result.getTitle());
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.3: requestがnullの場合、NullPointerExceptionがスローされる")
            void throwsNullPointerExceptionWhenRequestIsNull() {
                assertThrows(NullPointerException.class, () -> DiaryEntryMapper.toEntity(null));
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("No.4: titleが1文字の場合、title=1文字のEntityが返る")
            void shouldMapSingleCharTitle() {
                DiaryEntryRequest req = buildRequest("a", "内容", 60, LocalDate.of(2024, 1, 1));

                DiaryEntry result = DiaryEntryMapper.toEntity(req);

                assertEquals("a", result.getTitle());
            }

            @Test
            @DisplayName("No.5: titleが100文字の場合、title=100文字のEntityが返る")
            void shouldMapMaxLengthTitle() {
                String title100 = "あ".repeat(100);
                DiaryEntryRequest req = buildRequest(title100, "内容", 60, LocalDate.of(2024, 1, 1));

                DiaryEntry result = DiaryEntryMapper.toEntity(req);

                assertEquals(title100, result.getTitle());
                assertEquals(100, result.getTitle().length());
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("No.6: contentsが空文字の場合、contents=空文字のEntityが返る")
            void shouldMapEmptyContents() {
                DiaryEntryRequest req = buildRequest("テスト", "", 60, LocalDate.of(2024, 1, 1));

                DiaryEntry result = DiaryEntryMapper.toEntity(req);

                assertEquals("", result.getContents());
            }
        }
    }

    // ===========================
    // toResponse
    // ===========================

    @Nested
    @DisplayName("toResponse")
    class DescribeToResponse {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.7: 全フィールドが有効な値の場合、各フィールドが正しくマッピングされたResponseが返る")
            void shouldMapAllFieldsCorrectly() {
                LocalDate date = LocalDate.of(2024, 1, 1);
                DiaryEntry entity = buildEntity(1L, "テスト", "内容", 60, date);

                DiaryEntryResponse result = DiaryEntryMapper.toResponse(entity);

                assertEquals(1L, result.getId());
                assertEquals("テスト", result.getTitle());
                assertEquals("内容", result.getContents());
                assertEquals(60, result.getWorkedTime());
                assertEquals(date, result.getWorkedDate());
            }

            @Test
            @DisplayName("No.8: contentsがnullの場合、contents=nullのResponseが返る")
            void shouldMapWithNullContents() {
                DiaryEntry entity = buildEntity(2L, "タイトル", null, 30, LocalDate.of(2024, 3, 20));

                DiaryEntryResponse result = DiaryEntryMapper.toResponse(entity);

                assertNull(result.getContents());
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.9: entityがnullの場合、NullPointerExceptionがスローされる")
            void throwsNullPointerExceptionWhenEntityIsNull() {
                assertThrows(NullPointerException.class, () -> DiaryEntryMapper.toResponse(null));
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("No.10: workedTimeが1の場合、workedTime=1のResponseが返る")
            void shouldMapMinWorkedTime() {
                DiaryEntry entity = buildEntity(1L, "テスト", "内容", 1, LocalDate.of(2024, 1, 1));

                DiaryEntryResponse result = DiaryEntryMapper.toResponse(entity);

                assertEquals(1, result.getWorkedTime());
            }

            @Test
            @DisplayName("No.11: workedTimeが1440の場合、workedTime=1440のResponseが返る")
            void shouldMapMaxWorkedTime() {
                DiaryEntry entity = buildEntity(1L, "テスト", "内容", 1440, LocalDate.of(2024, 1, 1));

                DiaryEntryResponse result = DiaryEntryMapper.toResponse(entity);

                assertEquals(1440, result.getWorkedTime());
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("No.12: idがnullの場合（未保存Entity）、id=nullのResponseが返る")
            void shouldMapWithNullId() {
                DiaryEntry entity = buildEntity(null, "タイトル", "内容", 60, LocalDate.of(2024, 5, 10));

                DiaryEntryResponse result = DiaryEntryMapper.toResponse(entity);

                assertNull(result.getId());
            }
        }
    }

    // ===========================
    // toListResponse
    // ===========================

    @Nested
    @DisplayName("toListResponse")
    class DescribeToListResponse {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.13: 複数要素のリストを渡した場合、3件のResponseリストが返る")
            void shouldReturnListWithMultipleElements() {
                List<DiaryEntry> entities = List.of(
                        buildEntity(1L, "タイトル1", "内容1", 30, LocalDate.of(2024, 1, 1)),
                        buildEntity(2L, "タイトル2", "内容2", 60, LocalDate.of(2024, 1, 2)),
                        buildEntity(3L, "タイトル3", "内容3", 90, LocalDate.of(2024, 1, 3))
                );

                List<DiaryEntryResponse> result = DiaryEntryMapper.toListResponse(entities);

                assertEquals(3, result.size());
                assertEquals(1L, result.get(0).getId());
                assertEquals("タイトル2", result.get(1).getTitle());
                assertEquals(90, result.get(2).getWorkedTime());
            }

            @Test
            @DisplayName("No.14: 1件のリストを渡した場合、1件のResponseリストが返る")
            void shouldReturnListWithSingleElement() {
                List<DiaryEntry> entities = List.of(
                        buildEntity(1L, "タイトル", "内容", 45, LocalDate.of(2024, 6, 1))
                );

                List<DiaryEntryResponse> result = DiaryEntryMapper.toListResponse(entities);

                assertEquals(1, result.size());
                assertEquals(1L, result.get(0).getId());
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.15: リストがnullの場合、空リストが返る（null安全）")
            void returnsEmptyListWhenInputIsNull() {
                List<DiaryEntryResponse> result = DiaryEntryMapper.toListResponse(null);

                assertNotNull(result);
                assertTrue(result.isEmpty());
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("No.16: 空リストを渡した場合、空リストが返る")
            void returnsEmptyListWhenInputIsEmpty() {
                List<DiaryEntryResponse> result = DiaryEntryMapper.toListResponse(Collections.emptyList());

                assertNotNull(result);
                assertTrue(result.isEmpty());
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("No.17: contentsがnullのEntityを含むリストを渡した場合、contents=nullのResponseを含むリストが返る")
            void shouldHandleEntityWithNullContents() {
                List<DiaryEntry> entities = List.of(
                        buildEntity(1L, "タイトル", null, 60, LocalDate.of(2024, 7, 20))
                );

                List<DiaryEntryResponse> result = DiaryEntryMapper.toListResponse(entities);

                assertEquals(1, result.size());
                assertNull(result.get(0).getContents());
            }
        }
    }
}
