package com.example.engineary.exception;

import com.example.engineary.dto.ErrorResponse;
import com.example.engineary.dto.FieldValidationError;
import com.example.engineary.dto.ValidationErrorResponse;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 例外ハンドルクラス<br>
 * 発生した例外によってレスポンスをapi呼び出し元に送信
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 業務エラー
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {

        ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage());

        // 404
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {

        ErrorResponse response = new ErrorResponse(ex.getErrorCode(), ex.getMessage());

        // 400
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // バリデーション例外
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

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
    @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<?> handle404(NoHandlerFoundException ex) {

        ErrorResponse response = new ErrorResponse("NOT_FOUND", "URL not found");        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
        }


    
    // どれにも該当しない場合
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleSystemException(Exception ex) {

        ErrorResponse response = new ErrorResponse("SYSTEM_ERROR", "Unexpected error occurred");

        // 500
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}