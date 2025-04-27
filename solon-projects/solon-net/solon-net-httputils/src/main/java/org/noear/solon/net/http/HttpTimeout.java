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

import java.io.Serializable;
import java.time.Duration;

/**
 * Http 超时
 *
 * @author noear
 * @since 1.7
 */
public class HttpTimeout implements Serializable {
    /**
     * 连接超时
     */
    private Duration connectTimeout;
    /**
     * 写超时
     */
    private Duration writeTimeout;
    /**
     * 读超时
     */
    private Duration readTimeout;

    public HttpTimeout() {
        //用于反序列化
    }

    /**
     * @param timeout 所有超时（单位：秒）
     */
    public static HttpTimeout of(int timeout) {
        HttpTimeout tmp = new HttpTimeout();
        tmp.connectTimeout = Duration.ofSeconds(timeout);
        tmp.writeTimeout = Duration.ofSeconds(timeout);
        tmp.readTimeout = Duration.ofSeconds(timeout);
        return tmp;
    }

    /**
     * @param connectTimeout 连接超时（单位：秒）
     * @param writeTimeout   写超时（单位：秒）
     * @param readTimeout    读超时（单位：秒）
     */
    public static HttpTimeout of(int connectTimeout, int writeTimeout, int readTimeout) {
        HttpTimeout tmp = new HttpTimeout();
        tmp.connectTimeout = Duration.ofSeconds(connectTimeout);
        tmp.writeTimeout = Duration.ofSeconds(writeTimeout);
        tmp.readTimeout = Duration.ofSeconds(readTimeout);
        return tmp;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Duration getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Duration writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
}