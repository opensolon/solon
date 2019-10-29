/*
 * Copyright (c) 2018, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: HttpResponse.java
 * Date: 2018-02-03
 * Author: sandao
 */

package org.smartboot.http;

import org.smartboot.http.enums.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author 三刀
 * @version V1.0 , 2018/2/3
 */
public interface HttpResponse {


    OutputStream getOutputStream();

    HttpStatus getHttpStatus();

    void setHttpStatus(HttpStatus httpStatus);

    void setHeader(String name, String value);

    String getHeader(String name);

    Map<String, String> getHeaders();

    void setContentLength(int contentLength);

    void setContentType(String contentType);

    void write(byte[] data) throws IOException;
}
