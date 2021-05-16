package com.github.wangji92.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 基本返回参数
 *
 * @author 汪小哥
 * @date 16-05-2021
 */
@Data
public class BaseResult<T> implements Serializable {

    public static final int OK = 200;

    public static final int BAD_REQUEST = 400;

    protected Integer code;
    /**
     * 是否成功
     */
    protected Boolean success;
    /**
     * 成功消息，或者错误消息
     */
    protected String msg;
    /**
     * 成功时返回的数据
     */
    protected T data;


    protected BaseResult(Integer code, Boolean success, String msg, T data) {
        this.code = code;
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    private static <T> BaseResult<T> newInstance(Integer code, Boolean success, String msg, T data) {
        return new BaseResult<>(code, success, msg, data);
    }

    public static <T> BaseResult<T> success(Integer code, String msg, T data) {
        return BaseResult.newInstance(code, true, msg, data);
    }

    public static <T> BaseResult<T> success(String msg, T data) {
        return BaseResult.newInstance(OK, true, msg, data);
    }

    public static <T> BaseResult<T> success(T data) {
        return BaseResult.newInstance(OK, true, "success", data);
    }

    public static <T> BaseResult<T> success() {
        return BaseResult.newInstance(OK, true, "success", null);
    }


    public static <T> BaseResult<T> fail(String msg, T error) {
        return BaseResult.newInstance(BAD_REQUEST, false, msg, error);
    }
}