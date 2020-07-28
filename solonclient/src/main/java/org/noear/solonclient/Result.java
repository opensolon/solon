package org.noear.solonclient;

import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private List<Map.Entry<String, String>> headers;
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
        headers = new ArrayList<>();
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


    public void headerAdd(String name, String value) {
        headers.add(new AbstractMap.SimpleEntry<>(name, value));
    }

    public int code() {
        return code;
    }

    public Charset charset() {
        return charset;
    }
    public void charsetSet(Charset charset){
        this.charset = charset;
    }

    public byte[] body() {
        return body;
    }

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
