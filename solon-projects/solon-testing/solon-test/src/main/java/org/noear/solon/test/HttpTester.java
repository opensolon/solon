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
package org.noear.solon.test;

import org.noear.solon.Solon;
import org.noear.solon.net.http.HttpUtils;

/**
 * Http 接口测试器
 *
 * @author noear
 * @since 2.2
 */
public class HttpTester {
    public boolean enablePrint() {
        return true;
    }

    /**
     * 请求当前服务
     */
    public HttpUtils path(String path) {
        return path(Solon.cfg().serverPort(), path);
    }

    /**
     * 请求本机服务
     */
    public HttpUtils path(int port, String path) {
        return http("http://localhost:" + port + path);
    }

    /**
     * 请求当前服务
     */
    public HttpUtils path(String protocol, String path) {
        return path(protocol, Solon.cfg().serverPort(), path);
    }

    /**
     * 请求本机服务
     */
    public HttpUtils path(String protocol, int port, String path) {
        return http(protocol + "://localhost:" + port + path);
    }

    /**
     * 请求本机服务
     */
    public HttpUtils http(int port) {
        return http("http", port);
    }

    /**
     * 请求本机服务
     */
    public HttpUtils http(String protocol, int port) {
        return http(protocol + "://localhost:" + port);
    }

    /**
     * 请求服务
     */
    public HttpUtils http(String url) {
        return HttpUtils.http(url).enablePrintln(enablePrint());
    }
}
