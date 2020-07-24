package org.noear.solon.extend.uapi;

public class UApiException extends RuntimeException {
    public int code;

    public UApiException(int code) {
        this.code = code;
    }

    public UApiException(Throwable cause) {
        super(cause);
    }
}
