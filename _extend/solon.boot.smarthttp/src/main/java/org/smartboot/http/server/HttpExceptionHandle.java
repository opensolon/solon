/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpExceptionHandle.java
 * Date: 2020-06-23
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.HttpRequest;
import org.smartboot.http.HttpResponse;
import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.exception.HttpException;
import org.smartboot.http.server.handle.HttpHandle;

import java.io.IOException;

/**
 * Http异常统一处理
 *
 * @author 三刀
 * @version V1.0 , 2020/6/23
 */
public class HttpExceptionHandle extends HttpHandle {
    @Override
    public void doHandle(HttpRequest request, HttpResponse response) throws IOException {
        try {
            doNext(request, response);
        } catch (HttpException e) {
            e.printStackTrace();
            response.setHttpStatus(HttpStatus.valueOf(e.getHttpCode()));
            response.getOutputStream().write(e.getDesc().getBytes());
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.getOutputStream().write(e.fillInStackTrace().toString().getBytes());
            response.close();
        }
    }
}
