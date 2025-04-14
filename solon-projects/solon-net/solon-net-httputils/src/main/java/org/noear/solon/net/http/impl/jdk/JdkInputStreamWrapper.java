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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Http 输出流包装器，关闭时流同时关闭连接
 *
 * @author noear
 * @since 3.0
 */
public class JdkInputStreamWrapper extends InputStream {
    private final HttpURLConnection http;
    private final InputStream in;

    public JdkInputStreamWrapper(HttpURLConnection http, InputStream in) {
        this.http = http;
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public void close() throws IOException {
        try {
            if (in != null) {
                in.close();
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }
}