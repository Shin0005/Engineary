package com.example.engineary.dto;

/**
 * バリデーション例外用データクラス<br>
 * バリデーション作成用のデータ型
 */
public class FieldValidationError {
    /** 違反したフィールド */
    private final String field;
    /** 違反理由 */
    private final String message;

    public FieldValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}