可参考资料：

https://blog.csdn.net/qq_43842093/article/details/125453380


可参考代码：

```java
package com.example.demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class SSEServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/sse", new SSEHandler());
        server.start();
        System.out.println("SSE server started on port 8080");
    }

    static class SSEHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
            exchange.getResponseHeaders().add("Cache-Control", "no-cache");
            exchange.getResponseHeaders().add("Connection", "keep-alive");


            exchange.sendResponseHeaders(200, Long.MAX_VALUE);

            OutputStream os = exchange.getResponseBody();

            for (int i = 0; i < 100; i++) {
                os.write(("this is msg" + i + "\n").getBytes(StandardCharsets.UTF_8));
                os.flush();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            os.write("ok------------>".getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

        }
    }
}
```