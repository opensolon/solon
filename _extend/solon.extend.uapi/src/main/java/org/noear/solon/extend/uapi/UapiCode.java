package org.noear.solon.extend.uapi;

import org.noear.solon.core.DataThrowable;

/**
 * 接口代码
 *
 * 通过返回或抛出两种形式传递
 * */
public class UapiCode extends DataThrowable {
    private int detailCode;

    public int getCode() {
        return detailCode;
    }

    public UapiCode(int code) {
        super(code + "");
        detailCode = code;
    }

    public UapiCode(int code, String message) {
        super(code + ": " + message);
        detailCode = code;
    }

    public UapiCode(Throwable cause) {
        super(cause);
    }
}
