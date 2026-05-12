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

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.channel.*;
import net.hasor.neta.channel.data.ProtoRcvQueue;
import net.hasor.neta.channel.data.ProtoSndQueue;
import net.hasor.neta.codec.http.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 最小化测试 - 使用 neta 客户端验证服务端协议栈
 */
public class NetaHttpMinimalTest {

    private NetManager serverNeta;
    private NetManager clientNeta;
    private int port;

    private static int findFreePort() throws IOException {
        try (ServerSocket ss = new ServerSocket(0)) {
            return ss.getLocalPort();
        }
    }

    @BeforeEach
    public void setUp() throws Throwable {
        port = findFreePort();
    }

    @AfterEach
    public void tearDown() {
        if (clientNeta != null) { try { clientNeta.shutdown(); } catch (Exception e) {} clientNeta = null; }
        if (serverNeta != null) { try { serverNeta.shutdown(); } catch (Exception e) {} serverNeta = null; }
    }

    /**
     * 方式A：使用 ProtoHandler（Decoder）模式 + context.sendData
     */
    @Test
    public void testProtoHandlerWithContextSendData() throws Throwable {
        serverNeta = new NetManager();
        serverNeta.bind(new InetSocketAddress("127.0.0.1", port), ctx -> {
            ctx.addLast("http-codec", new HttpServerDuplexe());
            ctx.addLastDecoder("http-aggregator", new HttpRequestAggregator(1024 * 1024));
            ctx.addLastDecoder("http-handler", new ProtoHandler<HttpObject, Object>() {
                @Override
                public ProtoStatus onMessage(ProtoContext context, ProtoRcvQueue<HttpObject> src, ProtoSndQueue<Object> dst) throws Throwable {
                    HttpObject obj;
                    while ((obj = src.takeMessage()) != null) {
                        if (!(obj instanceof FullHttpRequest)) continue;
                        FullHttpRequest req = (FullHttpRequest) obj;
                        try {
                            byte[] body = "hello-handler".getBytes(StandardCharsets.UTF_8);
                            DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, ByteBuf.wrap(body));
                            resp.setHeader("Content-Type", "text/plain");
                            resp.setHeader("Content-Length", String.valueOf(body.length));
                            resp.streamId(req.streamId());
                            // 使用 dst.offerMessage 发送响应
                            dst.offerMessage(resp);
                        } finally {
                            req.release();
                        }
                    }
                    return ProtoStatus.Next;
                }
            });
        }, SoConfig.TCP());

        // 用 neta 客户端测试
        clientNeta = new NetManager();
        FullHttpResponse response = sendWithNetaClient(clientNeta, "/test");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.status());
        assertEquals("hello-handler", text(response.content().retain()));
        response.release();
    }

    /**
     * 方式B：使用 subscribe 模式 + NetChannel.sendData
     */
    @Test
    public void testSubscribeWithChannelSendData() throws Throwable {
        serverNeta = new NetManager();
        serverNeta.bind(new InetSocketAddress("127.0.0.1", port), ctx -> {
            ctx.addLast("http-codec", new HttpServerDuplexe());
            ctx.addLastDecoder("http-aggregator", new HttpRequestAggregator(1024 * 1024));
        }, SoConfig.TCP());

        serverNeta.subscribe(PlayLoad::isInbound, payload -> {
            Object data = payload.getData();
            if (!(data instanceof FullHttpRequest)) return;
            FullHttpRequest req = (FullHttpRequest) data;
            NetChannel ch = (NetChannel) payload.getSource();
            try {
                byte[] body = "hello-subscribe".getBytes(StandardCharsets.UTF_8);
                DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, ByteBuf.wrap(body));
                resp.setHeader("Content-Type", "text/plain");
                resp.setHeader("Content-Length", String.valueOf(body.length));
                resp.streamId(req.streamId());
                ch.sendData(resp);
            } finally {
                req.release();
            }
        });

        // 用 neta 客户端测试
        clientNeta = new NetManager();
        FullHttpResponse response = sendWithNetaClient(clientNeta, "/test");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.status());
        assertEquals("hello-subscribe", text(response.content().retain()));
        response.release();
    }

    private FullHttpResponse sendWithNetaClient(NetManager neta, String path) throws Exception {
        NetChannel channel = neta.connectSync(new InetSocketAddress("127.0.0.1", port), ctx -> {
            ctx.addLast("http-client", new HttpClientDuplexe());
            ctx.addLastDecoder("resp-agg", new HttpResponseAggregator());
        }, SoConfig.TCP());

        Queue<Object> inbound = new ConcurrentLinkedQueue<>();
        channel.subscribe(PlayLoad::isInbound, SubscribeMode.SYNC, d -> {
            if (d.getData() != null) inbound.offer(d.getData());
        });

        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
        request.addHeader("Host", "127.0.0.1:" + port);
        channel.sendData(request).get();

        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline) {
            Object msg = inbound.poll();
            if (msg instanceof FullHttpResponse) {
                return (FullHttpResponse) msg;
            }
            Thread.sleep(10);
        }
        fail("Timed out waiting for FullHttpResponse");
        return null;
    }

    private static String text(ByteBuf buf) {
        if (buf == null || buf.readableBytes() == 0) return "";
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(0, bytes, 0, bytes.length);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
