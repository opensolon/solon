/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: RouteHandle.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.handle;

import org.smartboot.http.HttpRequest;
import org.smartboot.http.HttpResponse;
import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.utils.AntPathMatcher;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 三刀
 * @version V1.0 , 2018/3/24
 */
public final class HttpRouteHandle extends HttpHandle {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private Map<String, HttpHandle> handleMap = new ConcurrentHashMap<>();

    /**
     * 默认404
     */
    private HttpHandle defaultHandle = new HttpHandle() {
        @Override
        public void doHandle(HttpRequest request, HttpResponse response) throws IOException {
            response.setHttpStatus(HttpStatus.NOT_FOUND);
        }
    };

    @Override
    public void doHandle(HttpRequest request, HttpResponse response) throws IOException {
        String uri = request.getRequestURI();
        HttpHandle httpHandle = handleMap.get(uri);
        if (httpHandle == null) {
            for (Map.Entry<String, HttpHandle> entity : handleMap.entrySet()) {
                if (PATH_MATCHER.match(entity.getKey(), uri)) {
                    httpHandle = entity.getValue();
                    break;
                }
            }
            if (httpHandle == null) {
                httpHandle = defaultHandle;
            }
            handleMap.put(uri, httpHandle);
        }

        httpHandle.doHandle(request, response);
    }

    /**
     * 配置URL路由
     *
     * @param urlPattern url匹配
     * @param httpHandle 处理handle
     * @return
     */
    public HttpRouteHandle route(String urlPattern, HttpHandle httpHandle) {
        handleMap.put(urlPattern, httpHandle);
        return this;
    }

}
