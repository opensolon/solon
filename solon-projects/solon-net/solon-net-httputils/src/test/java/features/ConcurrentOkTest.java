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

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpConfiguration;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.okhttp.OkHttpDispatcherLoader;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtils;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * OkHttp 适配：对齐生产并行/连接池场景。
 * <p>
 * 覆盖：同步并行、异步并行、POST 体、自定义 timeout 派生客户端仍共享池、
 * 重定向连接归还、异步响应关闭后连接回收、配置映射。
 *
 * @since 4.0.4
 */
public class ConcurrentOkTest {
    private static LocalHttpServer server;

    static HttpUtils http(String url) {
        return OkHttpUtilsFactory.getInstance().http(url);
    }

    /** 子类暴露 protected getClient()，便于断言连接池/调度器共享 */
    static class ClientExposingOkHttpUtils extends OkHttpUtils {
        ClientExposingOkHttpUtils(String url) {
            super(url);
        }

        OkHttpClient client() {
            return getClient();
        }
    }

    @BeforeAll
    public static void setup() throws Exception {
        server = new LocalHttpServer();
    }

    @AfterAll
    public static void teardown() {
        if (server != null) {
            server.close();
        }
    }

    @Test
    public void parallelSyncGet() throws Exception {
        final int threads = 32;
        final AtomicInteger success = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        final CountDownLatch ready = new CountDownLatch(threads);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(threads);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            for (int i = 0; i < threads; i++) {
                final int idx = i;
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        ready.countDown();
                        try {
                            start.await();
                            String body = http(server.url("/echo?i=" + idx)).get();
                            Assertions.assertEquals("i=" + idx, body);
                            success.incrementAndGet();
                        } catch (Throwable e) {
                            error.compareAndSet(null, e);
                        } finally {
                            done.countDown();
                        }
                    }
                });
            }

            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            Assertions.assertTrue(done.await(30, TimeUnit.SECONDS));
        } finally {
            pool.shutdownNow();
        }

        if (error.get() != null) {
            throw new AssertionError("parallel sync request failed", error.get());
        }
        Assertions.assertEquals(threads, success.get());
    }

    @Test
    public void parallelSyncSlowObservesConcurrency() throws Exception {
        // 生产同步路径：业务线程阻塞调用 HttpUtils.get()，应能形成真实并行
        server.resetCounters();
        server.setDelayMs(80);
        final int threads = 16;
        final AtomicInteger success = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        final CountDownLatch ready = new CountDownLatch(threads);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(threads);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            for (int i = 0; i < threads; i++) {
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        ready.countDown();
                        try {
                            start.await();
                            Assertions.assertEquals("slow", http(server.url("/slow")).get());
                            success.incrementAndGet();
                        } catch (Throwable e) {
                            error.compareAndSet(null, e);
                        } finally {
                            done.countDown();
                        }
                    }
                });
            }

            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            Assertions.assertTrue(done.await(30, TimeUnit.SECONDS));
        } finally {
            pool.shutdownNow();
            server.setDelayMs(0);
        }

        if (error.get() != null) {
            throw new AssertionError("parallel slow sync failed", error.get());
        }
        Assertions.assertEquals(threads, success.get());
        Assertions.assertEquals(threads, server.getTotalRequests());
        Assertions.assertTrue(server.getMaxActiveRequests() > 1,
                "expected concurrent in-flight sync requests, maxActive=" + server.getMaxActiveRequests());
    }

    @Test
    public void parallelAsyncGet() throws Exception {
        final int count = 40;
        List<CompletableFuture<HttpResponse>> futures = new ArrayList<CompletableFuture<HttpResponse>>(count);

        for (int i = 0; i < count; i++) {
            futures.add(http(server.url("/echo?a=" + i)).execAsync("GET"));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);

        for (int i = 0; i < count; i++) {
            HttpResponse resp = futures.get(i).get();
            try {
                Assertions.assertEquals(200, resp.code());
                Assertions.assertEquals("a=" + i, resp.bodyAsString());
            } finally {
                resp.close();
            }
        }
    }

    @Test
    public void parallelSlowAsyncObservesConcurrency() throws Exception {
        server.resetCounters();
        server.setDelayMs(80);
        try {
            final int count = 20;
            List<CompletableFuture<HttpResponse>> futures = new ArrayList<CompletableFuture<HttpResponse>>(count);
            for (int i = 0; i < count; i++) {
                futures.add(http(server.url("/slow")).execAsync("GET"));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);

            for (CompletableFuture<HttpResponse> future : futures) {
                HttpResponse resp = future.get();
                try {
                    Assertions.assertEquals("slow", resp.bodyAsString());
                } finally {
                    resp.close();
                }
            }

            Assertions.assertEquals(count, server.getTotalRequests());
            // 异步应形成真实并行，服务端峰值活跃请求 > 1
            Assertions.assertTrue(server.getMaxActiveRequests() > 1,
                    "expected concurrent in-flight requests, maxActive=" + server.getMaxActiveRequests());
        } finally {
            server.setDelayMs(0);
        }
    }

    @Test
    public void parallelPostBody() throws Exception {
        // 生产常见：多线程同步 POST JSON/文本
        final int threads = 24;
        final AtomicInteger success = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        final CountDownLatch ready = new CountDownLatch(threads);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(threads);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            for (int i = 0; i < threads; i++) {
                final int idx = i;
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        ready.countDown();
                        try {
                            start.await();
                            String payload = "{\"id\":" + idx + "}";
                            String body = http(server.url("/post"))
                                    .body(payload, "application/json")
                                    .post();
                            Assertions.assertEquals(payload, body);
                            success.incrementAndGet();
                        } catch (Throwable e) {
                            error.compareAndSet(null, e);
                        } finally {
                            done.countDown();
                        }
                    }
                });
            }

            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            Assertions.assertTrue(done.await(30, TimeUnit.SECONDS));
        } finally {
            pool.shutdownNow();
        }

        if (error.get() != null) {
            throw new AssertionError("parallel post failed", error.get());
        }
        Assertions.assertEquals(threads, success.get());
    }

    @Test
    public void connectionPoolAndDispatcherShared() {
        OkHttpDispatcherLoader loader = OkHttpDispatcherLoader.getInstance();
        OkHttpClient base = loader.getBaseClient();
        ConnectionPool pool = loader.getConnectionPool();
        Dispatcher dispatcher = loader.getDispatcher();

        Assertions.assertSame(pool, base.connectionPool());
        Assertions.assertSame(dispatcher, base.dispatcher());

        // newBuilder 派生后仍共享 pool / dispatcher（连接复用核心语义）
        OkHttpClient derived = base.newBuilder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .build();
        Assertions.assertSame(pool, derived.connectionPool());
        Assertions.assertSame(dispatcher, derived.dispatcher());
        Assertions.assertNotSame(base, derived);

        // 默认超时路径直接复用 base client
        ClientExposingOkHttpUtils utilsDefault = new ClientExposingOkHttpUtils(server.url("/ok"));
        OkHttpClient clientDefault = utilsDefault.client();
        Assertions.assertSame(base, clientDefault);

        // 自定义 timeout 走 newBuilder，但仍共享连接池与调度器
        ClientExposingOkHttpUtils utilsTimeout = new ClientExposingOkHttpUtils(server.url("/ok"));
        utilsTimeout.timeout(3, 3, 3);
        OkHttpClient clientTimeout = utilsTimeout.client();
        Assertions.assertNotSame(base, clientTimeout);
        Assertions.assertSame(pool, clientTimeout.connectionPool());
        Assertions.assertSame(dispatcher, clientTimeout.dispatcher());
    }

    @Test
    public void customTimeoutPathSharesPoolUnderConcurrency() throws Exception {
        // 生产中型应用常见：实例级 timeout + 高并发；必须仍走共享 ConnectionPool
        ConnectionPool pool = OkHttpDispatcherLoader.getInstance().getConnectionPool();
        final int threads = 20;
        final AtomicInteger success = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        final CountDownLatch ready = new CountDownLatch(threads);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(threads);

        ExecutorService exec = Executors.newFixedThreadPool(threads);
        try {
            for (int i = 0; i < threads; i++) {
                final int idx = i;
                exec.submit(new Runnable() {
                    @Override
                    public void run() {
                        ready.countDown();
                        try {
                            start.await();
                            ClientExposingOkHttpUtils utils = new ClientExposingOkHttpUtils(server.url("/echo?t=" + idx));
                            utils.timeout(5, 10, 10);
                            OkHttpClient client = utils.client();
                            Assertions.assertSame(pool, client.connectionPool());
                            Assertions.assertSame(OkHttpDispatcherLoader.getInstance().getDispatcher(), client.dispatcher());
                            Assertions.assertEquals("t=" + idx, utils.get());
                            success.incrementAndGet();
                        } catch (Throwable e) {
                            error.compareAndSet(null, e);
                        } finally {
                            done.countDown();
                        }
                    }
                });
            }

            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            Assertions.assertTrue(done.await(30, TimeUnit.SECONDS));
        } finally {
            exec.shutdownNow();
        }

        if (error.get() != null) {
            throw new AssertionError("custom timeout concurrent path failed", error.get());
        }
        Assertions.assertEquals(threads, success.get());
    }

    @Test
    public void sequentialRequestsReuseIdleConnections() throws Exception {
        ConnectionPool pool = OkHttpDispatcherLoader.getInstance().getConnectionPool();
        int before = pool.connectionCount();

        server.resetCounters();
        for (int i = 0; i < 12; i++) {
            String body = http(server.url("/ok")).get();
            Assertions.assertEquals("ok", body);
        }

        // 给连接归还一点时间
        Thread.sleep(150);

        int after = pool.connectionCount();
        int idle = pool.idleConnectionCount();

        // 串行 keep-alive 下，连接数不应随请求次数线性增长
        Assertions.assertTrue(after - before <= 3,
                "connectionCount grew too much: before=" + before + ", after=" + after);
        Assertions.assertTrue(idle >= 0);
        Assertions.assertTrue(after >= idle);
        // 远端端口集合应明显小于请求次数（复用）
        Assertions.assertTrue(server.getRemoteEndpointCount() <= 3,
                "expected connection reuse, remoteEndpoints=" + server.getRemoteEndpointCount());
        Assertions.assertEquals(12, server.getTotalRequests());
    }

    @Test
    public void parallelBurstThenIdleConnectionsRecover() throws Exception {
        // 生产：突发并发后响应关闭，连接应回到 ConnectionPool 空闲集合，供后续复用
        ConnectionPool pool = OkHttpDispatcherLoader.getInstance().getConnectionPool();

        final int count = 24;
        List<CompletableFuture<HttpResponse>> futures = new ArrayList<CompletableFuture<HttpResponse>>(count);
        for (int i = 0; i < count; i++) {
            futures.add(http(server.url("/ok")).execAsync("GET"));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);
        for (CompletableFuture<HttpResponse> future : futures) {
            HttpResponse resp = future.get();
            try {
                Assertions.assertEquals("ok", resp.bodyAsString());
            } finally {
                resp.close();
            }
        }

        Thread.sleep(200);

        int idle = pool.idleConnectionCount();
        int total = pool.connectionCount();
        Assertions.assertTrue(total >= 1, "expected pooled connections after burst");
        Assertions.assertTrue(idle >= 1, "expected idle connections after response close, idle=" + idle + ", total=" + total);
        Assertions.assertTrue(idle <= total);

        // 后续串行请求应继续成功（复用空闲连接）
        for (int i = 0; i < 5; i++) {
            Assertions.assertEquals("ok", http(server.url("/ok")).get());
        }
    }

    @Test
    public void redirectUnderConcurrencyDoesNotLeak() throws Exception {
        // 生产：手动 followRedirect 会 close 中间响应以归还连接；并发下应全部成功且不挂死
        final int threads = 20;
        final AtomicInteger success = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        final CountDownLatch ready = new CountDownLatch(threads);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(threads);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            for (int i = 0; i < threads; i++) {
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        ready.countDown();
                        try {
                            start.await();
                            Assertions.assertEquals("ok", http(server.url("/redirect")).get());
                            success.incrementAndGet();
                        } catch (Throwable e) {
                            error.compareAndSet(null, e);
                        } finally {
                            done.countDown();
                        }
                    }
                });
            }

            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            Assertions.assertTrue(done.await(30, TimeUnit.SECONDS));
        } finally {
            pool.shutdownNow();
        }

        if (error.get() != null) {
            throw new AssertionError("concurrent redirect failed", error.get());
        }
        Assertions.assertEquals(threads, success.get());
    }

    @Test
    public void errorResponseCloseDoesNotBreakPool() throws Exception {
        // 生产：4xx/5xx 也必须 close，否则连接不归还
        final int count = 16;
        for (int i = 0; i < count; i++) {
            try (HttpResponse resp = http(server.url("/status?code=500")).exec("GET")) {
                Assertions.assertEquals(500, resp.code());
                Assertions.assertTrue(resp.bodyAsString().contains("500"));
            }
        }

        // 错误响应关闭后，正常请求仍应成功
        Assertions.assertEquals("ok", http(server.url("/ok")).get());
    }

    @Test
    public void configurationMappedToDispatcher() {
        // 配置可能已在其它用例触发懒加载后固化；此处验证当前 loader 值与 HttpConfiguration 一致
        // （首次请求前设置才生效；本断言保证映射路径存在且可读）
        Dispatcher dispatcher = OkHttpDispatcherLoader.getInstance().getDispatcher();
        Assertions.assertEquals(HttpConfiguration.getMaxRequests(), dispatcher.getMaxRequests());
        Assertions.assertEquals(HttpConfiguration.getMaxRequestsPerHost(), dispatcher.getMaxRequestsPerHost());

        ConnectionPool pool = OkHttpDispatcherLoader.getInstance().getConnectionPool();
        Assertions.assertNotNull(pool);
        // 池对象已初始化即可；maxIdle/keepAlive 在构造时读取，运行期不可热改
        Assertions.assertTrue(pool.connectionCount() >= 0);
    }

    @Test
    public void mixedSyncAndAsyncUnderLoad() throws Exception {
        // 生产混合流量：部分线程同步调用，部分异步 fire
        final int syncThreads = 12;
        final int asyncCount = 20;
        final AtomicInteger syncSuccess = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        final CountDownLatch ready = new CountDownLatch(syncThreads);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(syncThreads);

        List<CompletableFuture<HttpResponse>> futures = new ArrayList<CompletableFuture<HttpResponse>>(asyncCount);
        ExecutorService pool = Executors.newFixedThreadPool(syncThreads);
        try {
            for (int i = 0; i < syncThreads; i++) {
                final int idx = i;
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        ready.countDown();
                        try {
                            start.await();
                            Assertions.assertEquals("i=" + idx, http(server.url("/echo?i=" + idx)).get());
                            syncSuccess.incrementAndGet();
                        } catch (Throwable e) {
                            error.compareAndSet(null, e);
                        } finally {
                            done.countDown();
                        }
                    }
                });
            }

            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();

            for (int i = 0; i < asyncCount; i++) {
                futures.add(http(server.url("/echo?a=" + i)).execAsync("GET"));
            }

            Assertions.assertTrue(done.await(30, TimeUnit.SECONDS));
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);
        } finally {
            pool.shutdownNow();
        }

        if (error.get() != null) {
            throw new AssertionError("mixed sync failed", error.get());
        }
        Assertions.assertEquals(syncThreads, syncSuccess.get());

        for (int i = 0; i < asyncCount; i++) {
            HttpResponse resp = futures.get(i).get();
            try {
                Assertions.assertEquals("a=" + i, resp.bodyAsString());
            } finally {
                resp.close();
            }
        }
    }
}
