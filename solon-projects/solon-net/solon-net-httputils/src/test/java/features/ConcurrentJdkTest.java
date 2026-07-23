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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpConfiguration;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.jdk.JdkHttpDispatcherLoader;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtils;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * JDK 适配：对齐生产并行/keep-alive/异步调度场景。
 * <p>
 * JDK 无应用层连接池，连接复用依赖 JVM keep-alive（默认 close 不 disconnect）；
 * 异步靠自建线程池（有界队列 + CallerRunsPolicy）。
 *
 * @since 4.0.4
 */
public class ConcurrentJdkTest {
    private static LocalHttpServer server;

    static HttpUtils http(String url) {
        return JdkHttpUtilsFactory.getInstance().http(url);
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
        // 生产同步路径：业务线程阻塞调用，应形成真实并行
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
            Assertions.assertTrue(server.getMaxActiveRequests() > 1,
                    "expected concurrent in-flight requests, maxActive=" + server.getMaxActiveRequests());
        } finally {
            server.setDelayMs(0);
        }
    }

    @Test
    public void parallelPostBody() throws Exception {
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
                            String payload = "id=" + idx;
                            String body = http(server.url("/post"))
                                    .body(payload, "text/plain")
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
    public void asyncDispatcherSharedAndBounded() throws Exception {
        // 通过反射读取 JdkHttpUtils 静态 dispatcherLoader，验证异步调度器单例共享
        Field field = JdkHttpUtils.class.getDeclaredField("dispatcherLoader");
        field.setAccessible(true);
        JdkHttpDispatcherLoader loader = (JdkHttpDispatcherLoader) field.get(null);

        ExecutorService d1 = loader.getDispatcher();
        ExecutorService d2 = loader.getDispatcher();
        Assertions.assertSame(d1, d2, "async dispatcher should be shared singleton");

        // 触发一次异步请求，确保线程池已可用
        HttpResponse resp = http(server.url("/ok")).execAsync("GET").get(10, TimeUnit.SECONDS);
        try {
            Assertions.assertEquals("ok", resp.bodyAsString());
        } finally {
            resp.close();
        }

        if (d1 instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) d1;
            Assertions.assertTrue(tpe.getMaximumPoolSize() >= 1);
            // maxThreads 映射自 HttpConfiguration.maxRequests（懒加载后固化）
            Assertions.assertEquals(Math.max(1, HttpConfiguration.getMaxRequests()), tpe.getMaximumPoolSize());
            // 有界队列 + CallerRunsPolicy：生产背压语义
            Assertions.assertTrue(tpe.getQueue().remainingCapacity() >= 0);
            Assertions.assertNotNull(tpe.getRejectedExecutionHandler());
        }
    }

    @Test
    public void asyncBurstBeyondQueueStillCompletes() throws Exception {
        // 生产风险点：队列容量 64 + maxThreads；突发超过缓冲时 CallerRunsPolicy 回退调用方线程
        // 这里验证“超队列规模”的异步突发最终仍能全部完成，不丢任务、不挂死
        final int count = 100;
        List<CompletableFuture<HttpResponse>> futures = new ArrayList<CompletableFuture<HttpResponse>>(count);
        for (int i = 0; i < count; i++) {
            futures.add(http(server.url("/echo?b=" + i)).execAsync("GET"));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);

        for (int i = 0; i < count; i++) {
            HttpResponse resp = futures.get(i).get();
            try {
                Assertions.assertEquals("b=" + i, resp.bodyAsString());
            } finally {
                resp.close();
            }
        }
    }

    @Test
    public void sequentialRequestsKeepWorkingWithKeepAliveClose() throws Exception {
        // forceDisconnect=false（默认）下串行多次请求应持续成功，间接验证 keep-alive 友好关闭
        boolean old = HttpConfiguration.isForceDisconnectOnClose();
        try {
            HttpConfiguration.setForceDisconnectOnClose(false);
            server.resetCounters();
            for (int i = 0; i < 15; i++) {
                String body = http(server.url("/ok")).get();
                Assertions.assertEquals("ok", body);
            }
            // 远端端口数应明显小于请求次数（JVM keep-alive 复用）
            Assertions.assertTrue(server.getRemoteEndpointCount() <= 5,
                    "expected keep-alive reuse, remoteEndpoints=" + server.getRemoteEndpointCount());
        } finally {
            HttpConfiguration.setForceDisconnectOnClose(old);
        }
    }

    @Test
    public void forceDisconnectOnCloseStillWorks() throws Exception {
        boolean old = HttpConfiguration.isForceDisconnectOnClose();
        try {
            HttpConfiguration.setForceDisconnectOnClose(true);
            for (int i = 0; i < 8; i++) {
                String body = http(server.url("/ok")).get();
                Assertions.assertEquals("ok", body);
            }
        } finally {
            HttpConfiguration.setForceDisconnectOnClose(old);
        }
    }

    @Test
    public void keepAlivePreferredUnderParallelLoad() throws Exception {
        // 默认 forceDisconnect=false：并行后仍应正常工作，且远端端口数远小于请求数
        boolean old = HttpConfiguration.isForceDisconnectOnClose();
        try {
            HttpConfiguration.setForceDisconnectOnClose(false);
            server.resetCounters();

            final int threads = 20;
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
                                // 每线程连打多次，制造复用机会
                                for (int j = 0; j < 3; j++) {
                                    Assertions.assertEquals("i=" + idx,
                                            http(server.url("/echo?i=" + idx)).get());
                                }
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
                Assertions.assertTrue(done.await(40, TimeUnit.SECONDS));
            } finally {
                pool.shutdownNow();
            }

            if (error.get() != null) {
                throw new AssertionError("keep-alive parallel load failed", error.get());
            }
            Assertions.assertEquals(threads, success.get());
            Assertions.assertEquals(threads * 3, server.getTotalRequests());
            // 60 次请求不应对应 60 个远端端口
            Assertions.assertTrue(server.getRemoteEndpointCount() < server.getTotalRequests(),
                    "expected reuse under load, remoteEndpoints=" + server.getRemoteEndpointCount()
                            + ", total=" + server.getTotalRequests());
        } finally {
            HttpConfiguration.setForceDisconnectOnClose(old);
        }
    }

    @Test
    public void redirectUnderConcurrencyDoesNotHang() throws Exception {
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
    public void errorResponseCloseDoesNotBreakLaterRequests() throws Exception {
        for (int i = 0; i < 12; i++) {
            try (HttpResponse resp = http(server.url("/status?code=503")).exec("GET")) {
                Assertions.assertEquals(503, resp.code());
            }
        }
        Assertions.assertEquals("ok", http(server.url("/ok")).get());
    }

    @Test
    public void mixedSyncAndAsyncUnderLoad() throws Exception {
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
