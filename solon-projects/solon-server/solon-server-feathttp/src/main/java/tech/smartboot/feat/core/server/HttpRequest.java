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
import tech.smartboot.feat.core.common.HttpProtocol;
import tech.smartboot.feat.core.common.io.BodyInputStream;
import tech.smartboot.feat.core.common.multipart.MultipartConfig;
import tech.smartboot.feat.core.common.multipart.Part;
import tech.smartboot.feat.core.server.impl.Upgrade;

import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * HTTP请求处理的核心接口，提供了完整的HTTP请求操作能力。
 * <p>
 * 该接口封装了HTTP请求的所有核心功能，包括：
 * <ul>
 *   <li>请求头的获取与操作</li>
 *   <li>请求参数的获取与解析</li>
 *   <li>请求体内容的读取</li>
 *   <li>Cookie的处理</li>
 *   <li>请求元数据的访问（如远程地址、协议等）</li>
 *   <li>HTTP/2推送支持</li>
 *   <li>协议升级支持</li>
 * </ul>
 * </p>
 *
 * <p>典型使用示例:</p>
 * <pre>
 * String userAgent = httpRequest.getHeader("User-Agent");
 * String paramValue = httpRequest.getParameter("paramName");
 * BodyInputStream inputStream = httpRequest.getInputStream();
 * </pre>
 *
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public interface HttpRequest {
    ThreadLocal<SSLEngine> SSL_ENGINE_THREAD_LOCAL = ThreadLocal.withInitial(() -> null);

    /**
     * 获取与当前请求关联的HTTP响应对象。
     * <p>
     * 通过该方法可以获取对应的响应对象，用于构建HTTP响应内容。
     * </p>
     *
     * @return {@link HttpResponse} 与当前请求关联的响应对象
     */
    HttpResponse getResponse();

    /**
     * 获取指定名称的HTTP请求头值。
     * <p>
     * 如果请求头有多个值，则返回第一个值。
     * 如果请求头不存在，则返回null。
     * </p>
     *
     * @param headName 请求头名称
     * @return 请求头的值，如果不存在则返回null
     */
    String getHeader(String headName);

    default String getHeader(HeaderName headName) {
        return getHeader(headName.getName());
    }


    /**
     * 获取指定名称的所有HTTP请求头值。
     * <p>
     * 返回与指定名称关联的所有请求头值的集合。
     * 如果请求头不存在，则返回空集合。
     * </p>
     *
     * @param name 请求头名称
     * @return 包含所有匹配请求头值的集合
     */
    Collection<String> getHeaders(String name);

    /**
     * 获取所有HTTP请求头的名称。
     * <p>
     * 返回当前请求中所有请求头名称的集合。
     * </p>
     *
     * @return 包含所有请求头名称的集合
     */
    Collection<String> getHeaderNames();

    /**
     * 获取请求体的输入流。
     * <p>
     * 通过该输入流可以读取HTTP请求的请求体内容。
     * 对于POST、PUT等包含请求体的请求方法特别有用。
     * </p>
     *
     * @return {@link BodyInputStream} 用于读取请求体的输入流
     * @throws IOException 如果获取输入流时发生I/O错误
     */
    BodyInputStream getInputStream() throws IOException;

    /**
     * 获取请求的URI路径部分。
     * <p>
     * 返回请求URL的路径部分，不包括协议、主机名、端口号和查询字符串。
     * 例如，对于URL "http://example.com:8080/path/info?query=value"，返回"/path/info"。
     * </p>
     *
     * @return 请求的URI路径部分
     */
    String getRequestURI();

    /**
     * 获取请求使用的HTTP协议版本。
     * <p>
     * 返回客户端使用的HTTP协议版本，如HTTP/1.0、HTTP/1.1或HTTP/2等。
     * </p>
     *
     * @return {@link HttpProtocol} 表示HTTP协议版本的枚举值
     */
    HttpProtocol getProtocol();

    /**
     * 获取HTTP请求的方法名称。
     * <p>
     * 返回HTTP请求使用的方法名称，例如GET、POST或PUT等。
     * 等同于CGI变量REQUEST_METHOD的值。
     * </p>
     *
     * @return 一个指定请求方法名称的字符串，如"GET"、"POST"等
     */
    String getMethod();

    /**
     * 判断当前请求是否通过安全通道（HTTPS）发送。
     * <p>
     * 如果请求是通过HTTPS协议发送的，则返回true；否则返回false。
     * </p>
     *
     * @return 如果请求使用HTTPS协议，则为true；否则为false
     */
    boolean isSecure();

    /**
     * 获取请求的协议方案名称。
     * <p>
     * 返回请求URL的协议方案部分，通常为"http"或"https"。
     * </p>
     *
     * @return 请求的协议方案名称
     */
    String getScheme();

    /**
     * 获取完整的请求URL。
     * <p>
     * 返回客户端用于发送请求的完整URL，包括协议、主机名、端口号和路径，但不包括查询字符串。
     * 例如，对于URL "http://example.com:8080/path/info?query=value"，返回"http://example.com:8080/path/info"。
     * </p>
     *
     * @return 完整的请求URL字符串
     */
    String getRequestURL();

    /**
     * 获取请求URL中的查询字符串部分。
     * <p>
     * 返回URL中"?"后面的部分，不包括"?"字符本身。
     * 如果URL中没有查询字符串，则返回null。
     * </p>
     *
     * @return 查询字符串，如果不存在则返回null
     */
    String getQueryString();

    /**
     * 获取请求的内容类型。
     * <p>
     * 返回请求的Content-Type头的值，指示请求体的MIME类型。
     * 如果请求没有设置Content-Type头，则返回null。
     * </p>
     *
     * @return 请求的内容类型，如果未指定则返回null
     */
    String getContentType();

    /**
     * 获取请求体的内容长度。
     * <p>
     * 返回请求的Content-Length头的值，表示请求体的字节数。
     * 如果请求没有设置Content-Length头，则可能返回-1或默认值。
     * </p>
     *
     * @return 请求体的字节数，如果未知则可能返回-1
     */
    long getContentLength();

    /**
     * 获取指定名称的请求参数值。
     * <p>
     * 返回与指定名称关联的请求参数的值。如果参数有多个值，则返回第一个值。
     * 请求参数可以来自查询字符串或表单提交的数据。
     * </p>
     *
     * @param name 参数名称
     * @return 参数值，如果参数不存在则返回null
     */
    String getParameter(String name);

    /**
     * 获取指定名称的所有请求参数值。
     * <p>
     * 返回与指定名称关联的所有请求参数值的数组。
     * 当一个参数名对应多个值时（如复选框），此方法可以获取所有值。
     * </p>
     *
     * @param name 参数名称
     * @return 包含所有匹配参数值的字符串数组，如果参数不存在则返回null
     */
    String[] getParameterValues(String name);

    /**
     * 获取所有请求参数的映射。
     * <p>
     * 返回包含所有请求参数的映射，其中键为参数名，值为参数值的字符串数组。
     * 每个参数名可能对应一个或多个参数值。
     * </p>
     *
     * @return 包含所有请求参数的映射
     */
    Map<String, String[]> getParameters();

    /**
     * 获取multipart/form-data请求中的所有部分。
     * <p>
     * 使用默认的Multipart配置解析multipart/form-data类型的请求，返回所有Part对象的集合。
     * 每个Part对象代表请求中的一个表单字段或上传的文件。
     * </p>
     *
     * @return 包含所有Part对象的集合
     * @throws IOException 如果解析请求体时发生I/O错误
     */
    default Collection<Part> getParts() throws IOException {
        return getParts(new MultipartConfig());
    }

    /**
     * 使用指定的配置获取multipart/form-data请求中的所有部分。
     * <p>
     * 根据提供的Multipart配置解析multipart/form-data类型的请求，返回所有Part对象的集合。
     * 通过自定义配置可以控制文件上传的大小限制、存储位置等参数。
     * </p>
     *
     * @param configElement Multipart解析的配置对象
     * @return 包含所有Part对象的集合
     * @throws IOException 如果解析请求体时发生I/O错误
     */
    Collection<Part> getParts(MultipartConfig configElement) throws IOException;

    default Part getPart(String name) throws IOException {
        return getParts().stream().filter(part -> part.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * 获取HTTP尾部字段。
     * <p>
     * 返回HTTP请求中的尾部字段映射，这些字段在分块传输编码的最后一个块之后发送。
     * 默认实现返回一个空映射。
     * </p>
     *
     * @return 包含尾部字段的映射，键为字段名，值为字段值
     */
    default Map<String, String> getTrailerFields() {
        return Collections.emptyMap();
    }

    /**
     * 检查HTTP尾部字段是否已准备好。
     * <p>
     * 当使用分块传输编码时，此方法指示尾部字段是否已经可用。
     * 默认实现返回false。
     * </p>
     *
     * @return 如果尾部字段已准备好可以读取，则为true；否则为false
     */
    default boolean isTrailerFieldsReady() {
        return false;
    }

    /**
     * 获取发送请求的客户端或最后一个代理的IP地址。
     * <p>
     * 对于HTTP服务，等同于CGI变量<code>REMOTE_ADDR</code>的值。
     * 该方法返回的是客户端的IP地址字符串表示形式。
     * </p>
     *
     * @return 一个包含发送请求的客户端IP地址的字符串
     */
    String getRemoteAddr();

    /**
     * 获取客户端的网络套接字地址。
     * <p>
     * 返回发送请求的客户端的完整网络地址，包括IP地址和端口号。
     * </p>
     *
     * @return {@link InetSocketAddress} 客户端的网络套接字地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * 获取服务器的本地网络套接字地址。
     * <p>
     * 返回接收请求的服务器端口的网络地址，包括IP地址和端口号。
     * </p>
     *
     * @return {@link InetSocketAddress} 服务器的本地网络套接字地址
     */
    InetSocketAddress getLocalAddress();

    /**
     * 获取发送请求的客户端或最后一个代理的完全限定主机名。
     * <p>
     * 如果服务器无法或选择不解析主机名（为了提高性能），此方法将返回IP地址的点分十进制形式。
     * 对于HTTP服务，等同于CGI变量<code>REMOTE_HOST</code>的值。
     * </p>
     *
     * @return 一个包含客户端完全限定主机名的字符串，或者IP地址（如果主机名无法解析）
     */
    String getRemoteHost();

    /**
     * 获取客户端首选的区域设置。
     * <p>
     * 根据请求的Accept-Language头，返回客户端最优先的区域设置。
     * 如果请求中没有指定Accept-Language头，则返回服务器的默认区域设置。
     * </p>
     *
     * @return {@link Locale} 客户端首选的区域设置
     */
    Locale getLocale();

    /**
     * 获取客户端接受的所有区域设置。
     * <p>
     * 根据请求的Accept-Language头，返回客户端接受的所有区域设置，按优先级排序。
     * 如果请求中没有指定Accept-Language头，则返回只包含服务器默认区域设置的枚举。
     * </p>
     *
     * @return {@link Enumeration} 包含客户端接受的所有区域设置的枚举
     */
    Enumeration<Locale> getLocales();

    /**
     * 获取请求的字符编码。
     * <p>
     * 返回用于解码请求参数的字符编码。
     * 这通常从Content-Type头的charset参数中获取。
     * 如果请求没有指定字符编码，则返回null或服务器的默认编码。
     * </p>
     *
     * @return 请求的字符编码，如果未指定则可能返回null
     */
    String getCharacterEncoding();

    /**
     * 获取请求中的所有Cookie。
     * <p>
     * 返回客户端随请求发送的所有Cookie对象数组。
     * 如果请求中没有Cookie，则返回null或空数组。
     * </p>
     *
     * @return 请求中的Cookie数组，如果没有Cookie则可能为null或空数组
     */
    Cookie[] getCookies();


    /**
     * 获取与当前请求关联的SSL引擎。
     * <p>
     * 如果请求通过HTTPS发送，此

     /**
     * 获取与当前请求关联的SSL引擎。
     * <p>
     * 如果请求通过HTTPS发送，此方法返回用于处理SSL/TLS通信的引擎。
     * 如果请求不是通过HTTPS发送的，则可能返回null。
     * </p>
     *
     * @return {@link SSLEngine} SSL引擎实例，如果不是HTTPS请求则可能为null
     */
    SSLEngine getSslEngine();

    /**
     * 创建一个新的PushBuilder实例，用于HTTP/2服务器推送。
     * <p>
     * 此方法允许服务器在客户端请求资源之前主动推送相关资源，
     * 从而提高页面加载性能。仅在HTTP/2协议下有效。
     * </p>
     *
     * @return {@link PushBuilder} 用于配置服务器推送的构建器，如果不支持推送则返回null
     */
    /**
     * 创建一个新的HTTP/2推送构建器。
     * <p>
     * 在HTTP/2协议中，服务器可以主动推送资源给客户端。
     * 此方法用于创建一个推送构建器，以便配置和发起服务器推送。
     * 如果客户端不支持HTTP/2或服务器未启用推送功能，则返回null。
     * </p>
     *
     * @return {@link PushBuilder} 用于配置HTTP/2服务器推送的构建器，如果不支持则返回null
     */
    default PushBuilder newPushBuilder() {
        return null;
    }

    /**
     * 将HTTP连接升级到其他协议。
     * <p>
     * 此方法用于实现协议升级，如从HTTP/1.1升级到WebSocket。
     * 调用此方法后，原始的HTTP连接将被转换为新协议的连接。
     * </p>
     *
     * @param upgrade {@link Upgrade} 升级处理器
     * @throws IOException 如果在升级过程中发生I/O错误
     */
    /**
     * 将HTTP连接升级到其他协议。
     * <p>
     * 此方法允许将标准的HTTP连接升级到其他协议，如WebSocket或HTTP/2。
     * 升级过程通常在HTTP握手完成后进行，并且需要客户端和服务器的协商。
     * </p>
     *
     * @param upgrade 包含升级信息和处理逻辑的升级对象
     * @throws IOException 如果在升级过程中发生I/O错误
     */
    void upgrade(Upgrade upgrade) throws IOException;
}
