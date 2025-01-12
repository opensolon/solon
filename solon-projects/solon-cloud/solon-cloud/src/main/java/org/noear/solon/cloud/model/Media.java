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
package org.noear.solon.cloud.model;

import org.noear.solon.Solon;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.core.util.IoUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 媒体
 *
 * @author noear
 * @since 1.5
 */
public class Media {
    private final InputStream body;
    private final String contentType;
    private final long contentSize;

    public Media(InputStream body, String contentType) {
        this(body, contentType, 0);
    }

    public Media(InputStream body, String contentType, long contentSize) {
        this.body = body;
        this.contentType = contentType;
        this.contentSize = contentSize;
    }

    public Media(InputStream body) {
        this(body, null);
    }

    public Media(byte[] body, String contentType) {
        this(new ByteArrayInputStream(body), contentType, body.length);
    }

    public Media(byte[] body) {
        this(body, null);
    }

    public Media(String body, String contentType) {
        this(body.getBytes(StandardCharsets.UTF_8), contentType);
    }

    public Media(String body) {
        this(body, null);
    }

    /**
     * 内容类型
     */
    public String contentType() {
        return contentType;
    }

    public long contentSize() throws IOException{
        if (contentSize > 0) {
            return contentSize;
        } else {
            return body.available();
        }
    }

    /**
     * 主体
     */
    public InputStream body() {
        return body;
    }

    /**
     * 主体转为字节数组
     */
    public byte[] bodyAsBytes() {
        return bodyAsBytes(false);
    }

    public byte[] bodyAsBytes(boolean autoClose) {
        try {
            if (autoClose) {
                try (InputStream stream = body) {
                    return IoUtil.transferToBytes(stream);
                }
            } else {
                return IoUtil.transferToBytes(body);
            }
        } catch (IOException e) {
            throw new CloudFileException(e);
        }
    }

    /**
     * 主体转为字符串
     */
    public String bodyAsString() {
        return bodyAsString(false);
    }

    public String bodyAsString(boolean autoClose) {
        try {
            if (autoClose) {
                try (InputStream stream = body) {
                    return IoUtil.transferToString(stream, Solon.encoding());
                }
            } else {
                return IoUtil.transferToString(body, Solon.encoding());
            }
        } catch (IOException e) {
            throw new CloudFileException(e);
        }
    }
}
