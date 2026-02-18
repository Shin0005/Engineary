package com.example.engineary.exception;

/**
 * 業務例外クラス<br>
 * 業務の例外をまとめる抽象クラス
 */
public abstract class BusinessException extends RuntimeException {

    protected BusinessException(String message) {
        super(message);
    }
}
