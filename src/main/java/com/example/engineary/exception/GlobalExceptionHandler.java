package com.example.engineary.exception;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * 例外ハンドルクラス<br>
 * 発生した例外によってレスポンスをapi呼び出し元に送信
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
        // ロガー
        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        // DBにリソースがない場合
        @ExceptionHandler(ResourceNotFoundException.class)
        public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
                log.warn("Resource not found: {}", ex.getMessage());

                return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "指定されたリソースが見つかりませんでした。");
        }

        // バリデーション例外
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {

                // "Validation failed for argument []...
                log.warn(ex.getMessage());

                // 400
                ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                "リクエストの形式が正しくありません");

                // 発生したバリデーションエラーを取得しリストしてdetailに保存
                List<Map<String, String>> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> Map.of(
                                                "field", error.getField(),
                                                "reason", error.getDefaultMessage()))
                                .toList();

                detail.setProperty("errors", errors);
                return detail;
        }

        // JSONパースエラー
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
                log.warn("Unreadable request: {}", ex.getMessage());

                // 400
                return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "リクエストの形式が正しくありません");
        }

        // 標準的なhttpStatusを持つ例外を自動ハンドル（ResponseStatusExceptionを継承した例外）
        @ExceptionHandler(ResponseStatusException.class)
        public ProblemDetail handleResponseStatusException(ResponseStatusException ex) {
                log.warn(ex.getMessage());

                String msg;
                switch (ex.getStatusCode().value()) {
                        case 400:
                                msg = "リクエストの形式が正しくありません";
                                break;
                        case 401:
                                msg = "認証が必要です";
                                break;
                        case 403:
                                msg = "アクセス権限がありません";
                                break;
                        case 404:
                                msg = "リソースが見つかりません";
                                break;
                        default:
                                msg = "エラーが発生しました";
                }
                ;
                // 例外が持っているステータスコードをそのまま利用
                return ProblemDetail.forStatusAndDetail(ex.getStatusCode(), msg);
        }

        // 予期せぬ例外（500）
        @ExceptionHandler(Exception.class)
        public ProblemDetail handleSystemException(Exception ex) {
                // stacktraceを出す
                log.error("Unexpected system error", ex);

                return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                "予期しないエラーが発生しました。");
        }
}