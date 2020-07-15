package org.noear.solon.extend.uapi;

import java.io.Serializable;

/**
 * UAPI结果
 * */
public class Result<T> implements Serializable {
    public int code = 1;
    public String message;
    public T data;

    public Result() {

    }

    public Result(int code, String msg) {
        this(code, msg, null);
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }
}
