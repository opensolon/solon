# solon-net-httputils

轻量级 HTTP 客户端工具，支持同步 / 异步调用、流式响应（SSE / Line Stream）、连接池复用等特性。底层可切换 OkHttp 与 JDK 两种实现。

---

## 一、HttpConfiguration 配置说明

`HttpConfiguration` 是全局静态配置类，用于控制连接池大小、并发上限和重定向策略。**所有配置须在首次请求前设置。**

| 参数 | 默认值 | 说明 | 作用对象 |
|------|--------|------|---------|
| `maxIdleConnections` | 50 | 空闲连接缓存上限 | OkHttp 连接池（JDK 路径不生效） |
| `keepAliveMinutes` | 5 | 连接保活时长（分钟） | OkHttp 连接池 |
| `maxRequests` | 256 | 全局异步并发上限 | OkHttp Dispatcher / JDK 异步线程池 maxThreads |
| `maxRequestsPerHost` | 64 | 单 Host 异步并发上限 | OkHttp Dispatcher（JDK 路径不生效） |
| `forceDisconnectOnClose` | false | 关闭响应时是否强制断开连接 | JDK HttpURLConnection |
| `maxRedirects` | 20 | 最大重定向次数 | 两者均生效 |

### 超时配置（实例级）

每个 `HttpUtils` 实例可独立设置超时，默认值为 `connect=10s, write=60s, read=60s`。

```java
// 全局配置（首次请求前）
HttpConfiguration.setMaxIdleConnections(200);
HttpConfiguration.setKeepAliveMinutes(5);
HttpConfiguration.setMaxRequests(300);
HttpConfiguration.setMaxRequestsPerHost(256);
HttpConfiguration.setMaxRedirects(5);

// 实例级超时
HttpUtils.http(url).timeout(5, 30, 10).get();  // connect=5s, write=30s, read=10s
```

### JDK 路径注意事项

JDK `HttpURLConnection` 底层依赖 JVM 系统属性控制连接复用，`HttpConfiguration` 的连接池参数**不生效**。需通过 JVM 参数调优：

```
-Dhttp.maxConnections=200
-Dhttp.keepAlive=true
```

---

## 二、QPS 参考表

以下数据基于 Little's Law（`QPS = 并发数 / 延迟`）估算，实际 QPS 受网络质量、TLS 握手开销、服务端处理能力等因素影响。

### OkHttp 同步路径

> 链路：客户端 → Solon Server 线程（阻塞）→ HttpUtils.execDo(sync) → 服务端

| 服务端延迟 | Server 线程数 | maxIdleConnections | 理论 QPS | 连接复用率 | 有效 QPS（HTTP） | 有效 QPS（HTTPS） |
|-----------|-------------|-------------------|---------|-----------|-----------------|------------------|
| 20ms | 64 | 50 | 3,200 | 78% | ~2,800 | ~2,400 |
| 20ms | 200 | 50 | 10,000 | 25% | ~8,000 | ~6,500 |
| 20ms | 200 | 200 | 10,000 | ~100% | ~10,000 | ~9,500 |
| 50ms | 64 | 50 | 1,280 | 78% | ~1,180 | ~1,050 |
| 50ms | 200 | 50 | 4,000 | 25% | ~3,400 | ~2,800 |
| 50ms | 200 | 200 | 4,000 | ~100% | ~4,000 | ~3,800 |
| 100ms | 200 | 50 | 2,000 | 25% | ~1,600 | ~1,300 |
| 100ms | 200 | 200 | 2,000 | ~100% | ~2,000 | ~1,900 |

### OkHttp 异步路径

> 链路：客户端 → Solon Server → HttpUtils.execAsync → OkHttp Dispatcher → 服务端

| 服务端延迟 | maxRequests | maxRequestsPerHost | maxIdleConnections | 单 Host QPS | 多 Host（4）QPS |
|-----------|-------------|-------------------|-------------------|------------|----------------|
| 20ms | 256 | 64 | 50 | 3,200 | 5,120（受 maxRequests 卡顶） |
| 20ms | 512 | 256 | 200 | 12,800 | 12,800（受 maxRequests 卡顶） |
| 50ms | 256 | 64 | 50 | 1,280 | 5,120 |
| 50ms | 512 | 256 | 200 | 5,120 | 5,120 |
| 100ms | 256 | 64 | 50 | 640 | 5,120 |
| 100ms | 512 | 256 | 200 | 2,560 | 5,120 |

> 注：异步路径的 QPS 上限 = maxRequests / 延迟（多 Host 时各 Host 按 maxRequestsPerHost 分配，但全局不超过 maxRequests）。

### JDK 同步路径

> 链路：客户端 → Solon Server 线程（阻塞）→ HttpURLConnection → 服务端

| 服务端延迟 | Server 线程数 | http.maxConnections | 理论 QPS | 有效 QPS（HTTP） | 有效 QPS（HTTPS） |
|-----------|-------------|-------------------|---------|-----------------|------------------|
| 20ms | 64 | 5（默认） | 3,200 | ~800 | ~500 |
| 20ms | 200 | 5 | 10,000 | ~800 | ~500 |
| 20ms | 200 | 200 | 10,000 | ~9,500 | ~9,000 |
| 50ms | 64 | 5 | 1,280 | ~400 | ~320 |
| 50ms | 200 | 5 | 4,000 | ~500 | ~350 |
| 50ms | 200 | 200 | 4,000 | ~3,800 | ~3,500 |
| 100ms | 200 | 5 | 2,000 | ~300 | ~220 |
| 100ms | 200 | 200 | 2,000 | ~1,900 | ~1,700 |

### JDK 异步路径

> 链路：客户端 → Solon Server → HttpUtils.execAsync → 自建线程池（core=0, max=maxRequests, SynchronousQueue, CallerRunsPolicy）→ 服务端

| 服务端延迟 | maxRequests | http.maxConnections | 理论 QPS | 有效 QPS（HTTPS） | 风险 |
|-----------|------------|-------------------|---------|------------------|------|
| 20ms | 256 | 5 | 12,800 | ~500 | 低 |
| 20ms | 256 | 200 | 12,800 | ~9,000 | 低 |
| 50ms | 256 | 5 | 5,120 | ~350 | 中（满载后 CallerRuns 回退阻塞调用方） |
| 50ms | 256 | 200 | 5,120 | ~3,500 | 中 |
| 100ms | 256 | 5 | 2,560 | ~220 | 高（雪崩风险） |
| 100ms | 256 | 200 | 2,560 | ~1,700 | 中 |

> **警告**：JDK 异步线程池使用 `SynchronousQueue + corePoolSize=0`，无线冲缓冲。达到 maxThreads 后触发 `CallerRunsPolicy`，阻塞 Solon Server 线程，可能导致雪崩。

### QPS 对比总览（50ms 延迟，单 Host，HTTPS）

| 路径 | 默认配置 | 调优后 | 关键调优项 |
|------|---------|-------|----------|
| OkHttp 同步 200 线程 | ~2,800 | ~3,800 | maxIdleConnections=200 |
| OkHttp 异步 | ~1,280 | ~5,120 | maxRequestsPerHost=256, maxIdleConnections=200 |
| JDK 同步 200 线程 | ~350 | ~3,500 | -Dhttp.maxConnections=200 |
| JDK 异步 | ~350 | ~3,500 | -Dhttp.maxConnections=200 |

---

## 三、推荐配置组合

### 小型应用（< 1,000 QPS）

```java
// 默认配置即可，无需调整
// OkHttp 同步路径
HttpUtils.http(url).timeout(5, 30, 10).get();
```

### 中型应用（1,000 ~ 5,000 QPS）

```java
HttpConfiguration.setMaxIdleConnections(200);
HttpConfiguration.setKeepAliveMinutes(5);
HttpConfiguration.setMaxRequests(300);
HttpConfiguration.setMaxRequestsPerHost(256);
HttpConfiguration.setMaxRedirects(5);

// 同步调用
HttpUtils.http(url).timeout(5, 30, 10).get();

// 异步调用
HttpUtils.http(url).timeout(5, 30, 10).execAsync("GET");
```

### 大型应用（5,000 ~ 10,000 QPS）

```java
HttpConfiguration.setMaxIdleConnections(300);
HttpConfiguration.setKeepAliveMinutes(3);
HttpConfiguration.setMaxRequests(512);
HttpConfiguration.setMaxRequestsPerHost(256);
HttpConfiguration.setMaxRedirects(5);

// 推荐：OkHttp 异步路径
HttpUtils.http(url).timeout(5, 30, 10).execAsync("GET");
```

### JDK 路径（如无法引入 OkHttp）

JVM 启动参数：
```
-Dhttp.maxConnections=200
-Dhttp.keepAlive=true
```

```java
HttpConfiguration.setMaxRequests(300);
```

---

## 四、HttpUtils 使用示例

### 基础调用

#### GET 请求

```java
// 同步 GET
String body = HttpUtils.http("http://localhost:8080/hello").get();

// 带参数（拼接到 URL）
String body = HttpUtils.http("http://localhost:8080/hello?name=world").get();

// 带超时控制
String body = HttpUtils.http("http://localhost:8080/hello")
        .timeout(5, 30, 10)  // connect=5s, write=30s, read=10s
        .get();
```

#### POST 请求

```java
// POST JSON
String body = HttpUtils.http("http://localhost:8080/api/user")
        .bodyOfJson("{\"name\":\"solon\",\"age\":1}")
        .post();

// POST 表单
String body = HttpUtils.http("http://localhost:8080/api/login")
        .bodyOfForm("username=solon&password=123456")
        .post();
```

#### 添加请求头

```java
String body = HttpUtils.http("http://localhost:8080/api/data")
        .header("Authorization", "Bearer xxx-yyy-zzz")
        .header("Accept", "application/json")
        .get();
```

### 异步调用

```java
// 异步 GET（返回 CompletableFuture）
CompletableFuture<HttpResponse> future = HttpUtils.http("http://localhost:8080/hello")
        .timeout(5, 30, 10)
        .execAsync("GET");

// 链式回调
future.thenAccept(resp -> {
    System.out.println("状态码: " + resp.code());
    System.out.println("响应体: " + resp.bodyAsString());
}).exceptionally(ex -> {
    System.err.println("请求失败: " + ex.getMessage());
    return null;
});

// 阻塞等待结果（谨慎使用，仅用于演示）
HttpResponse resp = future.get();
String body = resp.bodyAsString();
```

### 通过服务名调用（需配合 Solon Cloud 注册中心）

```java
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);

        // 通过服务名进行 HTTP 请求
        String body = HttpUtils.http("HelloService", "/hello").get();
    }
}
```

### 流式响应

#### SSE（Server-Sent Events）

```java
// 同步 SSE 流
HttpUtils.http("http://localhost:8080/sse/events")
        .execAsSseStream("GET")
        .subscribe(evt -> {
            System.out.println("event: " + evt.getEventName());
            System.out.println("data: " + evt.getData());
        });

// 异步 SSE 流（返回 Flux）
Flux<SseEvent> sseFlux = HttpUtils.http("http://localhost:8080/sse/events")
        .execAsSseStream("GET");

sseFlux.subscribe(evt -> {
    System.out.println("data: " + evt.getData());
});
```

#### Line Stream（逐行流式响应）

```java
Flux<String> lineFlux = HttpUtils.http("http://localhost:8080/stream/logs")
        .execAsLineStream("GET");

lineFlux.subscribe(line -> {
    System.out.println(line);
    // 按行处理日志或大响应体
});
```

### 预热示例

```java
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);

        // 用 HTTP 请求自己进行预热
        PreheatUtils.preheat("/healthz");

        // 用 bean 预热
        HelloService service = Aop.get(HelloService.class);
        service.hello();
    }
}
```

### 完整示例：服务端 + 客户端

```java
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args, app -> {
            // 在首次请求前配置连接池
            HttpConfiguration.setMaxIdleConnections(200);
            HttpConfiguration.setMaxRequests(300);
            HttpConfiguration.setMaxRequestsPerHost(256);
        });
    }
}

@Mapping("/api")
@Controller
public class ApiController {

    // 同步调用下游服务
    @Mapping("/sync")
    public String sync() {
        return HttpUtils.http("http://localhost:8081/user/info")
                .timeout(5, 30, 10)
                .header("X-Trace-Id", Solon.cfg().get("solon.app.id"))
                .get();
    }

    // 异步调用下游服务
    @Mapping("/async")
    public CompletableFuture<String> async() {
        return HttpUtils.http("http://localhost:8081/user/info")
                .timeout(5, 30, 10)
                .execAsync("GET")
                .thenApply(resp -> {
                    if (resp.code() == 200) {
                        return resp.bodyAsString();
                    }
                    throw new RuntimeException("下游返回: " + resp.code());
                });
    }

    // 流式 SSE 转发
    @Mapping(value = "/sse", produces = "text/event-stream")
    public Flux<SseEvent> sse() {
        return HttpUtils.http("http://localhost:8081/events/stream")
                .execAsSseStream("GET");
    }
}
```

---

## 五、注意事项

1. **HttpUtils 实例为一次性使用**，不可在多线程间复用同一实例（内部状态非线程安全）。
2. **HttpConfiguration 必须在首次请求前配置**，运行时修改不保证立即生效。
3. **JDK 路径的连接池参数不生效**，需通过 JVM 系统属性 `-Dhttp.maxConnections` 控制。
4. **生产环境建议将 `readTimeout` 设为 10~15 秒**，默认 60 秒在慢请求场景下可能耗尽线程池。
5. **`maxRedirects` 建议设为 5**，默认 20 在极端场景下可能导致长链路请求。
6. **OkHttp 为推荐实现**，JDK 路径在高并发下连接复用率极低，性能差距可达 5~10 倍。