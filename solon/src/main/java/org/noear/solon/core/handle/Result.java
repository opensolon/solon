package org.noear.solon.core.handle;

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
 *     public Result api1(){
 *         return Result.SUCCEED;
 *     }
 *
 *     @Mapping("A.0.2")
 *     public Result api2(){
 *         return Result.succeed(12);
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public class Result<T> implements Serializable {
    public static int SUCCEED_CODE = 200;
    public static int FAILURE_CODE = 400;

    private static final Result SUCCEED = new ResultReadonly(SUCCEED_CODE,"");
    private static final Result FAILURE = new ResultReadonly(FAILURE_CODE,"");

    /**
     * 状态码
     * <p>
     * 400xxxx:明确的失败
     * 400:未知失败
     * 200:成功
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
        this.code = SUCCEED_CODE;
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
     * 只读 Result
     * */
    static class ResultReadonly<T> extends Result<T> {
        public ResultReadonly(T data) {
            super(data);
        }

        public ResultReadonly(int code, String description) {
            super(code, description);
        }

        @Override
        public void setCode(int code) {
            throw new RuntimeException("This result is readonly!");
        }

        @Override
        public void setData(T data) {
            throw new RuntimeException("This result is readonly!");
        }

        @Override
        public void setDescription(String description) {
            throw new RuntimeException("This result is readonly!");
        }
    }
}
