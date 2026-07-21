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

/**
 * SSL 或 TLS 版本号
 *
 * @author noear
 * @since 3.5
 */
public interface SslVersions {
    /**
     * @deprecated SSL 协议已不安全，建议使用 TLSv12 或 TLSv13
     */
    @Deprecated
    String SSL = "SSL";
    /**
     * @deprecated SSLv2 已不安全，建议使用 TLSv12 或 TLSv13
     */
    @Deprecated
    String SSLv2 = "SSLv2";
    /**
     * @deprecated SSLv3 已不安全，建议使用 TLSv12 或 TLSv13
     */
    @Deprecated
    String SSLv3 = "SSLv3";
    String TLS = "TLS";
    String TLSv1 = "TLSv1";
    String TLSv11 = "TLSv1.1";
    String TLSv12 = "TLSv1.2";
    /**
     * @since JDK 11+
     */
    String TLSv13 = "TLSv1.3";
}
