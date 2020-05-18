package org.noear.solon.core;

import java.io.Serializable;

/**
 * 结果
 * */
public class Result<T> implements Serializable {
    public int code;
    public String msg;
    public T data;

    public Result(int code, String msg) {
        this(code, msg, null);
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
