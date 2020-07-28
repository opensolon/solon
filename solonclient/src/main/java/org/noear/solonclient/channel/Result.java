package org.noear.solonclient.channel;

import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Result {
    public int code;
    public List<Map.Entry<String,String>> headers;
    public byte[] body;
    public Charset charset;

    public Result(){
        headers = new ArrayList<>();
    }

    public Result(Charset charset, byte[] body){
        this();
        this.charset = charset;
        this.body = body;
    }

    public void headerAdd(String name, String value){
        headers.add(new AbstractMap.SimpleEntry<>(name,value));
    }

    public String bodyAsString(){
        if(charset == null) {
            return new String(body);
        }else{
            return new String(body, charset);
        }
    }
}
