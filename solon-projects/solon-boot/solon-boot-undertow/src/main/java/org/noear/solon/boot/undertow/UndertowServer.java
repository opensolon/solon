/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.boot.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.*;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.boot.undertow.http.UtHttpContextServletHandler;
import org.noear.solon.boot.undertow.websocket.UtWsProtocolHandshakeHandler;
import org.noear.solon.core.event.EventBus;

/**
 * @author  by: Yukai
 * @since : 2019/3/28 15:49
 */
public class UndertowServer extends UndertowServerBase implements ServerLifecycle {
    protected Undertow _server;
    protected boolean isSecure;
    protected boolean enableWebSocket;

    public UndertowServer(HttpServerProps props) {
        super(props);
    }

    public boolean isSecure() {
        return isSecure;
    }

    public void enableWebSocket(boolean enableWebSocket) {
        this.enableWebSocket = enableWebSocket;
    }

    @Override
    public void start(String host, int port) {
        try {
            setup(host, port);

            _server.start();
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }

    protected void setup(String host, int port) throws Throwable {
        HttpHandler httpHandler = buildHandler();

        //************************** init server start******************
        Undertow.Builder builder = Undertow.builder();

        builder.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false);

        if (ServerProps.request_maxHeaderSize > 0) {
            builder.setServerOption(UndertowOptions.MAX_HEADER_SIZE, ServerProps.request_maxHeaderSize);
        }

        if (ServerProps.request_maxBodySize > 0) {
            builder.setServerOption(UndertowOptions.MAX_ENTITY_SIZE, ServerProps.request_maxBodySize);
        }

        if (ServerProps.request_maxFileSize > 0) {
            builder.setServerOption(UndertowOptions.MULTIPART_MAX_ENTITY_SIZE, ServerProps.request_maxFileSize);
        }


        builder.setServerOption(UndertowOptions.IDLE_TIMEOUT, (int) props.getIdleTimeoutOrDefault());
        builder.setIoThreads(props.getCoreThreads());
        builder.setWorkerThreads(props.getMaxThreads(props.isIoBound()));

        if (isEnableHttp2()) {
            builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
        }

        if (Utils.isEmpty(host)) {
            host = "0.0.0.0";
        }

        if (sslConfig.isSslEnable()) {
            //https
            builder.addHttpsListener(port, host, sslConfig.getSslContext());
            isSecure = true;
        } else {
            //http
            builder.addHttpListener(port, host);
        }

        //http add
        for (Integer portAdd : addHttpPorts) {
            builder.addHttpListener(portAdd, host);
        }

        if (enableWebSocket) {
            builder.setHandler(new UtWsProtocolHandshakeHandler(httpHandler));
        } else {
            builder.setHandler(httpHandler);
        }


        //1.1:分发事件（充许外部扩展）
        EventBus.publish(builder);

        _server = builder.build();

        //************************* init server end********************
    }

    protected HttpHandler buildHandler() throws Exception {
        DeploymentInfo builder = initDeploymentInfo();

        //添加servlet
        builder.addServlet(new ServletInfo("ACTServlet", UtHttpContextServletHandler.class).addMapping("/").setAsyncSupported(true));
        //builder.addInnerHandlerChainWrapper(h -> handler); //这个会使过滤器不能使用


        //开始部署
        final ServletContainer container = Servlets.defaultContainer();
        DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();

        return manager.start();
    }
}
