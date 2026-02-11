package com.example.engineary.dto;

import java.util.List;
/**
 * バリデーション例外用レスポンスクラス<br>
 * バリデーション作成用のデータ型
 */
public class ValidationErrorResponse {

    private final String errorCode;
    private final List<FieldValidationError> errors;

    public ValidationErrorResponse(String errorCode,
            List<FieldValidationError> errors) {
        this.errorCode = errorCode;
        this.errors = errors;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public List<FieldValidationError> getErrors() {
        return errors;
    }
}