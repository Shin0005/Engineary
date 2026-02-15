package com.example.engineary.exception;

import com.example.engineary.dto.ErrorResponse;
import com.example.engineary.dto.FieldValidationError;
import com.example.engineary.dto.ValidationErrorResponse;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

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
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
                log.warn("Resource not found: {}", ex.getMessage());

                ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage());

                // 404
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        // 業務エラー
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
                log.warn("Request is wrong: {}", ex.getMessage());

                ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage());

                // 400
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        // バリデーション例外
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ValidationErrorResponse> handleValidationException(
                        MethodArgumentNotValidException ex) {

                log.warn("Validation Failed: {}", ex.getMessage());
                // 発生したバリデーションエラーを取得しリスト化
                List<FieldValidationError> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> new FieldValidationError(
                                                error.getField(),
                                                error.getDefaultMessage()))
                                .toList();

                ValidationErrorResponse response = new ValidationErrorResponse("VALIDATION_ERROR", errors);

                // 400
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        // 不正なURIの時
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ErrorResponse> handleUriNotFoundException(NoHandlerFoundException ex) {
                log.warn("URI not found: {}", ex.getMessage());

                ErrorResponse response = new ErrorResponse("NOT_FOUND", "URL not found");
                // 404
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        // リクエストのデータ型が不正な場合(jacksonでの例外をキャッチ)
        // bodyがnullのとき、bodyがrequestにない時をキャッチ
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException ex) {
                log.warn("Invalid request body: {}", ex.getMessage());

                ErrorResponse response = new ErrorResponse("INVALID_REQUEST", "入力形式が不正です");
                // 400
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        // どれにも該当しない場合
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleSystemException(Exception ex) {
                log.error("Unexpected error occurred", ex);

                ErrorResponse response = new ErrorResponse("SYSTEM_ERROR", "Unexpected error occurred");

                // 500
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }
}