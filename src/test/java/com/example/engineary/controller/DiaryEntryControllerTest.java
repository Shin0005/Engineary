package com.example.engineary.controller;

import com.example.engineary.dto.DiaryEntryRequest;
import com.example.engineary.dto.DiaryEntryResponse;
import com.example.engineary.exception.GlobalExceptionHandler;
import com.example.engineary.exception.ResourceNotFoundException;
import com.example.engineary.service.DiaryEntryService;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiaryEntryController")
class DiaryEntryControllerTest {

    @Mock
    private DiaryEntryService diaryEntryService;

    @InjectMocks
    private DiaryEntryController diaryEntryController;

    private MockMvc mockMvc;
    private JsonMapper objectMapper;

    // mockMvc
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(diaryEntryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = JsonMapper.builder().build();
    }

    // ================================
    // テストデータ生成ヘルパー
    // ================================

    // request作成
    private DiaryEntryRequest buildRequest(String title, String contents, Integer workedTime, LocalDate workedDate) {
        DiaryEntryRequest req = new DiaryEntryRequest();
        req.setTitle(title);
        req.setContents(contents);
        req.setWorkedTime(workedTime);
        req.setWorkedDate(workedDate);
        return req;
    }

    private DiaryEntryRequest validRequest() {
        return buildRequest("テストタイトル", "テスト内容", 60, LocalDate.of(2024, 1, 15));
    }

    // response作成
    private DiaryEntryResponse buildResponse(Long id, String title, Integer workedTime, LocalDate workedDate) {
        DiaryEntryResponse res = new DiaryEntryResponse();
        res.setId(id);
        res.setTitle(title);
        res.setWorkedTime(workedTime);
        res.setWorkedDate(workedDate);
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
            @DisplayName("デフォルトページで一覧取得できる")
            void shouldReturnPagedDiaryEntries() throws Exception {
                DiaryEntryResponse response = buildResponse(1L, "タイトル1", 60, LocalDate.of(2024, 1, 15));
                Page<DiaryEntryResponse> page = new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 10),
                        1);

                when(diaryEntryService.getAllEntries(any(Pageable.class))).thenReturn(page);

                mockMvc.perform(get("/api/diary"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content").isArray())
                        .andExpect(jsonPath("$.content[0].id").value(1))
                        .andExpect(jsonPath("$.content[0].title").value("タイトル1"));

                verify(diaryEntryService, times(1)).getAllEntries(any(Pageable.class));
            }

            @Test
            @DisplayName("件数が0件の場合に空リストが返る")
            void shouldReturnEmptyListWhenNoEntries() throws Exception {
                Page<DiaryEntryResponse> emptyPage = new PageImpl<>(
                        Collections.emptyList(),
                        PageRequest.of(0, 10),
                        0);

                when(diaryEntryService.getAllEntries(any(Pageable.class))).thenReturn(emptyPage);

                mockMvc.perform(get("/api/diary"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content").isArray())
                        .andExpect(jsonPath("$.content").isEmpty());
            }
        }

        @Nested
        @DisplayName("準正常系")
        class EdgeCases {

            @Test
            @DisplayName("最終ページで取得できる")
            void shouldReturnLastPage() throws Exception {
                DiaryEntryResponse response = buildResponse(11L, "最終タイトル", 30, LocalDate.of(2024, 2, 1));
                Page<DiaryEntryResponse> lastPage = new PageImpl<>(
                        List.of(response),
                        PageRequest.of(1, 10),
                        11);

                when(diaryEntryService.getAllEntries(any(Pageable.class))).thenReturn(lastPage);

                mockMvc.perform(get("/api/diary?page=1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.last").value(true));
            }
        }
    }

    // ================================
    // createDiaryEntry
    // ================================

    @Nested
    @DisplayName("createDiaryEntry")
    class DescribeCreateDiaryEntry {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("全フィールドが有効な値で作成できる")
            void shouldCreateDiaryEntryAndReturn201() throws Exception {
                DiaryEntryRequest request = validRequest();
                DiaryEntryResponse response = buildResponse(1L, request.getTitle(), request.getWorkedTime(),
                        request.getWorkedDate());

                when(diaryEntryService.createDiaryEntry(any(DiaryEntryRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.title").value("テストタイトル"));

                verify(diaryEntryService, times(1)).createDiaryEntry(any(DiaryEntryRequest.class));
            }

            @Test
            @DisplayName("contentsがnull（任意項目）でも作成できる")
            void shouldCreateDiaryEntryWithNullContents() throws Exception {
                DiaryEntryRequest request = buildRequest("タイトル", null, 60, LocalDate.of(2024, 1, 15));
                DiaryEntryResponse response = buildResponse(2L, "タイトル", 60, LocalDate.of(2024, 1, 15));

                when(diaryEntryService.createDiaryEntry(any(DiaryEntryRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("titleがブランクで400が返る")
            void throwsBadRequestWhenTitleIsBlank() throws Exception {
                DiaryEntryRequest request = buildRequest("", "内容", 60, LocalDate.of(2024, 1, 15));

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(diaryEntryService, never()).createDiaryEntry(any());
            }

            @Test
            @DisplayName("titleがnullで400が返る")
            void throwsBadRequestWhenTitleIsNull() throws Exception {
                DiaryEntryRequest request = buildRequest(null, "内容", 60, LocalDate.of(2024, 1, 15));

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(diaryEntryService, never()).createDiaryEntry(any());
            }

            @Test
            @DisplayName("workedTimeがnullで400が返る")
            void throwsBadRequestWhenWorkedTimeIsNull() throws Exception {
                DiaryEntryRequest request = buildRequest("タイトル", "内容", null, LocalDate.of(2024, 1, 15));

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(diaryEntryService, never()).createDiaryEntry(any());
            }

            @Test
            @DisplayName("workedDateがnullで400が返る")
            void throwsBadRequestWhenWorkedDateIsNull() throws Exception {
                DiaryEntryRequest request = buildRequest("タイトル", "内容", 60, null);

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(diaryEntryService, never()).createDiaryEntry(any());
            }
        }

        @Nested
        @DisplayName("境界値系")
        class BoundaryValues {

            @Test
            @DisplayName("titleが100文字（上限）で作成できる")
            void shouldCreateWhenTitleIsExactly100Chars() throws Exception {
                String title100 = "a".repeat(100);
                DiaryEntryRequest request = buildRequest(title100, "内容", 60, LocalDate.of(2024, 1, 15));
                DiaryEntryResponse response = buildResponse(3L, title100, 60, LocalDate.of(2024, 1, 15));

                when(diaryEntryService.createDiaryEntry(any(DiaryEntryRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            }

            @Test
            @DisplayName("titleが101文字（上限+1）で400が返る")
            void throwsBadRequestWhenTitleExceeds100Chars() throws Exception {
                String title101 = "a".repeat(101);
                DiaryEntryRequest request = buildRequest(title101, "内容", 60, LocalDate.of(2024, 1, 15));

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(diaryEntryService, never()).createDiaryEntry(any());
            }

            @Test
            @DisplayName("workedTimeが1440（上限）で作成できる")
            void shouldCreateWhenWorkedTimeIs1440() throws Exception {
                DiaryEntryRequest request = buildRequest("タイトル", "内容", 1440, LocalDate.of(2024, 1, 15));
                DiaryEntryResponse response = buildResponse(4L, "タイトル", 1440, LocalDate.of(2024, 1, 15));

                when(diaryEntryService.createDiaryEntry(any(DiaryEntryRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            }

            @Test
            @DisplayName("workedTimeが1441（上限+1）で400が返る")
            void throwsBadRequestWhenWorkedTimeExceeds1440() throws Exception {
                DiaryEntryRequest request = buildRequest("タイトル", "内容", 1441, LocalDate.of(2024, 1, 15));

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(diaryEntryService, never()).createDiaryEntry(any());
            }

            @Test
            @DisplayName("workedTimeが1（下限）で作成できる")
            void shouldCreateWhenWorkedTimeIs1() throws Exception {
                DiaryEntryRequest request = buildRequest("タイトル", "内容", 1, LocalDate.of(2024, 1, 15));
                DiaryEntryResponse response = buildResponse(50L, "タイトル", 1, LocalDate.of(2024, 1, 15));

                when(diaryEntryService.createDiaryEntry(any(DiaryEntryRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            }

            @Test
            @DisplayName("workedTimeが0（下限-1）で400が返る")
            void throwsBadRequestWhenWorkedTimeExceeds0() throws Exception {
                DiaryEntryRequest request = buildRequest("タイトル", "内容", 0, LocalDate.of(2024, 1, 15));

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(diaryEntryService, never()).createDiaryEntry(any());
            }

            @Test
            @DisplayName("contentsが5000文字（上限）で作成できる")
            void shouldCreateWhenContentsIsExactly5000Chars() throws Exception {
                String contents5000 = "あ".repeat(5000);
                DiaryEntryRequest request = buildRequest("タイトル", contents5000, 60, LocalDate.of(2024, 1, 15));
                DiaryEntryResponse response = buildResponse(5L, "タイトル", 60, LocalDate.of(2024, 1, 15));

                when(diaryEntryService.createDiaryEntry(any(DiaryEntryRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            }

            @Test
            @DisplayName("contentsが5001文字（上限+1）で400が返る")
            void throwsBadRequestWhenContentsExceeds5000Chars() throws Exception {
                String contents5001 = "あ".repeat(5001);
                DiaryEntryRequest request = buildRequest("タイトル", contents5001, 60, LocalDate.of(2024, 1, 15));

                mockMvc.perform(post("/api/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(diaryEntryService, never()).createDiaryEntry(any());
            }
        }
    }

    // ================================
    // updateDiaryEntry
    // ================================

    @Nested
    @DisplayName("updateDiaryEntry")
    class DescribeUpdateDiaryEntry {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("正常な入力で200が返る")
            void shouldReturn200WhenRequestIsValid() throws Exception {
                DiaryEntryRequest request = validRequest();

                doNothing().when(diaryEntryService).updateDiaryEntry(eq(1L), any(DiaryEntryRequest.class));

                mockMvc.perform(put("/api/diary/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk());

                verify(diaryEntryService, times(1)).updateDiaryEntry(eq(1L), any(DiaryEntryRequest.class));
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("ServiceがResourceNotFoundExceptionをスローしたとき404が返る")
            void throwsNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception {
                DiaryEntryRequest request = validRequest();

                doThrow(new ResourceNotFoundException(999L))
                        .when(diaryEntryService).updateDiaryEntry(eq(999L), any(DiaryEntryRequest.class));

                mockMvc.perform(put("/api/diary/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isNotFound());
            }

            @Test
            @DisplayName("リクエストのtitleがブランクで400が返る")
            void throwsBadRequestWhenTitleIsBlank() throws Exception {
                DiaryEntryRequest request = buildRequest("", "内容", 60, LocalDate.of(2024, 1, 15));

                mockMvc.perform(put("/api/diary/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

                verify(diaryEntryService, never()).updateDiaryEntry(any(), any());
            }
        }
    }

    // ================================
    // deleteDiaryEntry
    // ================================

    @Nested
    @DisplayName("deleteDiaryEntry")
    class DescribeDeleteDiaryEntry {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("正常なIDで204が返る")
            void shouldReturn204WhenDeletionSucceeds() throws Exception {
                doNothing().when(diaryEntryService).deleteDiaryEntry(1L);

                mockMvc.perform(delete("/api/diary/1"))
                        .andExpect(status().isNoContent());

                verify(diaryEntryService, times(1)).deleteDiaryEntry(1L);
            }
        }

        @Nested
        @DisplayName("異常系")
        class WhenInvalid {

            @Test
            @DisplayName("ServiceがResourceNotFoundExceptionをスローしたとき404が返る")
            void throwsNotFoundWhenServiceThrowsResourceNotFoundException() throws Exception {
                doThrow(new ResourceNotFoundException(999L))
                        .when(diaryEntryService).deleteDiaryEntry(999L);

                mockMvc.perform(delete("/api/diary/999"))
                        .andExpect(status().isNotFound());
            }
        }
    }
}