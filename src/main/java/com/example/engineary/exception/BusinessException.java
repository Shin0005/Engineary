package com.example.engineary.exception;

/**
 * 業務例外クラス<br>
 * 業務の例外をまとめる抽象クラス
 */
public abstract class BusinessException extends RuntimeException {

    private final String errorCode;

    protected BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
