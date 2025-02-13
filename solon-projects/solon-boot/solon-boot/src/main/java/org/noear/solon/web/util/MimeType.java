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
package org.noear.solon.web.util;

import org.noear.solon.Utils;

/**
 * Web 媒体类型
 *
 * @author noear
 * @since 1.10
 */
public final class MimeType {
    public static final String ALL_VALUE = "*/*";
    public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";
    public static final String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";
    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
    public static final String APPLICATION_PDF_VALUE = "application/pdf";
    public static final String APPLICATION_PROBLEM_JSON_VALUE = "application/problem+json";
    public static final String APPLICATION_PROBLEM_JSON_UTF8_VALUE = "application/problem+json;charset=UTF-8";
    public static final String APPLICATION_PROBLEM_XML_VALUE = "application/problem+xml";
    public static final String APPLICATION_RSS_XML_VALUE = "application/rss+xml";
    public static final String APPLICATION_STREAM_JSON_VALUE = "application/stream+json";
    public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";
    public static final String APPLICATION_XML_VALUE = "application/xml";
    public static final String APPLICATION_X_NDJSON_VALUE = "application/x-ndjson";
    public static final String APPLICATION_X_NDJSON_UTF8_VALUE = "application/x-ndjson;charset=UTF-8";
    public static final String IMAGE_GIF_VALUE = "image/gif";
    public static final String IMAGE_JPEG_VALUE = "image/jpeg";
    public static final String IMAGE_PNG_VALUE = "image/png";
    public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";
    public static final String TEXT_EVENT_STREAM_VALUE = "text/event-stream";
    public static final String TEXT_EVENT_STREAM_UTF8_VALUE = "text/event-stream;charset=UTF-8";
    public static final String TEXT_HTML_VALUE = "text/html";
    public static final String TEXT_JSON_VALUE = "text/json";
    public static final String TEXT_MARKDOWN_VALUE = "text/markdown";
    public static final String TEXT_PLAIN_VALUE = "text/plain";
    public static final String TEXT_XML_VALUE = "text/xml";

    /**
     * 是否为流类型
     *
     * @since 3.1
     */
    public static boolean isStreaming(String mimeType) {
        if (Utils.isNotEmpty(mimeType)) {
            if (mimeType.startsWith(APPLICATION_X_NDJSON_VALUE)) {
                return true;
            } else if (mimeType.contains("stream")) {
                return true;
            }
        }

        return false;
    }
}
