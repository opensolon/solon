package org.noear.solon.extend.uapi;

import org.noear.solon.core.DataThrowable;

/**
 * 接口代码
 *
 * 通过返回或抛出两种形式传递
 * */
public class UapiCode extends RuntimeException implements DataThrowable {
    private int detailCode;

    public int getCode() {
        return detailCode;
    }

    public UapiCode(int code) {
        detailCode = code;
    }

    public UapiCode(int code, String message) {
        super(message);
        detailCode = code;
    }

    public UapiCode(Throwable cause) {
        super(cause);
    }
}
