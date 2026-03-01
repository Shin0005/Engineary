package com.example.engineary.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.engineary.dto.DiaryEntryRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * GlobalExceptionHandler テストクラス
 *
 * 各例外ハンドラが正しいHTTPステータスとProblemDetailを返すことを検証する。
 * Jackson非依存とするためObjectMapperは使用せず、JSON文字列をテキストブロックで直書きする。
 * Hamcrest(hasSize等)も使用せず、JSONPathのlength()とexists()で代替する。
 */
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    // ===================================================================
    // テスト用コントローラ
    // 各エンドポイントが特定の例外をスローし、ハンドラを発火させる
    // ===================================================================
    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/resource-not-found")
        public void throwResourceNotFoundException() {
            throw new ResourceNotFoundException(1L);
        }

        @PostMapping("/validation")
        public void throwValidationException(
                @RequestBody @jakarta.validation.Valid DiaryEntryRequest request) {
        }

        @GetMapping("/response-status/400")
        public void throwResponseStatus400() {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        @GetMapping("/response-status/401")
        public void throwResponseStatus401() {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        @GetMapping("/response-status/403")
        public void throwResponseStatus403() {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        @GetMapping("/response-status/404")
        public void throwResponseStatus404() {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 500はhandleSystemExceptionと競合するため、switchのdefaultを検証するには
        // caseに存在しない409（Conflict）を使用する
        @GetMapping("/response-status/409")
        public void throwResponseStatus409() {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        @GetMapping("/system-error")
        public void throwUnexpectedException() throws Exception {
            throw new Exception("unexpected system error");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ===================================================================
    // handleResourceNotFoundException
    // ===================================================================
    @Nested
    @DisplayName("handleResourceNotFoundException")
    class DescribeHandleResourceNotFoundException {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("ResourceNotFoundExceptionが発生した場合、404と固定メッセージを返す")
            void shouldReturn404WhenResourceNotFound() throws Exception {
                mockMvc.perform(get("/test/resource-not-found"))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.detail").value("指定されたリソースが見つかりませんでした。"));
            }
        }
    }

    // ===================================================================
    // handleValidationException
    // ===================================================================
    @Nested
    @DisplayName("handleValidationException")
    class DescribeHandleValidationException {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("バリデーション違反が1件の場合、400・errors件数が厳密に1件・field/reasonキーを返す")
            void shouldReturn400WithSingleErrorContainingFieldAndReason() throws Exception {
                // title="" は @NotBlank 違反（1件のみ）
                String json = """
                        {
                          "title": "",
                          "workedTime": 30,
                          "workedDate": "2025-01-01"
                        }
                        """;

                mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.detail").value("リクエストが正しくありません"))
                        .andExpect(jsonPath("$.errors").isArray())
                        .andExpect(jsonPath("$.errors.length()").value(1)) // 厳密に1件
                        .andExpect(jsonPath("$.errors[0].field").value("title")) // fieldキー確認
                        .andExpect(jsonPath("$.errors[0].reason").exists()); // reasonキー確認
            }

            @Test
            @DisplayName("バリデーション違反が複数件の場合、errorsに複数件含まれる")
            void shouldReturn400WithMultipleErrorsWhenMultipleValidationsFail() throws Exception {
                // title="" は @NotBlank 違反、workedTimeなしは @NotNull 違反
                String json = """
                        {
                          "title": "",
                          "workedDate": "2025-01-01"
                        }
                        """;

                mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isBadRequest())
                        // インデックス[1]が存在することで2件以上を確認（Hamcrest不使用）
                        .andExpect(jsonPath("$.errors[1]").exists());
            }
        }
    }

    // ===================================================================
    // handleHttpMessageNotReadableException
    // ===================================================================
    @Nested
    @DisplayName("handleHttpMessageNotReadableException")
    class DescribeHandleHttpMessageNotReadableException {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("不正なJSONが送られた場合、400とエラーメッセージを返す")
            void shouldReturn400WhenJsonIsMalformed() throws Exception {
                mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.detail").value("リクエストの形式が正しくありません"));
            }
        }
    }

    // ===================================================================
    // handleResponseStatusException
    // ===================================================================
    @Nested
    @DisplayName("handleResponseStatusException")
    class DescribeHandleResponseStatusException {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("400のResponseStatusExceptionで「不正なリクエストです」を返す")
            void shouldReturn400WithCorrectMessage() throws Exception {
                mockMvc.perform(get("/test/response-status/400"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.detail").value("不正なリクエストです"));
            }

            @Test
            @DisplayName("401のResponseStatusExceptionで「認証が必要です」を返す")
            void shouldReturn401WithCorrectMessage() throws Exception {
                mockMvc.perform(get("/test/response-status/401"))
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.detail").value("認証が必要です"));
            }

            @Test
            @DisplayName("403のResponseStatusExceptionで「アクセス権限がありません」を返す")
            void shouldReturn403WithCorrectMessage() throws Exception {
                mockMvc.perform(get("/test/response-status/403"))
                        .andExpect(status().isForbidden())
                        .andExpect(jsonPath("$.detail").value("アクセス権限がありません"));
            }

            @Test
            @DisplayName("404のResponseStatusExceptionで「リソースが見つかりません」を返す")
            void shouldReturn404WithCorrectMessage() throws Exception {
                mockMvc.perform(get("/test/response-status/404"))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.detail").value("リソースが見つかりません"));
            }

            @Test
            @DisplayName("switchに未定義のステータス(409)でdefaultの「エラーが発生しました」を返す")
            void shouldReturnDefaultMessageForUnmappedStatus() throws Exception {
                // 500はhandleSystemExceptionと競合するため409で検証する
                mockMvc.perform(get("/test/response-status/409"))
                        .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.detail").value("エラーが発生しました"));
            }
        }
    }

    // ===================================================================
    // handleSystemException
    // ===================================================================
    @Nested
    @DisplayName("handleSystemException")
    class DescribeHandleSystemException {

        @Nested
        @DisplayName("正常系")
        class WhenValid {

            @Test
            @DisplayName("予期せぬExceptionが発生した場合、500とエラーメッセージを返す")
            void shouldReturn500WhenUnexpectedExceptionOccurs() throws Exception {
                mockMvc.perform(get("/test/system-error"))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.detail").value("予期しないエラーが発生しました。"));
            }
        }
    }
}