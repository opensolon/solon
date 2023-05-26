package org.noear.solon.cloud.model;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.exception.CloudFileException;

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
                    return Utils.transferToBytes(stream);
                }
            } else {
                return Utils.transferToBytes(body);
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
                    return Utils.transferToString(stream, Solon.encoding());
                }
            } else {
                return Utils.transferToString(body, Solon.encoding());
            }
        } catch (IOException e) {
            throw new CloudFileException(e);
        }
    }
}
