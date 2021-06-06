package org.noear.nami;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 代理调用结果
 *
 * @author noear
 * @since 1.2
 * */
public class Result {
    /**
     * 状态码
     * */
    private int code;
    /**
     * 头信息
     * */
    private List<Map.Entry<String,String>> headers;
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
        this.charset = StandardCharsets.UTF_8;
        this.body = body;
    }

    //////////////////
    //////////////////
    //////////////////


    public void headerAdd(String name, String value) {
        headers.add(new AbstractMap.SimpleEntry<>(name,value));
    }

    public String headerGet(String name){
        if(name != null) {
            for (Map.Entry<String, String> kv : headers) {
                if (name.equals(kv.getKey())) {
                    return kv.getValue();
                }
            }
        }

        return null;
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
    public Iterable<Map.Entry<String, String>> headers(){
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
