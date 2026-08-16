package features;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 专门用于 30x 重定向测试的简易 HTTP 服务
 */
public class LocalRedirectServer implements AutoCloseable {
    private final HttpServer server;
    private final ExecutorService executor;
    private final int port;

    public LocalRedirectServer() throws IOException {
        this(0);
    }

    public LocalRedirectServer(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        this.executor = Executors.newCachedThreadPool();
        this.server.setExecutor(executor);

        this.server.createContext("/redirect", this::handleRedirect);
        this.server.createContext("/target-endpoint", this::handleTarget);
        this.server.createContext("/loop-redirect", this::handleLoop);

        this.server.start();
        this.port = this.server.getAddress().getPort();
    }

    public int getPort() {
        return port;
    }

    public String url(String path) {
        return "http://127.0.0.1:" + port + path;
    }

    private void handleRedirect(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        int code = 302;
        String target = "/target-endpoint";

        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2) {
                    if ("code".equals(pair[0])) {
                        try {
                            code = Integer.parseInt(pair[1]);
                        } catch (NumberFormatException ignored) {
                        }
                    } else if ("target".equals(pair[0])) {
                        target = pair[1];
                    }
                }
            }
        }

        // 规范：重定向时读空请求体，避免连接未完成
        readBody(exchange);

        exchange.getResponseHeaders().set("Location", target);
        exchange.getResponseHeaders().set("Connection", "keep-alive");
        exchange.sendResponseHeaders(code, -1);
        exchange.close();
    }

    private void handleLoop(HttpExchange exchange) throws IOException {
        readBody(exchange);
        exchange.getResponseHeaders().set("Location", "/loop-redirect");
        exchange.getResponseHeaders().set("Connection", "keep-alive");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void handleTarget(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String body = readBody(exchange);
        String response = "target:" + method + ":body=[" + body + "]";

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.getResponseHeaders().set("Connection", "keep-alive");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
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

    @Override
    public void close() {
        server.stop(0);
        executor.shutdownNow();
    }
}
