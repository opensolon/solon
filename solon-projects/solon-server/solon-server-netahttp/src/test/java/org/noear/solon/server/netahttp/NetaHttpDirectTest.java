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
import net.hasor.neta.codec.http.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.server.netahttp.http.NetaHttpContext;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NetaHttp 协议栈直接测试（使用 neta 客户端）
 *
 * @author noear
 * @since 3.10
 */
public class NetaHttpDirectTest {

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

    private void startServer(Handler handler) throws Throwable {
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
                FullHttpResponse resp;
                try {
                    NetaHttpContext ctx = new NetaHttpContext(req);
                    try {
                        handler.handle(ctx);
                    } catch (Throwable e) {
                        ctx.status(500);
                    }
                    resp = ctx.commitResponse();
                } catch (Throwable e) {
                    byte[] body = ("Error: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
                    resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.INTERNAL_SERVER_ERROR, ByteBuf.wrap(body));
                    resp.setHeader("Content-Type", "text/plain; charset=utf-8");
                    resp.setHeader("Content-Length", String.valueOf(body.length));
                }
                resp.streamId(req.streamId());
                ch.sendData(resp);
            } finally {
                req.release();
            }
        });
        Thread.sleep(100);
    }

    private FullHttpResponse netaGet(String path) throws Exception {
        clientNeta = new NetManager();
        NetChannel ch = clientNeta.connectSync(new InetSocketAddress("127.0.0.1", port), ctx -> {
            ctx.addLast("http-client", new HttpClientDuplexe());
            ctx.addLastDecoder("resp-agg", new HttpResponseAggregator());
        }, SoConfig.TCP());

        Queue<Object> inbound = new ConcurrentLinkedQueue<>();
        ch.subscribe(PlayLoad::isInbound, SubscribeMode.SYNC, d -> {
            if (d.getData() != null) inbound.offer(d.getData());
        });

        DefaultFullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
        req.addHeader("Host", "127.0.0.1:" + port);
        ch.sendData(req).get();
        return awaitResponse(inbound, 5000);
    }

    private FullHttpResponse netaPost(String path, String contentType, String body) throws Exception {
        clientNeta = new NetManager();
        NetChannel ch = clientNeta.connectSync(new InetSocketAddress("127.0.0.1", port), ctx -> {
            ctx.addLast("http-client", new HttpClientDuplexe());
            ctx.addLastDecoder("resp-agg", new HttpResponseAggregator());
        }, SoConfig.TCP());

        Queue<Object> inbound = new ConcurrentLinkedQueue<>();
        ch.subscribe(PlayLoad::isInbound, SubscribeMode.SYNC, d -> {
            if (d.getData() != null) inbound.offer(d.getData());
        });

        byte[] bodyBytes = body != null ? body.getBytes(StandardCharsets.UTF_8) : new byte[0];
        DefaultFullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path,
                ByteBuf.wrap(bodyBytes));
        req.addHeader("Host", "127.0.0.1:" + port);
        if (contentType != null) req.addHeader("Content-Type", contentType);
        req.addHeader("Content-Length", String.valueOf(bodyBytes.length));
        ch.sendData(req).get();
        return awaitResponse(inbound, 5000);
    }

    private FullHttpResponse awaitResponse(Queue<Object> inbound, long timeoutMs) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            Object msg = inbound.poll();
            if (msg instanceof FullHttpResponse) return (FullHttpResponse) msg;
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

    // ========== 测试用例 ==========

    @Test
    public void testBasicGetRequest() throws Throwable {
        startServer(ctx -> ctx.output("hello-neta"));
        FullHttpResponse resp = netaGet("/hello");
        assertEquals(HttpStatus.OK, resp.status());
        assertEquals("hello-neta", text(resp.content().retain()));
        resp.release();
    }

    @Test
    public void testGetWithPathParam() throws Throwable {
        startServer(ctx -> ctx.output("path:" + ctx.path()));
        FullHttpResponse resp = netaGet("/api/test");
        assertEquals(HttpStatus.OK, resp.status());
        assertEquals("path:/api/test", text(resp.content().retain()));
        resp.release();
    }

    @Test
    public void testGetWithQueryString() throws Throwable {
        startServer(ctx -> ctx.output("name=" + ctx.param("name")));
        FullHttpResponse resp = netaGet("/echo?name=world");
        assertEquals(HttpStatus.OK, resp.status());
        assertEquals("name=world", text(resp.content().retain()));
        resp.release();
    }

    @Test
    public void testPostFormUrlEncoded() throws Throwable {
        startServer(ctx -> {
            String name = ctx.param("name");
            String city = ctx.param("city");
            ctx.output("name=" + name + "&city=" + city);
        });
        FullHttpResponse resp = netaPost("/form", "application/x-www-form-urlencoded", "name=alice&city=hangzhou");
        assertEquals(HttpStatus.OK, resp.status());
        assertEquals("name=alice&city=hangzhou", text(resp.content().retain()));
        resp.release();
    }

    @Test
    public void testPostPlainTextBody() throws Throwable {
        startServer(ctx -> {
            try {
                InputStream is = ctx.bodyAsStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) != -1) bos.write(buf, 0, len);
                ctx.output("echo:" + bos.toString("UTF-8"));
            } catch (IOException e) {
                ctx.status(500);
                ctx.output("error");
            }
        });
        FullHttpResponse resp = netaPost("/echo", "text/plain", "hello-body");
        assertEquals(HttpStatus.OK, resp.status());
        assertEquals("echo:hello-body", text(resp.content().retain()));
        resp.release();
    }

    @Test
    public void testResponseStatus200() throws Throwable {
        startServer(ctx -> { ctx.status(200); ctx.output("ok"); });
        FullHttpResponse resp = netaGet("/ok");
        assertEquals(HttpStatus.OK, resp.status());
        assertEquals("ok", text(resp.content().retain()));
        resp.release();
    }

    @Test
    public void testResponseStatus404() throws Throwable {
        startServer(ctx -> { ctx.status(404); ctx.output("not found"); });
        FullHttpResponse resp = netaGet("/not-exist");
        assertEquals(HttpStatus.NOT_FOUND, resp.status());
        resp.release();
    }

    @Test
    public void testResponseStatus500() throws Throwable {
        startServer(ctx -> { ctx.status(500); ctx.output("server error"); });
        FullHttpResponse resp = netaGet("/error");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.status());
        resp.release();
    }

    @Test
    public void testResponseHeaders() throws Throwable {
        startServer(ctx -> {
            ctx.headerSet("X-Custom", "test-value");
            ctx.contentType("application/json");
            ctx.output("{\"key\":\"value\"}");
        });
        FullHttpResponse resp = netaGet("/headers");
        assertEquals(HttpStatus.OK, resp.status());
        assertEquals("{\"key\":\"value\"}", text(resp.content().retain()));
        resp.release();
    }

    @Test
    public void testRequestMethod() throws Throwable {
        startServer(ctx -> ctx.output("method=" + ctx.method()));
        FullHttpResponse resp = netaGet("/method");
        assertEquals(HttpStatus.OK, resp.status());
        assertEquals("method=GET", text(resp.content().retain()));
        resp.release();
    }

    @Test
    public void testLargeBodyResponse() throws Throwable {
        startServer(ctx -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000; i++) sb.append("line-").append(i).append("\n");
            ctx.output(sb.toString());
        });
        FullHttpResponse resp = netaGet("/large");
        assertEquals(HttpStatus.OK, resp.status());
        String body = text(resp.content().retain());
        assertTrue(body.contains("line-0"));
        assertTrue(body.contains("line-999"));
        resp.release();
    }

    @Test
    public void testHandlerException() throws Throwable {
        startServer(ctx -> { throw new RuntimeException("intentional error"); });
        FullHttpResponse resp = netaGet("/fail");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.status());
        resp.release();
    }
}
