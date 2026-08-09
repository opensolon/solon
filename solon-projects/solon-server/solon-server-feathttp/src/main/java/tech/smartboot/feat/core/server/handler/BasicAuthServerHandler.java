/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.handler;

import tech.smartboot.feat.core.common.FeatUtils;
import tech.smartboot.feat.core.common.HeaderName;
import tech.smartboot.feat.core.common.HttpStatus;
import tech.smartboot.feat.core.server.HttpHandler;
import tech.smartboot.feat.core.server.HttpRequest;
import tech.smartboot.feat.core.server.HttpResponse;
import tech.smartboot.feat.core.server.impl.HttpEndpoint;

import java.io.IOException;
import java.util.Base64;

/**
 * Basic认证服务处理器
 * 
 * <p>实现HTTP Basic认证机制，验证客户端请求的Authorization头信息</p>
 * 
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 * @since 1.0.0
 */
public final class BasicAuthServerHandler implements HttpHandler {
    /**
     * 实际业务处理器
     */
    private final HttpHandler httpServerHandler;
    
    /**
     * 预计算的Basic认证字符串
     */
    private final String basic;

    /**
     * 构造函数
     * 
     * @param username Basic认证用户名
     * @param password Basic认证密码
     * @param httpServerHandler 认证通过后实际执行的业务处理器
     */
    public BasicAuthServerHandler(String username, String password, HttpHandler httpServerHandler) {
        this.httpServerHandler = httpServerHandler;
        basic = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    /**
     * 处理HTTP请求头完成事件
     * 
     * @param request HTTP请求对象
     * @throws IOException 如果发生I/O错误
     */
    @Override
    public void onHeaderComplete(HttpEndpoint request) throws IOException {
        String clientBasic = request.getHeader(HeaderName.AUTHORIZATION);
        if (FeatUtils.equals(clientBasic, this.basic)) {
            // 认证通过，执行实际业务处理
            httpServerHandler.onHeaderComplete(request);
        } else {
            // 认证失败，返回401 Unauthorized
            HttpResponse response = request.getResponse();
            response.setHeader(HeaderName.WWW_AUTHENTICATE, "Basic realm=\"feat\"");
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.close();
        }
    }

    @Override
    public void onClose(HttpEndpoint request) {
        httpServerHandler.onClose(request);
    }

    @Override
    public void handle(HttpRequest request) throws Throwable {
        httpServerHandler.handle(request);
    }

}
