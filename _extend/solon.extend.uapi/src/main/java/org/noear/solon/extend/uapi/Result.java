package org.noear.solon.extend.uapi;

/**
 * 结果
 * */
public class Result<T> {
    /**
     * 状态码
     *
     * -x:明确的失败（或正数）
     * 00:未知失败
     * 01:成功
     */
    public int code;
    /**
     * 状态描述
     */
    public String description;
    /**
     * 数据
     */
    public T data;

    /**
     * 成功的结果
     */
    public static <T> Result<T> succeed(T data) {
        Result<T> result = new Result<>();
        result.code = 1;
        result.data = data;

        return result;
    }

    /**
     * 失败的结果
     */
    public static <T> Result<T> failure(int code) {
        return failure(code, null);
    }

    /**
     * 失败的结果
     * */
    public static <T> Result<T> failure(int code, String description) {
        Result<T> result = new Result<>();
        result.code = 1;
        result.description = description;

        return result;
    }
}
