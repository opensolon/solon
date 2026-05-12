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
package org.noear.solon.server.netahttp;

import org.noear.solon.core.handle.Handler;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.http.HttpServerConfigure;
import org.noear.solon.server.prop.impl.HttpServerProps;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 通过组合支持多端口模式
 *
 * @author noear
 * @since 3.10
 */
public class NetaHttpServerComb implements HttpServerConfigure, ServerLifecycle {
    protected final HttpServerProps props;
    protected int coreThreads;
    protected Executor workExecutor;
    protected Handler handler;
    protected boolean enableDebug = false;
    protected boolean enableWebSocket = false;
    protected Set<Integer> addHttpPorts = new LinkedHashSet<>();
    protected List<NetaHttpServerImpl> servers = new ArrayList<>();

    public NetaHttpServerComb(HttpServerProps props) {
        this.props = props;
    }

    @Override
    public void enableSsl(boolean enable, javax.net.ssl.SSLContext sslContext) {
        // neta-http 暂不支持 SSL，预留接口
    }

    @Override
    public void enableDebug(boolean enable) {
        this.enableDebug = enable;
    }

    @Override
    public void addHttpPort(int port) {
        addHttpPorts.add(port);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setCoreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
    }

    public void enableWebSocket(boolean enable) {
        this.enableWebSocket = enable;
    }

    @Override
    public void setExecutor(Executor executor) {
        this.workExecutor = executor;
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
            NetaHttpServerImpl s1 = new NetaHttpServerImpl(props);
            s1.setWorkExecutor(workExecutor);
            s1.setCoreThreads(coreThreads);
            s1.setHandler(handler);
            s1.enableDebug(enableDebug);
            s1.enableWebSocket(enableWebSocket);
            s1.start(host, port);
            servers.add(s1);
        }

        for (Integer portAdd : addHttpPorts) {
            NetaHttpServerImpl s2 = new NetaHttpServerImpl(props);
            s2.setWorkExecutor(workExecutor);
            s2.setCoreThreads(coreThreads);
            s2.setHandler(handler);
            s2.enableDebug(enableDebug);
            s2.enableWebSocket(enableWebSocket);
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
