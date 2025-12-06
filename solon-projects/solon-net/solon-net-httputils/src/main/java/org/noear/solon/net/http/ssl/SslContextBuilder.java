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
package org.noear.solon.net.http.ssl;

import org.noear.solon.Utils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;

/**
 * SSLContext 构建器
 *
 * @author noear
 * @since 3.5
 * */
public class SslContextBuilder {
    private String protocol = SslVersions.TLS;
    private Provider provider = null;
    private SecureRandom secureRandom = null;

    private KeyManager[] keyManagers;
    private TrustManager[] trustManagers;


    /**
     * 创建 SSLContextBuilder
     */
    public static SslContextBuilder of() {
        return new SslContextBuilder();
    }

    /**
     * 配置协议
     */
    public SslContextBuilder protocol(final String protocol) {
        if (protocol != null) {
            this.protocol = protocol;
        }

        return this;
    }

    /**
     * 配置提供者
     */
    public SslContextBuilder provider(final Provider provider) {
        this.provider = provider;
        return this;
    }

    /**
     * 配置安全随机
     */
    public SslContextBuilder secureRandom(final SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }


    /**
     * 配置信任信息
     */
    public SslContextBuilder trustManagers(final TrustManager... trustManagers) {
        if (Utils.isNotEmpty(trustManagers)) {
            this.trustManagers = trustManagers;
        }
        return this;
    }

    /**
     * 配置密钥管理
     */
    public SslContextBuilder keyManagers(final KeyManager... keyManagers) {
        if (Utils.isNotEmpty(keyManagers)) {
            this.keyManagers = keyManagers;
        }
        return this;
    }


    /**
     * 构建
     */
    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sslContext;
        if (null != this.provider) {
            sslContext = SSLContext.getInstance(protocol, provider);
        } else {
            sslContext = SSLContext.getInstance(protocol);
        }

        sslContext.init(this.keyManagers, this.trustManagers, this.secureRandom);
        return sslContext;
    }
}