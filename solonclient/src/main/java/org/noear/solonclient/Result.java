package org.noear.solonclient;

import java.nio.charset.Charset;
import java.util.*;

/**
 * 代理调用结果
 * */
public class Result {
    /**
     * 状态码
     * */
    private int code;
    /**
     * 头信息
     * */
    private Map<String,String> headers;
    /**
     * 编码
     * */
    private Charset charset;
    /**
     * 返回体
     * */
    private byte[] body;
    /**
     * 返回体的字符串形式
     * */
    private String body_string;

    public Result() {
        headers = new LinkedHashMap<>();
    }

    public Result(Charset charset, byte[] body) {
        this();
        this.charset = charset;
        this.body = body;
    }

    public Result(int code,  byte[] body) {
        this();
        this.code = code;
        this.charset = charset;
        this.body = body;
    }

    //////////////////
    //////////////////
    //////////////////


    public void headerSet(String name, String value) {
        headers.putIfAbsent(name,value);
    }

    public void charsetSet(Charset charset){
        this.charset = charset;
    }

    //////////////////
    //////////////////
    //////////////////

    /**
     * 头信息
     * */
    public Map<String,String> headers(){
        return headers;
    }

    /**
     * 状态码
     * */
    public int code() {
        return code;
    }

    /**
     * 字符集
     * */
    public Charset charset() {
        return charset;
    }

    /**
     * 返回体
     * */
    public byte[] body() {
        return body;
    }

    /**
     * 返回体字符形式
     * */
    public String bodyAsString() {
        if (body_string == null) {
            if (charset == null) {
                body_string = new String(body);
            } else {
                body_string = new String(body, charset);
            }

            //清掉body
            body = null;
        }

        if (code >= 400) {
            throw new RuntimeException(code + "错误：" + body_string);
        } else {
            return body_string;
        }
    }
}
