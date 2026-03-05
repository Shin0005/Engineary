package com.example.engineary.controller;

import com.example.engineary.dto.MemoRequest;
import com.example.engineary.dto.MemoResponse;
import com.example.engineary.exception.GlobalExceptionHandler;
import com.example.engineary.exception.ResourceNotFoundException;
import com.example.engineary.service.MemoService;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemoController")
class MemoControllerTest {

    @Mock
    private MemoService memoEntryService;

    @InjectMocks
    private MemoController memoEntryController;

    private MockMvc mockMvc;
    private JsonMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(memoEntryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = JsonMapper.builder().build();
    }

    // ================================
    // テストデータ生成ヘルパー
    // ================================

    private MemoRequest buildRequest(String title, String contents) {
        MemoRequest req = new MemoRequest();
        req.setTitle(title);
        req.setContents(contents);
        return req;
    }

    private MemoRequest validRequest() {
        return buildRequest("テストタイトル", "テスト内容");
    }

    private MemoResponse buildResponse(Long id, String title) {
        MemoResponse res = new MemoResponse();
        res.setId(id);
        res.setTitle(title);
        return res;
    }

    // ================================
    // getAllEntries
    // ================================

    @Nested
    @DisplayName("getAllEntries")
    class DescribeGetAllEntries {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.1: デフォルトページで一覧取得できる")
            void shouldReturnPagedMemoEntries() throws Exception {
                MemoResponse response = buildResponse(1L, "タイトル1");
                Page<MemoResponse> page = new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 10),
                        1);

                when(memoEntryService.getAllEntries(any(Pageable.class))).thenReturn(page);

                mockMvc.perform(get("/api/memo"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content").isArray())
                        .andExpect(jsonPath("$.content[0].id").value(1))
                        .andExpect(jsonPath("$.content[0].title").value("タイトル1"));

                verify(memoEntryService, times(1)).getAllEntries(any(Pageable.class));
            }

            @Test
            @DisplayName("No.2: 件数が0件の場合に空リストが返る")
            void shouldReturnEmptyListWhenNoEntries() throws Exception {
                Page<MemoResponse> emptyPage = new PageImpl<>(
                        Collections.emptyList(),
                        PageRequest.of(0, 10),
                        0);

                when(memoEntryService.getAllEntries(any(Pageable.class))).thenReturn(emptyPage);

                mockMvc.perform(get("/api/memo"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content").isArray())
                        .andExpect(jsonPath("$.content").isEmpty());
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("No.3: 最終ページで取得できる")
            void shouldReturnLastPage() throws Exception {
                MemoResponse response = buildResponse(11L, "最終タイトル");
                Page<MemoResponse> lastPage = new PageImpl<>(
                        List.of(response),
                        PageRequest.of(1, 10),
                        11);

                when(memoEntryService.getAllEntries(any(Pageable.class))).thenReturn(lastPage);

                mockMvc.perform(get("/api/memo?page=1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.last").value(true));
            }
        }
    }

    // ================================
    // createMemo
    // ================================

    @Nested
    @DisplayName("createMemo")
    class DescribeCreateMemo {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.4: 全フィールドが有効な値で作成できる")
            void shouldCreateMemoAndReturn201() throws Exception {
                MemoRequest request = validRequest();
                MemoResponse response = buildResponse(1L, request.getTitle());

                when(memoEntryService.createMemo(any(MemoRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/memo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.title").value("テストタイトル"));

                verify(memoEntryService, times(1)).createMemo(any(MemoRequest.class));
            }

            @Test
            @DisplayName("No.5: contentsがnull（任意項目）でも作成できる")
            void shouldCreateMemoWithNullContents() throws Exception {
                MemoRequest request = buildRequest("タイトル", null);
                MemoResponse response = buildResponse(2L, "タイトル");

                when(memoEntryService.createMemo(any(MemoRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/memo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.6: titleがブランクで400が返る")
            void throwsBadRequestWhenTitleIsBlank() throws Exception {
                MemoRequest request = buildRequest("", "内容");

                mockMvc.perform(post("/api/memo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(memoEntryService, never()).createMemo(any());
            }

            @Test
            @DisplayName("No.7: titleがnullで400が返る")
            void throwsBadRequestWhenTitleIsNull() throws Exception {
                MemoRequest request = buildRequest(null, "内容");

                mockMvc.perform(post("/api/memo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(memoEntryService, never()).createMemo(any());
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("No.8: titleが100文字（上限）で作成できる")
            void shouldCreateWhenTitleIsExactly100Chars() throws Exception {
                String title100 = "a".repeat(100);
                MemoRequest request = buildRequest(title100, "内容");
                MemoResponse response = buildResponse(3L, title100);

                when(memoEntryService.createMemo(any(MemoRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/memo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            }

            @Test
            @DisplayName("No.9: titleが101文字（上限+1）で400が返る")
            void throwsBadRequestWhenTitleExceeds100Chars() throws Exception {
                String title101 = "a".repeat(101);
                MemoRequest request = buildRequest(title101, "内容");

                mockMvc.perform(post("/api/memo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(memoEntryService, never()).createMemo(any());
            }

            @Test
            @DisplayName("No.10: contentsが5000文字（上限）で作成できる")
            void shouldCreateWhenContentsIsExactly5000Chars() throws Exception {
                String contents5000 = "あ".repeat(5000);
                MemoRequest request = buildRequest("タイトル", contents5000);
                MemoResponse response = buildResponse(5L, "タイトル");

                when(memoEntryService.createMemo(any(MemoRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/memo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            }

            @Test
            @DisplayName("No.11: contentsが5001文字（上限+1）で400が返る")
            void throwsBadRequestWhenContentsExceeds5000Chars() throws Exception {
                String contents5001 = "あ".repeat(5001);
                MemoRequest request = buildRequest("タイトル", contents5001);

                mockMvc.perform(post("/api/memo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(memoEntryService, never()).createMemo(any());
            }
        }
    }

    // ================================
    // updateMemo
    // ================================

    @Nested
    @DisplayName("updateMemo")
    class DescribeUpdateMemo {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.12: 正常な入力で200が返る")
            void shouldReturn200WhenRequestIsValid() throws Exception {
                MemoRequest request = validRequest();

                doNothing().when(memoEntryService).updateMemo(eq(1L), any(MemoRequest.class));

                mockMvc.perform(put("/api/memo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk());

                verify(memoEntryService, times(1)).updateMemo(eq(1L), any(MemoRequest.class));
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.13: ServiceがResourceNotFoundExceptionをスローしたとき404が返る")
            void throwsNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception {
                MemoRequest request = validRequest();

                doThrow(new ResourceNotFoundException(999L))
                        .when(memoEntryService).updateMemo(eq(999L), any(MemoRequest.class));

                mockMvc.perform(put("/api/memo/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isNotFound());
            }

            @Test
            @DisplayName("No.14: リクエストのtitleがブランクで400が返る")
            void throwsBadRequestWhenTitleIsBlank() throws Exception {
                MemoRequest request = buildRequest("", "内容");

                mockMvc.perform(put("/api/memo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(memoEntryService, never()).updateMemo(any(), any());
            }
        }
    }

    // ================================
    // deleteMemo
    // ================================

    @Nested
    @DisplayName("deleteMemo")
    class DescribeDeleteMemo {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("No.15: 正常なIDで204が返る")
            void shouldReturn204WhenDeletionSucceeds() throws Exception {
                doNothing().when(memoEntryService).deleteMemo(1L);

                mockMvc.perform(delete("/api/memo/1"))
                        .andExpect(status().isNoContent());

                verify(memoEntryService, times(1)).deleteMemo(1L);
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("No.16: ServiceがResourceNotFoundExceptionをスローしたとき404が返る")
            void throwsNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception {
                doThrow(new ResourceNotFoundException(999L))
                        .when(memoEntryService).deleteMemo(999L);

                mockMvc.perform(delete("/api/memo/999"))
                        .andExpect(status().isNotFound());
            }
        }
    }
}
