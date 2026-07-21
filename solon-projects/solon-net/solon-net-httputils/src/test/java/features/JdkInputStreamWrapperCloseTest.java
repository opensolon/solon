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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.noear.solon.net.http.impl.jdk.JdkInputStreamWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests for {@link JdkInputStreamWrapper} close() idempotency and forceDisconnect behavior.
 *
 * @since 4.0.4
 */
class JdkInputStreamWrapperCloseTest {

    @Test
    void testMultipleCloseNoException() {
        JdkInputStreamWrapper wrapper = new JdkInputStreamWrapper(
                null, new ByteArrayInputStream("test".getBytes()), false);

        Assertions.assertDoesNotThrow(() -> {
            wrapper.close();
            wrapper.close();
            wrapper.close();
        });
    }

    @Test
    void testConcurrentClose() throws InterruptedException {
        JdkInputStreamWrapper wrapper = new JdkInputStreamWrapper(
                null, new ByteArrayInputStream("test".getBytes()), false);

        int threadCount = 2;
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        AtomicReference<Throwable> errorRef = new AtomicReference<>();

        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(() -> {
                ready.countDown();
                try {
                    start.await();
                    wrapper.close();
                } catch (Throwable e) {
                    errorRef.compareAndSet(null, e);
                } finally {
                    done.countDown();
                }
            });
            t.start();
        }

        ready.await();
        start.countDown();
        done.await();

        Assertions.assertNull(errorRef.get(), "close() in concurrent threads should not throw");
    }

    @Test
    void testCloseWithNullHttp() {
        // forceDisconnect=true but http=null — close() should not throw
        // because the guard is: if (forceDisconnect && http != null)
        JdkInputStreamWrapper wrapper = new JdkInputStreamWrapper(
                null, new ByteArrayInputStream("test".getBytes()), true);

        Assertions.assertDoesNotThrow(wrapper::close);
    }

    @Test
    void testReadAfterClose() throws IOException {
        // Use a custom InputStream whose read() throws IOException after close,
        // because ByteArrayInputStream.close() is a no-op and read() keeps working.
        InputStream mockStream = new InputStream() {
            private volatile boolean closed = false;

            @Override
            public int read() throws IOException {
                if (closed) {
                    throw new IOException("Stream closed");
                }
                return 'x';
            }

            @Override
            public void close() throws IOException {
                closed = true;
            }
        };

        JdkInputStreamWrapper wrapper = new JdkInputStreamWrapper(null, mockStream, false);

        // Verify read works before close
        Assertions.assertEquals('x', wrapper.read());

        wrapper.close();

        // read() after close() should throw IOException (stream closed)
        Assertions.assertThrows(IOException.class, () -> wrapper.read(),
                "read() after close() should throw IOException (stream closed)");
    }
}