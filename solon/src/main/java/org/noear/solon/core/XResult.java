package org.noear.solon.core;

import java.io.Serializable;

/**
 * 结果
 * */
public class XResult<T> implements Serializable {
    /**
     * 状态码
     * <p>
     * -x:明确的失败（或正数）
     * 00:未知失败
     * 01:成功
     */
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 状态描述
     */
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null) {
            this.description = "";
        } else {
            this.description = description;
        }
    }

    /**
     * 数据
     */
    private T data;

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public XResult(T data) {
        setCode(1);
        setDescription("");
        setData(data);
    }

    public XResult(int code, String description) {
        setCode(code);
        setDescription(description);
    }

    /**
     * 成功的结果
     */
    public static <T> XResult<T> succeed(T data) {
        return new XResult<>(data);
    }

    /**
     * 失败的结果
     */
    public static <T> XResult<T> failure(int code) {
        return failure(code, "");
    }

    /**
     * 失败的结果
     */
    public static <T> XResult<T> failure(int code, String description) {
        return new XResult<>(code, description);
    }
}
