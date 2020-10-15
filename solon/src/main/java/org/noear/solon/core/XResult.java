package org.noear.solon.core;

import org.noear.solon.annotation.XNote;

import java.io.Serializable;

/**
 * 结果
 *
 * @author noear
 * @since 1.0
 * */
public class XResult<T> implements Serializable {
    private static final XResult _SUCCEED = new XResultReadonly(null);
    private static final XResult _FAILURE = new XResultReadonly(0,"");

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
        this.code = 1;
        this.description = "";
        this.data = data;
    }

    public XResult(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 成功的空结果
     */
    @XNote("成功的空结果")
    public static <T> XResult<T> succeed() {
        return _SUCCEED;
    }

    /**
     * 成功的结果
     */
    @XNote("成功的结果")
    public static <T> XResult<T> succeed(T data) {
        return new XResult<>(data);
    }

    /**
     * 成功的空结果
     */
    @XNote("失败的空结果")
    public static <T> XResult<T> failure() {
        return _FAILURE;
    }

    /**
     * 失败的结果
     */
    @XNote("失败的结果")
    public static <T> XResult<T> failure(int code) {
        return failure(code, "");
    }

    /**
     * 失败的结果
     */
    @XNote("失败的结果")
    public static <T> XResult<T> failure(int code, String description) {
        return new XResult<>(code, description);
    }

    @XNote("失败的结果")
    public static <T> XResult<T> failure(String description) {
        return new XResult<>(0, description);
    }


    /**
     * 只读 XResult
     * */
    static class XResultReadonly<T> extends XResult<T> {
        public XResultReadonly(T data) {
            super(data);
        }

        public XResultReadonly(int code, String description) {
            super(code, description);
        }

        @Override
        public void setCode(int code) {
            throw new RuntimeException("Thes result is readonly!");
        }

        @Override
        public void setData(T data) {
            throw new RuntimeException("Thes result is readonly!");
        }

        @Override
        public void setDescription(String description) {
            throw new RuntimeException("Thes result is readonly!");
        }
    }
}
