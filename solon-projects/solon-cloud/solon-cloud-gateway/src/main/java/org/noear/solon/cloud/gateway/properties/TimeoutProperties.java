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
package org.noear.solon.cloud.gateway.properties;

/**
 * 超时属性（单位：秒）
 *
 * @author noear
 * @since 2.9
 */
public class TimeoutProperties {
    private int connectTimeout = 10;
    private int requestTimeout = 10;
    private int responseTimeout = 60 * 30;

    public TimeoutProperties() {

    }

    public TimeoutProperties(int timeout) {
        this(timeout, timeout, timeout);
    }

    public TimeoutProperties(int connectTimeout, int requestTimeout, int responseTimeout) {
        this.connectTimeout = connectTimeout;
        this.requestTimeout = requestTimeout;
        this.responseTimeout = responseTimeout;
    }

    /**
     * 连接超时
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 请求超时
     */
    public int getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * 响应超时
     */
    public int getResponseTimeout() {
        return responseTimeout;
    }
}