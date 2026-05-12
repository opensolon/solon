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

import net.hasor.neta.channel.*;
import net.hasor.neta.codec.http.*;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.netahttp.http.NetaHttpContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

/**
 * 基于 neta-codec-http 的 HTTP 服务器实现
 *
 * @author noear
 * @since 3.10
 */
public class NetaHttpServerImpl implements ServerLifecycle {
    static final Logger log = LoggerFactory.getLogger(NetaHttpServerImpl.class);

    protected final HttpServerProps props;
    protected Handler handler;
    protected int coreThreads;
    protected Executor workExecutor;
    protected boolean enableDebug = false;
    protected boolean isSecure = false;
    protected boolean enableWebSocket = false;

    private NetListen serverChannel;

    public NetaHttpServerImpl(HttpServerProps props) {
        this.props = props;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public void enableDebug(boolean enable) {
        this.enableDebug = enable;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setWorkExecutor(Executor executor) {
        this.workExecutor = executor;
    }

    public void setCoreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
    }

    public void enableWebSocket(boolean enable) {
        this.enableWebSocket = enable;
    }

    @Override
    public void start(String host, int port) throws Throwable {
        NetaHttpContextHandler contextHandler = new NetaHttpContextHandler(handler);

        // 构建 neta 服务端协议栈
        ProtoInitializer initializer = new ProtoInitializer() {
            @Override
            public void config(ProtoBuildContext ctx) {
                // HTTP 解码/编码（双向）
                ctx.addLast("http", new HttpServerDuplexe());

                // HTTP 消息聚合（将 HttpObject 流聚合为 FullHttpRequest / FullHttpResponse）
                int maxContentLength = ServerProps.request_maxBodySize > 0
                        ? (int) ServerProps.request_maxBodySize
                        : 1024 * 1024; // 默认 1MB
                ctx.addLast("http-agg", new HttpServerDuplexeAggregator(maxContentLength));

                // TODO: WebSocket 路由支持需要 ProtoBuilder/ProtoRoutingBuilder 机制
                // 当前版本仅支持 HTTP，WebSocket 将在后续版本中通过路由机制实现

                // 业务处理
                ctx.addLast("handler", contextHandler);
            }
        };

        // 配置 Socket 参数
        SoConfig soConfig = SoConfig.TCP();
        soConfig.setRcvSlotSize(8192);
        soConfig.setSndSlotSize(8192);

        if (props.getIdleTimeoutOrDefault() > 0) {
            soConfig.setSoReadTimeoutMs((int) props.getIdleTimeoutOrDefault());
        }

        // 绑定端口
        NetManager netManager = new NetManager();
        serverChannel = netManager.bind(new InetSocketAddress(host, port), initializer, soConfig);

        log.info("neta-http server started on {}:{}", host, port);
    }

    @Override
    public void stop() throws Throwable {
        if (serverChannel != null) {
            serverChannel.closeNow();
            serverChannel = null;
        }
    }
}
