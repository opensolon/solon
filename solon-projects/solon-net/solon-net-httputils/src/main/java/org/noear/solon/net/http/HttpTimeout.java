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
package org.noear.solon.net.http;

/**
 * Http 超时：单位：秒
 *
 * @author noear
 * @since 1.7
 */
public class HttpTimeout {
    /**
     * 连接超时
     */
    public final int connectTimeout;
    /**
     * 写超时
     */
    public final int writeTimeout;
    /**
     * 读超时
     */
    public final int readTimeout;

    public HttpTimeout(int timeout) {
        this.connectTimeout = timeout;
        this.writeTimeout = timeout;
        this.readTimeout = timeout;
    }

    public HttpTimeout(int connectTimeout, int writeTimeout, int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.writeTimeout = writeTimeout;
        this.readTimeout = readTimeout;
    }
}
