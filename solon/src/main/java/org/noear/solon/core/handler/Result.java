package org.noear.solon.core.handler;

import org.noear.solon.annotation.Note;

import java.io.Serializable;

/**
 * 结果（可用于接口开发返回统一结果）
 *
 * <pre><code>
 * @Mapping("api")
 * @Controller
 * public class DemoController{
 *     @Mapping("A.0.1")
 *     public XResult api1(){
 *         return XResult.SUCCEED;
 *     }
 *
 *     @Mapping("A.0.2")
 *     public XResult api2(){
 *         return XResult.succeed(12);
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public class Result<T> implements Serializable {
    private static final Result SUCCEED = new XResultReadonly(null);
    private static final Result FAILURE = new XResultReadonly(0,"");

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

    public Result(T data) {
        this.code = 1;
        this.description = "";
        this.data = data;
    }

    public Result(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 成功的空结果
     */
    @Note("成功的空结果")
    public static <T> Result<T> succeed() {
        return SUCCEED;
    }

    /**
     * 成功的结果
     */
    @Note("成功的结果")
    public static <T> Result<T> succeed(T data) {
        return new Result<>(data);
    }

    /**
     * 成功的空结果
     */
    @Note("失败的空结果")
    public static <T> Result<T> failure() {
        return FAILURE;
    }

    /**
     * 失败的结果
     */
    @Note("失败的结果")
    public static <T> Result<T> failure(int code) {
        return failure(code, "");
    }

    /**
     * 失败的结果
     */
    @Note("失败的结果")
    public static <T> Result<T> failure(int code, String description) {
        return new Result<>(code, description);
    }

    @Note("失败的结果")
    public static <T> Result<T> failure(String description) {
        return new Result<>(0, description);
    }


    /**
     * 只读 XResult
     * */
    static class XResultReadonly<T> extends Result<T> {
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
