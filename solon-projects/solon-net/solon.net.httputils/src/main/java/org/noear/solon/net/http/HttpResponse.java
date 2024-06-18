package org.noear.solon.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Http 响应
 *
 * @author noear
 * @since 2.8
 */
public interface HttpResponse {
    /**
     * 获取头值
     */
    String header(String name);

    /**
     * 获取头值数组
     */
    List<String> headers(String name);

    /**
     * 获取内容长度
     */
    Long contentLength();

    /**
     * 获取内容类型
     */
    String contentType();

    /**
     * 获取小饼数组
     */
    List<String> cookies();

    /**
     * 获取响应代码
     */
    int code();

    /**
     * 获取响应主体
     */
    InputStream body();

    /**
     * 获取响应主体字节数组
     */
    byte[] bodyAsBytes() throws IOException;

    /**
     * 获取响应主体字符串
     */
    String bodyAsString() throws IOException;
}
