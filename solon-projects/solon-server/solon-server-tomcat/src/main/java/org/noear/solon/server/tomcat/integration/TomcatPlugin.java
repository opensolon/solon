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
package org.noear.solon.server.tomcat.integration;

import org.apache.catalina.util.ServerInfo;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.util.ClassUtil;

import org.noear.solon.server.tomcat.TomcatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

public final class TomcatPlugin implements Plugin {
    static final Logger log = LoggerFactory.getLogger(TomcatPlugin.class);

    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    private ServerLifecycle _server = null;

    public static String solon_server_ver() {
    	return ServerInfo.getServerInfo() + "/" + Solon.version();
    }

    @Override
    public void start(AppContext context) {
        if (Solon.app().enableHttp() == false) {
            return;
        }

        context.beanBuilderAdd(WebFilter.class, (clz, bw, ano) -> {
        });
        context.beanBuilderAdd(WebServlet.class, (clz, bw, ano) -> {
        });
        context.beanBuilderAdd(WebListener.class, (clz, bw, ano) -> {
        });

        context.lifecycle(ServerConstants.SIGNAL_LIFECYCLE_INDEX, () -> {
            start0(Solon.app());
        });
    }

    private void start0(SolonApp app) throws Throwable {
        //初始化属性
        ServerProps.init();

        Class<?> jspClz = ClassUtil.loadClass("org.apache.jasper.servlet.JspServlet");

        HttpServerProps props = HttpServerProps.getInstance();
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        _server = new TomcatServer();

        long time_start = System.currentTimeMillis();

        _server.start(_host, _port);

        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "http", SignalType.HTTP);

        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        String connectorInfo = "solon.connector:main: tomcat: Started ServerConnector@{HTTP/1.1,[http/1.1]";
        if (app.enableWebSocket()) {
            String wsServerUrl = props.buildWsServerUrl(false);
            log.info(connectorInfo + "[WebSocket]}{" + wsServerUrl + "}");
        }

        String httpServerUrl = props.buildHttpServerUrl(false);
        log.info(connectorInfo + "}{" + httpServerUrl + "}");
        log.info("Server:main: tomcat: Started (" + solon_server_ver() + ") @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            log.info("Server:main: tomcat: Has Stopped (" + solon_server_ver() + ")");
        }
    }
}
