package com.github.wangji92.common;

import lombok.Data;

/**
 * @author 汪小哥
 * @date 16-05-2021
 */
@Data
public class PageResult<T> extends BaseResult<T> {

    /**
     * 总记录条数
     */
    private Long total;

    /**
     * @param code
     * @param success
     * @param msg
     * @param data
     * @param total
     */
    protected PageResult(Integer code, Boolean success, String msg, T data, Long total) {
        super(code, success, msg, data);
        setTotal(total);
    }

    /**
     * 成功
     *
     * @param data
     * @param total
     * @param <T>
     * @return
     */
    public static <T> PageResult<T> success(T data, Long total) {
        return new PageResult<T>(OK, true, "success", data, total);
    }

    /**
     * 失败
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> PageResult<T> error(String msg) {
        return new PageResult<T>(BAD_REQUEST, false, msg, null, 0L);
    }


}
