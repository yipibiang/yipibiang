package com.example.userservice.infrastructure.config;

/**
 * 统一响应结果类
 */
public class ApiResult<T> {

    private int code;
    private String message;
    private T data;

    /**
     * 无参构造函数（用于反射和序列化）
     */
    public ApiResult() {
    }

    private ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "OK", data);
    }

    /**
     * 成功响应（带消息）
     */
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    /**
     * 参数错误
     */
    public static <T> ApiResult<T> badRequest(String message) {
        return new ApiResult<>(400, message, null);
    }

    /**
     * 未授权
     */
    public static <T> ApiResult<T> unauthorized(String message) {
        return new ApiResult<>(401, message, null);
    }

    /**
     * 资源不存在
     */
    public static <T> ApiResult<T> notFound(String message) {
        return new ApiResult<>(404, message, null);
    }

    /**
     * 服务器错误
     */
    public static <T> ApiResult<T> serverError(String message) {
        return new ApiResult<>(500, message, null);
    }

    /**
     * 失败响应（兼容方法）
     */
    public static <T> ApiResult<T> fail(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    // Getters and Setters
    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}