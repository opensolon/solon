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
package org.noear.solon.cloud.trace;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.net.http.HttpExtension;
import org.noear.solon.net.http.HttpConfiguration;
import org.noear.solon.net.http.HttpUtils;

/**
 * Http 跟踪扩展
 *
 * @author noear
 * @since 2.9
 */
public class HttpTraceExtension implements HttpExtension {
    /**
     * 注册扩展
     */
    public static void register() {
        HttpConfiguration.addExtension(new HttpTraceExtension());
    }

    @Override
    public void onInit(HttpUtils httpUtils, String url) {
        if (CloudClient.trace() != null) {
            httpUtils.header(CloudClient.trace().HEADER_TRACE_ID_NAME(), CloudClient.trace().getTraceId());
            httpUtils.header(CloudClient.trace().HEADER_FROM_ID_NAME(), CloudClient.trace().getFromId());
        }
    }
}
