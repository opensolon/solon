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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

/**
 * Http 响应
 *
 * @author noear
 * @since 2.8
 */
public interface HttpResponse extends Closeable {
    /**
     * 获取头名
     */
    Collection<String> headerNames();

    /**
     * 获取头值
     */
    String header(String name);

    /**
     * 获取头值数组
     */
    List<String> headers(String name);

    /**
     * 获取饼名
     */
    Collection<String> cookieNames();

    /**
     * 获取饼值
     */
    String cookie(String name);

    /**
     * 获取饼值数组
     */
    List<String> cookies(String name);

    /**
     * 获取内容长度
     */
    Long contentLength();

    /**
     * 获取内容类型
     */
    String contentType();

    /**
     * 获取内容字符集
     */
    Charset contentCharset();

    /**
     * @deprecated 3.6
     *
     */
    @Deprecated
    default Charset contentEncoding() {
        return contentCharset();
    }

    /**
     * 获取小饼数组
     */
    List<String> cookies();

    /**
     * 获取状态代码
     */
    int code();

    /**
     * 获取状态消息
     */
    String message();

    /**
     * 获取响应主体
     */
    InputStream body();

    /**
     * 获取响应主体并转为字节数组
     */
    byte[] bodyAsBytes() throws IOException;

    /**
     * 获取响应主体并转为字符串
     */
    String bodyAsString() throws IOException;

    /**
     * 获取响应主体并转为对象
     */
    <T> T bodyAsBean(Type type) throws IOException;

    /**
     * 创建响应异常
     */
    HttpResponseException createError();
}