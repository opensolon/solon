package org.noear.solon.core;


/**
 * 错误代码抛出
 *
 * */
public class CodeThrowable extends RuntimeException{
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

    public CodeThrowable(int code) {
        super(code + ""); //给异常系统用的
        this.code = code;
    }

    public CodeThrowable(int code, String description) {
        super(code + ": " + description);//给异常系统用的
        this.code = code;
        this.description = description;
    }

    public CodeThrowable(Throwable cause) {
        super(cause);
        this.description = cause.getMessage();
    }
}
