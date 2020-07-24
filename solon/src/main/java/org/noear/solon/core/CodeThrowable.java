package org.noear.solon.core;

public class CodeThrowable extends RuntimeException {
    private int detailCode;

    public int getCode() {
        return detailCode;
    }

    public CodeThrowable(int code) {
        detailCode = code;
    }

    public CodeThrowable(Throwable cause) {
        super(cause);
    }
}
