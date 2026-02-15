package com.example.engineary.dto;

import java.time.LocalDateTime;

/**
 * 例外用レスポンスクラス<br>
 * field:<br>
 * String errorCode<br>
 * String message<br>
 * LocalDateTime timestamp<br>
 */
public class ErrorResponse {
    private final String errorCode;
    private final String message;
    private final LocalDateTime timestamp;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}