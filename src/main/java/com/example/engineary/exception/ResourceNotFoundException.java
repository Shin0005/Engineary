package com.example.engineary.exception;

/**
 * ResourceNotFound例外クラス<br>
 * リソースが存在しなかった場合の例外
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(Long id) {
        super("Resource not found. id=" + id);
    }
}
