/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpMethodEnum.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.enums;

/**
 * Http支持的Method
 *
 * @author 三刀
 * @version V1.0 , 2018/2/6
 */
public enum HttpMethodEnum {

    GET("GET"),
    POST("POST"),

    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),

    HEAD("HEAD"),

    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    CONNECT("CONNECT");

    private String method;

    HttpMethodEnum(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
