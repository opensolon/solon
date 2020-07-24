package org.noear.solon.extend.uapi;

/**
 *
 *
 *
 * */
public class UApiError extends RuntimeException {
    private int detailCode;

    public int getCode() {
        return detailCode;
    }

    public UApiError(int code) {
        detailCode = code;
    }

    public UApiError(int code, String message) {
        super(message);
        detailCode = code;
    }

    public UApiError(Throwable cause) {
        super(cause);
    }
}
