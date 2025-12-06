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
package org.noear.solon.server.prop;

/**
 * 服务证书属性
 *
 * @author noear
 * @since 2.3
 * @since 3.5
 */
public interface ServerSslProps {
    static ServerSslProps of(String signalName) {
        return new ServerSslPropsImpl(signalName);
    }

    /**
     * 是否启用
     *
     */
    boolean isEnable();

    /**
     * key 密钥库
     *
     */
    String getSslKeyStore();

    /**
     * key 密钥库类型
     *
     */
    String getSslKeyStoreType();

    /**
     * key 密钥库密码
     */
    String getSslKeyStorePassword();

    /**
     * trust 信任库
     */
    String getSslTrustStore();

    /**
     * trust 信任库类型
     */
    String getSslTrustStoreType();

    /**
     * trust 信任库密码
     */
    String getSslTrustStorePassword();


    /**
     * key 密钥库类型
     *
     * @deprecated 3.7.4 {@link #getSslKeyStoreType()}
     */
    @Deprecated
    default String getSslKeyType() {
        return getSslKeyStoreType();
    }


    /**
     * key 密钥库密码
     *
     * @deprecated 3.7.4 {@link #getSslKeyStorePassword()}
     */
    @Deprecated
    default String getSslKeyPassword() {
        return getSslKeyStorePassword();
    }
}