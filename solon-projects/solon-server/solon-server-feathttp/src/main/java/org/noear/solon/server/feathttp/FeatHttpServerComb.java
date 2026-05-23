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
package org.noear.solon.server.feathttp;

import org.noear.solon.core.handle.Handler;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.http.HttpServerConfigure;
import org.noear.solon.server.prop.impl.HttpServerProps;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 通过组合支持多端口模式
 *
 * @author noear
 * @since 2.2
 */
public class FeatHttpServerComb implements HttpServerConfigure, ServerLifecycle {
    protected final HttpServerProps props;
    protected int coreThreads;
    protected Executor workExecutor;
    protected boolean enableWebSocket;
    protected Handler handler;
    protected boolean enableSsl = true;
    protected SSLContext sslContext;
    protected boolean enableDebug = false;
    protected Set<Integer> addHttpPorts = new LinkedHashSet<>();
    protected List<FeatHttpServer> servers = new ArrayList<>();

    public FeatHttpServerComb(HttpServerProps props) {
        this.props = props;
    }

    /**
     * 是否允许Ssl
     */
    @Override
    public void enableSsl(boolean enable, SSLContext sslContext) {
        this.enableSsl = enable;
        this.sslContext = sslContext;
    }

    @Override
    public void enableDebug(boolean enable) {
        enableDebug = enable;
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
        this.workExecutor = executor;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setCoreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
    }

    public void enableWebSocket(boolean enableWebSocket) {
        this.enableWebSocket = enableWebSocket;
    }

    public boolean isSecure() {
        if (servers.size() > 0) {
            return servers.get(0).isSecure();
        } else {
            return false;
        }
    }

    @Override
    public void start(String host, int port) throws Throwable {
        {
            FeatHttpServer s1 = new FeatHttpServer(props);
            s1.setWorkExecutor(workExecutor);
            s1.setCoreThreads(coreThreads);
            s1.enableWebSocket(enableWebSocket);
            s1.setHandler(handler);
            s1.enableSsl(enableSsl, sslContext);
            s1.enableDebug(enableDebug);
            s1.start(host, port);

            servers.add(s1);
        }

        for (Integer portAdd : addHttpPorts) {
            FeatHttpServer s2 = new FeatHttpServer(props);
            s2.setWorkExecutor(workExecutor);
            s2.setCoreThreads(coreThreads);
            s2.enableWebSocket(enableWebSocket);
            s2.setHandler(handler);
            s2.enableSsl(false, null); //只支持http
            s2.enableDebug(enableDebug);
            s2.start(host, portAdd);

            servers.add(s2);
        }
    }

    @Override
    public void stop() throws Throwable {
        for (ServerLifecycle s : servers) {
            s.stop();
        }
    }
}
