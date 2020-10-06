/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: RouteHandle.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.handle;

import org.smartboot.http.WebSocketRequest;
import org.smartboot.http.WebSocketResponse;
import org.smartboot.http.logging.RunLogger;
import org.smartboot.http.utils.AntPathMatcher;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * @author 三刀
 * @version V1.0 , 2018/3/24
 */
public final class WebSocketRouteHandle extends WebSocketHandle {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private Map<String, WebSocketHandle> handleMap = new ConcurrentHashMap<>();

    /**
     * 默认404
     */
    private WebSocketHandle defaultHandle = new WebSocketHandle() {
        @Override
        public void doHandle(WebSocketRequest request, WebSocketResponse response) throws IOException {
            RunLogger.getLogger().log(Level.WARNING, "not found");
        }
    };

    @Override
    public void doHandle(WebSocketRequest request, WebSocketResponse response) throws IOException {
        String uri = request.getRequestURI();
        WebSocketHandle httpHandle = handleMap.get(uri);
        if (httpHandle == null) {
            for (Map.Entry<String, WebSocketHandle> entity : handleMap.entrySet()) {
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
    public WebSocketRouteHandle route(String urlPattern, WebSocketHandle httpHandle) {
        handleMap.put(urlPattern, httpHandle);
        return this;
    }
}
