package org.noear.solon.core;


import java.io.Serializable;

/**
 * 结果代码
 *
 * */
public class XResultCode extends RuntimeException implements Serializable {
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

    public XResultCode(int code) {
        super(code + ""); //给异常系统用的
        this.code = code;
    }

    public XResultCode(int code, String description) {
        super(code + ": " + description);//给异常系统用的
        this.code = code;
        this.description = description;
    }

    public XResultCode(Throwable cause) {
        super(cause);
        this.description = cause.getMessage();
    }
}
