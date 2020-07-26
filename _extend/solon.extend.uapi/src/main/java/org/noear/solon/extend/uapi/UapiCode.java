package org.noear.solon.extend.uapi;

import org.noear.solon.core.DataThrowable;

/**
 * 接口代码
 *
 * 通过返回或抛出两种形式传递
 * */
public class UapiCode extends DataThrowable {
    private int code;
    private String description = "";

    /**
     * 代码
     * */
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 描述
     * */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UapiCode(int code) {
        super(code + ""); //给异常系统用的
        this.code = code;
    }

    public UapiCode(int code, String description) {
        super(code + ": " + description);//给异常系统用的
        this.code = code;
        this.description = description;
    }

    public UapiCode(Throwable cause) {
        super(cause);
        this.description = cause.getMessage();
    }
}
