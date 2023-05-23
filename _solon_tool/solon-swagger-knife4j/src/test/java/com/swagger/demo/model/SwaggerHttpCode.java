package com.swagger.demo.model;

import io.swagger.annotations.ApiModel;

import java.util.HashMap;
import java.util.Map;

@ApiModel(description = "响应状态")
public class SwaggerHttpCode {

    public SwaggerHttpCode() {
        Map<Integer,String> map = new HashMap();
        map.put(200, "请求成功");
        map.put(400, "服务器不理解请求的语法");
        map.put(403, "服务器拒绝请求");
        map.put(404, "服务器找不到请求的网页");
        map.put(405, "禁用请求中指定的方法");
        map.put(500, "服务器遇到错误，无法完成请求");

        this.httpCodes = map;
    }

    private Map<Integer,String> httpCodes;

    public Map<Integer,String> getHttpCodes() {
        return httpCodes;
    }

}
