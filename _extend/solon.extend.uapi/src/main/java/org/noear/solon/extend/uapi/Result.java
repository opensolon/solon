package org.noear.solon.extend.uapi;

/**
 * 结果
 * */
public class Result<T> {
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
        this.data = data;
        this.description = "";
    }

    public Result(int code, String description) {
        this.code = code;
        if (description == null) {
            this.description = "";
        } else {
            this.description = description;
        }
    }

    /**
     * 成功的结果
     */
    public static <T> Result<T> succeed(T data) {
        return new Result<>(data);
    }


    /**
     * 失败的结果
     */
    public static <T> Result<T> failure(UapiCode code) {
        return failure(code.getCode(), code.getDescription());
    }

    /**
     * 失败的结果
     */
    public static <T> Result<T> failure(int code) {
        return failure(code, "");
    }

    /**
     * 失败的结果
     */
    public static <T> Result<T> failure(int code, String description) {
        return new Result<>(code, description);
    }
}
