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
package org.noear.solon.boot.http;

import javax.net.ssl.SSLContext;
import java.util.concurrent.Executor;

/**
 * Http 服务器编程配置
 *
 * @author noear
 * @since 2.2
 * @deprecated 3.5
 * */
@Deprecated
public interface HttpServerConfigure {
    /**
     * 是否支持 http2
     */
    default boolean isSupportedHttp2() {
        return false;
    }

    /**
     * 启用 http2 （不一定所有服务都支持）
     */
    default void enableHttp2(boolean enable) {

    }

    /**
     * 启用 ssl
     */
    default void enableSsl(boolean enable){
        enableSsl(enable, null);
    }

    /**
     * 启用 ssl（并指定 sslContext）
     */
    void enableSsl(boolean enable, SSLContext sslContext);

    /**
     * 启用调试模式 （不一定所有服务都支持）
     */
    default void enableDebug(boolean enable) {

    }

    /**
     * 添加 HttpPort（当 ssl 时，可再开个 http 端口）
     */
    void addHttpPort(int port);

    /**
     * 设置执行器（线程池）
     */
    void setExecutor(Executor executor);
}
