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
import org.noear.solon.server.netahttp.http.NetaHttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

/**
 * 基于 neta-codec-http 的 HTTP 服务器实现
 * <p>
 * 使用 neta subscribe 模式处理 HTTP 请求（参考 neta-codec-http RealAsServerTest）：
 * <pre>
 *   协议栈：HttpServerDuplexe → HttpRequestAggregator
 *   业务处理：通过 netManager.subscribe() 接收解码后的 FullHttpRequest，
 *            通过 NetChannel.sendData() 发送 FullHttpResponse
 * </pre>
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

    private NetManager netManager;
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
        // 最大内容长度
        int maxContentLength = ServerProps.request_maxBodySize > 0
                ? (int) ServerProps.request_maxBodySize
                : 1024 * 1024; // 默认 1MB

        // 最终的 handler 引用
        final Handler finalHandler = this.handler;

        // 构建 neta 服务端协议栈（参考 neta-codec-http RealAsServerTest 模式）
        ProtoInitializer initializer = new ProtoInitializer() {
            @Override
            public void config(ProtoBuildContext ctx) {
                // HTTP 编解码（双向 Duplexer：rcv 方向解码请求，snd 方向编码响应）
                ctx.addLast("http-codec", new HttpServerDuplexe());

                // HTTP 请求聚合（Decoder：将 HttpObject 片段聚合为 FullHttpRequest）
                ctx.addLastDecoder("http-aggregator", new HttpRequestAggregator(maxContentLength));
            }
        };

        // 配置 Socket 参数
        SoConfig soConfig = SoConfig.TCP();
        soConfig.setRcvSlotSize(8192);
        soConfig.setSndSlotSize(8192);

        if (props.getIdleTimeoutOrDefault() > 0) {
            soConfig.setSoReadTimeoutMs((int) props.getIdleTimeoutOrDefault());
        }

        // 创建 NetManager
        netManager = new NetManager();

        // 绑定端口
        serverChannel = netManager.bind(new InetSocketAddress(host, port), initializer, soConfig);

        // 通过 subscribe 模式处理 HTTP 请求（参考 neta 官方 RealAsServerTest）
        // subscribe 在独立线程执行，不在 onMessage 帧内，
        // 因此 NetChannel.sendData() 会从链头开始走 SND 编码链
        netManager.subscribe(PlayLoad::isInbound, payload -> {
            Object data = payload.getData();
            if (!(data instanceof FullHttpRequest)) {
                return;
            }

            FullHttpRequest httpRequest = (FullHttpRequest) data;
            NetChannel netChannel = (NetChannel) payload.getSource();

            try {
                if (httpRequest.isBad()) {
                    sendErrorResponse(netChannel, httpRequest, HttpStatus.BAD_REQUEST,
                            httpRequest.badReason() != null ? httpRequest.badReason() : "Bad Request");
                    return;
                }

                NetaHttpContext ctx = new NetaHttpContext(httpRequest);
                handleDo(ctx);

                // 提交响应并通过 NetChannel.sendData() 发回
                FullHttpResponse resp = ctx.commitResponse();
                resp.streamId(httpRequest.streamId());
                netChannel.sendData(resp);

            } catch (Throwable e) {
                log.warn("HTTP handler error", e);
                sendErrorResponse(netChannel, httpRequest, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
            } finally {
                httpRequest.release();
            }
        });

        log.info("neta-http server started on {}:{}", host, port);
    }

    private void handleDo(NetaHttpContext ctx) {
        try {
            if (org.noear.solon.Solon.app() != null && ServerProps.output_meta) {
                ctx.headerSet("Solon-Server", "neta-http/" + org.noear.solon.Solon.version());
            }
            handler.handle(ctx);
        } catch (Throwable e) {
            log.warn("HTTP handler error: " + e.getMessage(), e);
            ctx.status(500);
        }
    }

    private void sendErrorResponse(NetChannel netChannel, FullHttpRequest httpRequest,
                                   HttpStatus status, String reason) {
        try {
            byte[] bodyBytes = reason.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            net.hasor.neta.bytebuf.ByteBuf bodyBuf = net.hasor.neta.bytebuf.ByteBuf.wrap(bodyBytes);

            DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, bodyBuf);
            resp.setHeader("Content-Type", "text/plain; charset=utf-8");
            resp.setHeader("Content-Length", String.valueOf(bodyBytes.length));
            resp.streamId(httpRequest.streamId());
            netChannel.sendData(resp);
        } catch (Throwable ex) {
            log.warn("Failed to send error response: {}", ex.getMessage());
        }
    }

    @Override
    public void stop() throws Throwable {
        if (serverChannel != null) {
            serverChannel.closeNow();
            serverChannel = null;
        }
        if (netManager != null) {
            netManager.shutdown();
            netManager = null;
        }
    }
}
