package com.example.engineary.exception;

/**
 * ResourceNotFound例外クラス<br>
 * リソースが存在しなかった場合の例外
 */
public class RequestIsNullException extends BusinessException {

    public RequestIsNullException() {
        super("REQUEST_IS_NULL", "Request is null");
    }
}
