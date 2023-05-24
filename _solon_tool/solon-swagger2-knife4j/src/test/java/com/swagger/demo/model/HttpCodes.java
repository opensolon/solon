package com.swagger.demo.model;

import java.util.LinkedHashMap;

public class HttpCodes extends LinkedHashMap<Integer,String> {

    public HttpCodes() {
        super();
        put(200, "请求成功");
        put(400, "服务器不理解请求的语法");
        put(403, "服务器拒绝请求");
        put(404, "服务器找不到请求的网页");
        put(405, "禁用请求中指定的方法");
        put(500, "服务器遇到错误，无法完成请求");
    }
}
