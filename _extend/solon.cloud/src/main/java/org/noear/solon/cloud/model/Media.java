package org.noear.solon.cloud.model;

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

    public Media(InputStream body, String contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    public Media(InputStream body) {
        this(body, null);
    }

    public Media(byte[] body, String contentType) {
        this(new ByteArrayInputStream(body), contentType);
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

    /**
     * 主体
     */
    public InputStream body() {
        return body;
    }

    /**
     * 主体转为字节数组
     * */
    public byte[] bodyAsByts() {
        try {
            return Utils.transferToBytes(body);
        } catch (IOException e) {
            throw new CloudFileException(e);
        }
    }

    /**
     * 主体转为字符串
     */
    public String bodyAsString() {
        try {
            return Utils.transferToString(body, "UTF-8");
        } catch (IOException e) {
            throw new CloudFileException(e);
        }
    }
}
