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
package org.noear.solon.net.websocket;

import org.noear.solon.Utils;
import org.noear.solon.core.util.MultiMap;

import java.net.URI;

/**
 * 握手信息实现类
 *
 * @author noear
 * @since 2.6
 */
public class HandshakeImpl implements Handshake {
    private final String url;
    private final URI uri;
    private final MultiMap<String> paramMap;

    public HandshakeImpl(URI uri) {
        this.uri = uri;
        this.url = uri.toString();
        this.paramMap = new MultiMap<>();

        String queryString = uri.getQuery();
        if (Utils.isNotEmpty(queryString)) {
            for (String kvStr : queryString.split("&")) {
                int idx = kvStr.indexOf('=');
                if (idx > 0) {
                    paramMap.add(kvStr.substring(0, idx), kvStr.substring(idx + 1));
                }
            }
        }
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    public MultiMap<String> getParamMap() {
        return paramMap;
    }
}
