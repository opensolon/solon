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

import org.noear.solon.Utils;

import java.net.URL;

/**
 * Http 异常
 *
 * @author noear
 * @since 3.1
 */
public class HttpResponseException extends HttpException {
    private final int statusCode;
    private final String statusMessage;
    private byte[] bodyBytes;
    private final String requestMethod;
    private final URL requestUrl;

    public int code() {
        return statusCode;
    }

    public String message() {
        return statusMessage;
    }

    public byte[] bodyBytes() {
        return bodyBytes;
    }

    public String requestMethod() {
        return requestMethod;
    }

    public URL requestUrl() {
        return requestUrl;
    }

    public HttpResponseException(HttpResponse response, String requestMethod, URL requestUrl) {
        super(buildMessage(response, requestMethod, requestUrl));

        this.statusCode = response.code();
        this.statusMessage = response.message();
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;

        try {
            this.bodyBytes = response.bodyAsBytes();
        } catch (Throwable ex) {
            this.bodyBytes = new byte[0];
        }
    }

    public HttpResponseException(HttpResponse response, String requestMethod, URL requestUrl, Throwable cause) {
        super(buildMessage(response, requestMethod, requestUrl), cause);

        this.statusCode = response.code();
        this.statusMessage = response.message();
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;

        try {
            this.bodyBytes = response.bodyAsBytes();
        } catch (Throwable ex) {
            this.bodyBytes = new byte[0];
        }
    }

    private static String buildMessage(HttpResponse response, String requestMethod, URL requestUrl) {
        StringBuilder buf = new StringBuilder();

        buf.append(response.code());
        buf.append(" ");
        if (Utils.isNotEmpty(response.message())) {
            buf.append(response.message());
        }
        buf.append(" from ").append(requestMethod).append(" ");

        buf.append(requestUrl.getProtocol()).append("://").append(requestUrl.getHost());

        //有可能是-1
        if (requestUrl.getPort() > 0) {
            buf.append(":").append(requestUrl.getPort());
        }

        buf.append(requestUrl.getPath());

        return buf.toString();
    }
}