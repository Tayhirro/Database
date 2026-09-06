package com.hmdp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public BusinessException(String message) {
        this(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", message, null);
    }

    public BusinessException(String message, Throwable cause) {
        this(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", message, cause);
    }

    public BusinessException(HttpStatus status, String code, String message) {
        this(status, code, message, null);
    }

    public BusinessException(HttpStatus status, String code, String message, Throwable cause) {
        super(message, cause);
        this.status = status == null ? HttpStatus.BAD_REQUEST : status;
        this.code = code == null ? "BUSINESS_ERROR" : code;
    }

    public static BusinessException badRequest(String code, String message) {
        return new BusinessException(HttpStatus.BAD_REQUEST, code, message);
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED", message);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
    }

    public static BusinessException notFound(String code, String message) {
        return new BusinessException(HttpStatus.NOT_FOUND, code, message);
    }

    public static BusinessException conflict(String code, String message) {
        return new BusinessException(HttpStatus.CONFLICT, code, message);
    }
}
