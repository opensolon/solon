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
package org.noear.solon.boot.ssl;

import org.noear.solon.boot.prop.ServerSslProps;

import javax.net.ssl.SSLContext;
import java.io.IOException;

/**
 * @author noear
 * @since 2.5
 * @deprecated 3.5
 * */
@Deprecated
public class SslConfig {
    /**
     * 信号名（http,socket,websocket）
     */
    private String signalName;
    /**
     * Ssl 配置属性
     */
    private ServerSslProps sslProps;
    /**
     * Ssl 上下文
     */
    private SSLContext sslContext;
    /**
     * Ssl 是否启用（默认启用）
     */
    private boolean sslEnable = true;

    public SslConfig(String signalName) {
        this.signalName = signalName;
    }

    /**
     * 是否支持
     * */
    private boolean isSupported() {
        if (sslContext != null) {
            return true;
        }

        if (sslProps == null) {
            sslProps = ServerSslProps.of(signalName);
        }

        return sslProps.isEnable() && sslProps.getSslKeyStore() != null;
    }

    /**
     * 设置
     * */
    public void set(boolean enable, SSLContext sslContext) {
        this.sslEnable = enable;
        this.sslContext = sslContext;
    }

    /**
     * 获取属性（可能是 null）
     * */
    public ServerSslProps getProps() {
        return sslProps;
    }

    /**
     * 是否启用
     * */
    public boolean isSslEnable() {
        return sslEnable && isSupported();
    }

    /**
     * 获取上下文
     * */
    public SSLContext getSslContext() throws IOException {
        if (sslContext != null) {
            return sslContext;
        } else {
            return SslContextFactory.create(sslProps);
        }
    }
}
