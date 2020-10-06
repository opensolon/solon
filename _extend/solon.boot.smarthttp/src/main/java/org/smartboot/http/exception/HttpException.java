/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpException.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.exception;

import org.smartboot.http.enums.HttpStatus;

/**
 * @author 三刀
 * @version V1.0 , 2018/6/3
 */
public class HttpException extends RuntimeException {
    private int httpCode;

    private String desc;

    public HttpException(HttpStatus httpStatus) {
        this.httpCode = httpStatus.value();
        this.desc = httpStatus.getReasonPhrase();
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
