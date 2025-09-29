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
package org.noear.solon.server.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.noear.solon.Utils;
import org.noear.solon.lang.Nullable;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.http.HttpServerConfigure;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.ssl.SslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @author Yukai
 * @since 2022/8/26 17:01
 **/
public abstract class TomcatServerBase implements ServerLifecycle, HttpServerConfigure {
    static final Logger log = LoggerFactory.getLogger(TomcatServerBase.class);

    protected Tomcat _server;
    protected final HttpServerProps props;
    protected SslConfig sslConfig = new SslConfig(ServerConstants.SIGNAL_HTTP);

    protected Set<Integer> addHttpPorts = new LinkedHashSet<>();

    protected boolean enableHttp2 = false;

    public TomcatServerBase(HttpServerProps props) {
        this.props = props;
    }

    /**
     * 是否允许Ssl
     */
    @Override
    public void enableSsl(boolean enable, @Nullable SSLContext sslContext) {
        sslConfig.set(enable, sslContext);
    }

    @Override
    public boolean isSupportedHttp2() {
        return true;
    }

    @Override
    public void enableHttp2(boolean enable) {
        this.enableHttp2 = enable;
    }

    public boolean isEnableHttp2(){
        return enableHttp2;
    }

    /**
     * 添加 HttpPort（当 ssl 时，可再开个 http 端口）
     */
    @Override
    public void addHttpPort(int port) {
        addHttpPorts.add(port);
    }

    @Override
    public void setExecutor(Executor executor) {
        log.warn("Tomcat does not support user-defined executor");
    }

    public HttpServerProps getProps() {
        return props;
    }



    @Override
    public void start(String host, int port) throws Throwable {
        _server = new Tomcat();

        if (Utils.isNotEmpty(host)) {
            _server.setHostname(host);
        }

        //初始化上下文
        initContext();

        //添加连接端口
        addConnector(port, true);

        //http add
        for (Integer portAdd : addHttpPorts) {
            addConnector(portAdd, false);
        }

        _server.start();
    }


    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.destroy();
            _server = null;
        }
    }

    protected abstract Context initContext() throws Throwable;

    protected abstract void addConnector(int port, boolean isMain) throws Throwable;
}
