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
package features;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本地 HTTP 服务，用于并发/连接复用单测（不依赖外网）。
 * <p>
 * 覆盖生产常见路径：GET/POST、慢接口、重定向、错误状态码；
 * 并统计峰值并发与远端端口集合（辅助观察连接复用）。
 *
 * @since 4.0.4
 */
public class LocalHttpServer implements AutoCloseable {
    private final HttpServer server;
    private final ExecutorService executor;
    private final int port;
    private final AtomicInteger totalRequests = new AtomicInteger();
    private final AtomicInteger activeRequests = new AtomicInteger();
    private final AtomicInteger maxActiveRequests = new AtomicInteger();
    private final Set<String> remoteEndpoints = ConcurrentHashMap.newKeySet();
    private volatile int delayMs;

    public LocalHttpServer() throws IOException {
        this(0);
    }

    public LocalHttpServer(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        this.executor = Executors.newCachedThreadPool();
        this.server.setExecutor(executor);

        this.server.createContext("/ok", this::handleOk);
        this.server.createContext("/echo", this::handleEcho);
        this.server.createContext("/slow", this::handleSlow);
        this.server.createContext("/post", this::handlePost);
        this.server.createContext("/redirect", this::handleRedirect);
        this.server.createContext("/status", this::handleStatus);

        this.server.start();
        this.port = this.server.getAddress().getPort();
    }

    public int getPort() {
        return port;
    }

    public String url(String path) {
        return "http://127.0.0.1:" + port + path;
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }

    public int getTotalRequests() {
        return totalRequests.get();
    }

    public int getMaxActiveRequests() {
        return maxActiveRequests.get();
    }

    /**
     * 观察到的远端 endpoint 数（host:port）。
     * 串行 keep-alive 复用时通常接近 1；高并发时会升高，但仍应远小于总请求数。
     */
    public int getRemoteEndpointCount() {
        return remoteEndpoints.size();
    }

    public void resetCounters() {
        totalRequests.set(0);
        activeRequests.set(0);
        maxActiveRequests.set(0);
        remoteEndpoints.clear();
    }

    private void handleOk(HttpExchange exchange) throws IOException {
        onEnter(exchange);
        try {
            maybeDelay();
            writeText(exchange, 200, "ok");
        } finally {
            onExit();
        }
    }

    private void handleEcho(HttpExchange exchange) throws IOException {
        onEnter(exchange);
        try {
            maybeDelay();
            String query = exchange.getRequestURI().getRawQuery();
            String body = query == null ? "echo" : query;
            writeText(exchange, 200, body);
        } finally {
            onExit();
        }
    }

    private void handleSlow(HttpExchange exchange) throws IOException {
        onEnter(exchange);
        try {
            int wait = delayMs > 0 ? delayMs : 50;
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            writeText(exchange, 200, "slow");
        } finally {
            onExit();
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        onEnter(exchange);
        try {
            maybeDelay();
            String body = readBody(exchange);
            writeText(exchange, 200, body == null ? "" : body);
        } finally {
            onExit();
        }
    }

    private void handleRedirect(HttpExchange exchange) throws IOException {
        onEnter(exchange);
        try {
            maybeDelay();
            // 相对路径重定向，覆盖生产手动 followRedirect 逻辑
            exchange.getResponseHeaders().set("Location", "/ok");
            exchange.getResponseHeaders().set("Connection", "keep-alive");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        } finally {
            onExit();
        }
    }

    private void handleStatus(HttpExchange exchange) throws IOException {
        onEnter(exchange);
        try {
            maybeDelay();
            String query = exchange.getRequestURI().getRawQuery();
            int code = 500;
            if (query != null && query.startsWith("code=")) {
                try {
                    code = Integer.parseInt(query.substring(5));
                } catch (NumberFormatException ignore) {
                    code = 500;
                }
            }
            writeText(exchange, code, "status-" + code);
        } finally {
            onExit();
        }
    }

    private void onEnter(HttpExchange exchange) {
        totalRequests.incrementAndGet();
        trackRemote(exchange);
        int active = activeRequests.incrementAndGet();
        while (true) {
            int max = maxActiveRequests.get();
            if (active <= max || maxActiveRequests.compareAndSet(max, active)) {
                break;
            }
        }
    }

    private void trackRemote(HttpExchange exchange) {
        try {
            InetSocketAddress remote = exchange.getRemoteAddress();
            if (remote != null && remote.getAddress() != null) {
                remoteEndpoints.add(remote.getAddress().getHostAddress() + ":" + remote.getPort());
            }
        } catch (Throwable ignore) {
            // 统计失败不影响业务响应
        }
    }

    private void onExit() {
        activeRequests.decrementAndGet();
    }

    private void maybeDelay() {
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        InputStream in = exchange.getRequestBody();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while ((n = in.read(buf)) >= 0) {
            out.write(buf, 0, n);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    private static void writeText(HttpExchange exchange, int code, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.getResponseHeaders().set("Connection", "keep-alive");
        exchange.sendResponseHeaders(code, bytes.length);
        OutputStream out = exchange.getResponseBody();
        try {
            out.write(bytes);
        } finally {
            out.close();
        }
    }

    @Override
    public void close() {
        server.stop(0);
        executor.shutdownNow();
    }
}
