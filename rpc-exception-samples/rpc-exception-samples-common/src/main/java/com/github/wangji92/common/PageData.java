package com.github.wangji92.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 汪小哥
 * @date 16-05-2021
 */
public class PageData<T> implements Serializable {
    /**
     * 数量
     */
    private int count;

    /**
     * 结果
     */
    private List<T> data;


    public PageData() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public PageData(int count, List<T> data) {
        this.count = count;
        this.data = data;
    }
}
