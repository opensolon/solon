/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server;

import tech.smartboot.feat.core.common.Cookie;
import tech.smartboot.feat.core.common.HeaderName;
import tech.smartboot.feat.core.common.HttpStatus;
import tech.smartboot.feat.core.common.io.FeatOutputStream;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * HTTP响应处理的核心接口，提供了完整的HTTP响应操作能力。
 * <p>
 * 该接口封装了HTTP响应的所有核心功能，包括：
 * <ul>
 *   <li>响应状态码的设置与获取</li>
 *   <li>响应头的操作(添加、设置、获取)</li>
 *   <li>响应体内容的写入</li>
 *   <li>Cookie的处理</li>
 *   <li>响应流的控制</li>
 * </ul>
 * </p>
 * 
 * <p>典型使用示例:</p>
 * <pre>
 * httpResponse.setHttpStatus(HttpStatus.OK);
 * httpResponse.setContentType("text/plain;charset=utf-8");
 * httpResponse.write("Hello World".getBytes());
 * httpResponse.close();
 * </pre>
 * 
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public interface HttpResponse {

    /**
     * 获取响应消息的输出流。
     * <p>
     * 通过该输出流可以直接写入响应体内容。使用完成后需要调用{@link #close()}方法关闭流。
     * </p>
     *
     * @return {@link FeatOutputStream} 用于写入响应内容的输出流
     */
    FeatOutputStream getOutputStream();

    /**
     * 获取当前设置的HTTP响应状态码。
     * <p>
     * 如果未显式设置状态码，默认返回{@link HttpStatus#OK} (200 OK)。
     * </p>
     *
     * @return {@link HttpStatus} 当前的HTTP响应状态
     */
    HttpStatus getHttpStatus();

    /**
     * 设置HTTP响应状态码。
     * <p>
     * 通过该方法可以设置标准的HTTP状态码，例如200(OK)、404(Not Found)等。
     * 如果不设置，默认使用{@link HttpStatus#OK}。
     * </p>
     *
     * @param httpStatus 要设置的HTTP状态码，不能为null
     */
    void setHttpStatus(HttpStatus httpStatus);

    /**
     * 设置自定义的HTTP响应状态码和原因短语。
     * <p>
     * 当需要使用非标准HTTP状态码时，可以通过该方法设置状态码和对应的原因短语。
     * 如果不设置，默认使用{@link HttpStatus#OK}。
     * </p>
     *
     * @param httpStatus 自定义的HTTP状态码
     * @param reasonPhrase 状态码对应的原因短语
     */
    void setHttpStatus(int httpStatus, String reasonPhrase);


    /**
     * 设置HTTP响应头。
     * <p>
     * 如果指定名称的响应头已存在，新的值将覆盖原有值。
     * 响应头的值如果包含非ASCII字符，应按照RFC 2047进行编码。
     * </p>
     *
     * @param name 响应头名称
     * @param value 响应头的值
     */
    void setHeader(String name, String value);

    default void setHeader(HeaderName name, String value) {
        setHeader(name.getName(), value);
    }

    /**
     * 添加HTTP响应头。
     * <p>
     * 该方法允许为同一个响应头名称添加多个值，而不是覆盖已有的值。
     * 响应头的值如果包含非ASCII字符，应按照RFC 2047进行编码。
     * </p>
     *
     * @param name 响应头名称
     * @param value 要添加的响应头值
     */
    void addHeader(String name, String value);

    default void addHeader(HeaderName name, String value) {
        addHeader(name.getName(), value);
    }

    default String getHeader(HeaderName name) {
        return getHeader(name.getName());
    }

    /**
     * 获取指定名称的响应头的值。
     * <p>
     * 如果该响应头有多个值，则返回第一个值。
     * 如果该响应头不存在，则返回null。
     * </p>
     *
     * @param name 要获取的响应头名称
     * @return 响应头的值，如果不存在则返回null
     */
    String getHeader(String name);

    Collection<String> getHeaders(String name);

    default Collection<String> getHeaders(HeaderName name) {
        return getHeaders(name.getName());
    }

    Collection<String> getHeaderNames();

    /**
     * 设置响应体的内容长度。
     * <p>
     * 设置Content-Length响应头，告知客户端响应体的字节数。
     * 在发送固定长度的响应时应当设置此值。
     * </p>
     *
     * @param contentLength 响应体的字节数
     */
    void setContentLength(long contentLength);

    long getContentLength();

    /**
     * 设置响应的内容类型。
     * <p>
     * 设置Content-Type响应头，指定响应体的MIME类型和字符编码。
     * 例如："text/html;charset=utf-8"、"application/json;charset=utf-8"等。
     * </p>
     *
     * @param contentType 内容类型，包含MIME类型和可选的字符编码
     */
    void setContentType(String contentType);

    String getContentType();

    default void write(String data) throws IOException {
        write(data.getBytes());
    }

    /**
     * 写入字节数组的指定部分到响应体。
     * <p>
     * 该方法允许写入字节数组的一个子序列到响应体中。
     * </p>
     *
     * @param data 要写入的字节数组
     * @param offset 开始位置的偏移量
     * @param length 要写入的字节数
     * @throws IOException 如果写入过程中发生I/O错误
     */
    void write(byte[] data, int offset, int length) throws IOException;

    /**
     * 写入字节数组到响应体。
     * <p>
     * 该方法将整个字节数组写入到响应体中。
     * </p>
     *
     * @param data 要写入的字节数组
     * @throws IOException 如果写入过程中发生I/O错误
     */
    void write(byte[] data) throws IOException;

    /**
     * 关闭响应。
     * <p>
     * 完成响应内容的写入后，应当调用此方法关闭响应。
     * 这将确保所有缓冲的内容被刷新并发送到客户端。
     * </p>
     */
    void close();

    /**
     * 添加Cookie到响应中。
     * <p>
     * 通过该方法可以向客户端设置Cookie。添加的Cookie将通过Set-Cookie响应头发送给客户端。
     * Cookie可以包含名称、值、过期时间、路径等属性。
     * </p>
     *
     * @param cookie 要添加的Cookie对象，包含Cookie的所有属性
     */
    void addCookie(Cookie cookie);

    /**
     * Sets the supplier of trailer headers.
     *
     * <p>
     * The trailer header field value is defined as a comma-separated list (see Section 3.2.2 and Section 4.1.2 of RFC
     * 7230).
     * </p>
     *
     * <p>
     * The supplier will be called within the scope of whatever thread/call causes the response content to be completed.
     * Typically this will be any thread calling close() on the output stream or writer.
     * </p>
     *
     * <p>
     * The trailers that run afoul of the provisions of section 4.1.2 of RFC 7230 are ignored.
     * </p>
     *
     * <p>
     * The RFC requires the name of every key that is to be in the supplied Map is included in the comma separated list that
     * is the value of the "Trailer" response header. The application is responsible for ensuring this requirement is met.
     * Failure to do so may lead to interoperability failures.
     * </p>
     *
     * @param supplier the supplier of trailer headers
     * @throws IllegalStateException if it is invoked after the response has has been committed, or the trailer is not
     *                               supported in the request, for instance, the underlying protocol is HTTP 1.0, or the response is not in chunked
     *                               encoding in HTTP 1.1.
     * @implSpec The default implementation is a no-op.
     * @since Servlet 4.0
     */
    default void setTrailerFields(Supplier<Map<String, String>> supplier) {
    }

    /**
     * Gets the supplier of trailer headers.
     *
     * @return <code>Supplier</code> of trailer headers
     * @implSpec The default implememtation return null.
     * @since Servlet 4.0
     */
    default Supplier<Map<String, String>> getTrailerFields() {
        return null;
    }

}
