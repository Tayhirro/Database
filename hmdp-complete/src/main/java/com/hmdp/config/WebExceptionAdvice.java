package com.hmdp.config;

import com.hmdp.dto.Result;
import com.hmdp.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.UUID;

/**
 * 把各处抛出的异常统一转换成前端能理解的 HTTP 错误响应。
 *     1. HTTP 状态码说明错误大类：例如 400 是请求参数有误、401 是未登录、
 *     403 是没有权限、404 是数据不存在、500 是服务器内部异常、503 是数据层暂时不可用。
 *     2. errorCode 供程序判断：前端可以稳定判断 {@code BLOG_NOT_FOUND} 等错误；
 *     errorMsg 只负责显示中文提示，文案变化不会破坏前端逻辑。
 *     3. traceId 用于排查问题：同一个编号同时写入响应和服务端日志。
 *     用户报告错误编号后，开发者可以找到这一次请求对应的日志。
 *     4. 未知异常不返回内部细节：前端只收到通用 500，完整异常堆栈只写服务端日志，
 *     防止把 SQL、文件路径或代码结构泄露出去。
 *
 */
@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result> handleBusinessException(BusinessException e) {
        return error(e.getStatus(), e.getCode(), e.getMessage());
    }

    /**
     * 数据访问层异常（{@link DataAccessException}，Spring 数据访问异常基类，
     * 覆盖 MySQL 读写失败、唯一约束冲突、连接池耗尽等场景）单独归类为 503：
     * 表示服务端数据层暂时不可用，客户端可稍后重试；与代码缺陷导致的 500 区分开。
     * 错误信息固定为通用文案，不返回 SQL 语句、表名或约束名等数据库细节，完整堆栈只写服务端日志。
     * 必须声明在 BusinessException 处理之后：更具体的异常类型优先匹配，BusinessException 的行为不受影响。
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result> handleDataAccessException(DataAccessException e) {
        log.error(e.toString(), e);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "DATA_ACCESS_ERROR", "数据访问异常，请稍后再试");
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Result> handleBadRequest(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "请求参数格式错误");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "HTTP 方法不受支持");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result> handleUploadTooLarge(MaxUploadSizeExceededException e) {
        return error(HttpStatus.PAYLOAD_TOO_LARGE, "UPLOAD_TOO_LARGE", "上传文件超出大小限制");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result> handleRuntimeException(RuntimeException e) {
        log.error(e.toString(), e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "服务器异常");
    }

    private ResponseEntity<Result> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(Result.fail(code, message, currentTraceId()));
    }

    private String currentTraceId() {
        String traceId = MDC.get("traceId");
        return traceId == null ? UUID.randomUUID().toString().replace("-", "") : traceId;
    }
}
