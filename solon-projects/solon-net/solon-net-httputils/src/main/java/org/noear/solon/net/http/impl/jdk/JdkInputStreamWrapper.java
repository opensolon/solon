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
package org.noear.solon.net.http.impl.jdk;

import org.noear.solon.net.http.HttpConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Http 输入流包装器
 * <p>
 * 默认关闭时仅关闭流，不 disconnect，以支持 JVM keep-alive 连接复用。
 * 可通过 {@link HttpConfiguration#setForceDisconnectOnClose(boolean)} 恢复旧行为。
 * <p>
 * 注意：forceDisconnect=true 在高流量场景下会彻底摧毁连接复用，"3. 连接重建开销大幅增加。
 * 仅在服务端不兼容 keep-alive 或调试连接问题时开启。
 *
 * @author noear
 * @since 3.0
 * @since 4.0.4 默认不 disconnect，利于连接复用
 */
public class JdkInputStreamWrapper extends InputStream {
    private final HttpURLConnection http;
    private final InputStream in;
    private final boolean forceDisconnect;
    private volatile boolean closed;

    public JdkInputStreamWrapper(HttpURLConnection http, InputStream in) {
        this(http, in, HttpConfiguration.isForceDisconnectOnClose());
    }

    public JdkInputStreamWrapper(HttpURLConnection http, InputStream in, boolean forceDisconnect) {
        this.http = http;
        this.in = in;
        this.forceDisconnect = forceDisconnect;
    }

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.in.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return this.in.skip(n);
    }

    @Override
    public int available() throws IOException {
        return this.in.available();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.in.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.in.reset();
    }

    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }


    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        closed = true;

        IOException closeEx = null;
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            closeEx = e;
        }

        // 无论 in.close() 是否异常，都执行 disconnect（如果需要）
        // 默认不 disconnect：让连接回到 JVM keep-alive 缓存；异常/重定向路径由 JdkHttpUtils 主动 disconnect
        if (forceDisconnect && http != null) {
            http.disconnect();
        }

        if (closeEx != null) {
            throw closeEx;
        }
    }
}
