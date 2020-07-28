package org.noear.solonclient;

import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Result {
    public int code;
    public List<Map.Entry<String, String>> headers;
    public byte[] body;
    public Charset charset;
    private String body_string;

    public Result() {
        headers = new ArrayList<>();
    }

    public Result(Charset charset, byte[] body) {
        this();
        this.charset = charset;
        this.body = body;
    }

    public void headerAdd(String name, String value) {
        headers.add(new AbstractMap.SimpleEntry<>(name, value));
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
