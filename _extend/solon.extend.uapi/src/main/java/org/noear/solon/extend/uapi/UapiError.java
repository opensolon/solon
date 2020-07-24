package org.noear.solon.extend.uapi;

import org.noear.solon.core.DataThrowable;

public class UapiError extends RuntimeException implements DataThrowable {
    private int detailCode;

    public int getCode() {
        return detailCode;
    }

    public UapiError(int code) {
        detailCode = code;
    }

    public UapiError(int code, String message) {
        super(message);
        detailCode = code;
    }

    public UapiError(Throwable cause) {
        super(cause);
    }
}
