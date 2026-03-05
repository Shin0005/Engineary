package com.example.engineary.mapper;

import com.example.engineary.dto.MemoRequest;
import com.example.engineary.dto.MemoResponse;
import com.example.engineary.model.Memo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemoMapper")
class MemoMapperTest {

    // ===========================
    // テストデータ生成ヘルパー
    // ===========================

    private MemoRequest buildRequest(String title, String contents) {
        MemoRequest req = new MemoRequest();
        req.setTitle(title);
        req.setContents(contents);
        return req;
    }

    private Memo buildEntity(Long id, String title, String contents) {
        Memo entity = new Memo();
        entity.setId(id);
        entity.setTitle(title);
        entity.setContents(contents);
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
                MemoRequest req = buildRequest("テスト", "内容");

                Memo result = MemoMapper.toEntity(req);

                assertEquals("テスト", result.getTitle());
                assertEquals("内容", result.getContents());
            }

            @Test
            @DisplayName("No.2: contentsがnullの場合、contents=nullのEntityが返る")
            void shouldMapWithNullContents() {
                MemoRequest req = buildRequest("テスト", null);

                Memo result = MemoMapper.toEntity(req);

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
                assertThrows(NullPointerException.class, () -> MemoMapper.toEntity(null));
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("No.4: titleが1文字の場合、title=1文字のEntityが返る")
            void shouldMapSingleCharTitle() {
                MemoRequest req = buildRequest("a", "内容");

                Memo result = MemoMapper.toEntity(req);

                assertEquals("a", result.getTitle());
            }

            @Test
            @DisplayName("No.5: titleが100文字の場合、title=100文字のEntityが返る")
            void shouldMapMaxLengthTitle() {
                String title100 = "あ".repeat(100);
                MemoRequest req = buildRequest(title100, "内容");

                Memo result = MemoMapper.toEntity(req);

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
                MemoRequest req = buildRequest("テスト", "");

                Memo result = MemoMapper.toEntity(req);

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
                Memo entity = buildEntity(1L, "テスト", "内容");

                MemoResponse result = MemoMapper.toResponse(entity);

                assertEquals(1L, result.getId());
                assertEquals("テスト", result.getTitle());
                assertEquals("内容", result.getContents());
            }

            @Test
            @DisplayName("No.8: contentsがnullの場合、contents=nullのResponseが返る")
            void shouldMapWithNullContents() {
                Memo entity = buildEntity(2L, "タイトル", null);

                MemoResponse result = MemoMapper.toResponse(entity);

                assertNull(result.getContents());
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.9: entityがnullの場合、NullPointerExceptionがスローされる")
            void throwsNullPointerExceptionWhenEntityIsNull() {
                assertThrows(NullPointerException.class, () -> MemoMapper.toResponse(null));
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("No.10: idがnullの場合（未保存Entity）、id=nullのResponseが返る")
            void shouldMapWithNullId() {
                Memo entity = buildEntity(null, "タイトル", "内容");

                MemoResponse result = MemoMapper.toResponse(entity);

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
            @DisplayName("No.11: 複数要素のリストを渡した場合、3件のResponseリストが返る")
            void shouldReturnListWithMultipleElements() {
                List<Memo> entities = List.of(
                        buildEntity(1L, "タイトル1", "内容1"),
                        buildEntity(2L, "タイトル2", "内容2"),
                        buildEntity(3L, "タイトル3", "内容3")
                );

                List<MemoResponse> result = MemoMapper.toListResponse(entities);

                assertEquals(3, result.size());
                assertEquals(1L, result.get(0).getId());
                assertEquals("タイトル2", result.get(1).getTitle());
                assertEquals("内容3", result.get(2).getContents());
            }

            @Test
            @DisplayName("No.12: 1件のリストを渡した場合、1件のResponseリストが返る")
            void shouldReturnListWithSingleElement() {
                List<Memo> entities = List.of(
                        buildEntity(1L, "タイトル", "内容")
                );

                List<MemoResponse> result = MemoMapper.toListResponse(entities);

                assertEquals(1, result.size());
                assertEquals(1L, result.get(0).getId());
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.13: リストがnullの場合、空リストが返る（null安全）")
            void returnsEmptyListWhenInputIsNull() {
                List<MemoResponse> result = MemoMapper.toListResponse(null);

                assertNotNull(result);
                assertTrue(result.isEmpty());
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("No.14: 空リストを渡した場合、空リストが返る")
            void returnsEmptyListWhenInputIsEmpty() {
                List<MemoResponse> result = MemoMapper.toListResponse(Collections.emptyList());

                assertNotNull(result);
                assertTrue(result.isEmpty());
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("No.15: contentsがnullのEntityを含むリストを渡した場合、contents=nullのResponseを含むリストが返る")
            void shouldHandleEntityWithNullContents() {
                List<Memo> entities = List.of(
                        buildEntity(1L, "タイトル", null)
                );

                List<MemoResponse> result = MemoMapper.toListResponse(entities);

                assertEquals(1, result.size());
                assertNull(result.get(0).getContents());
            }
        }
    }
}
