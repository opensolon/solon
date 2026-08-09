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

import io.github.smartboot.socket.Plugin;
import io.github.smartboot.socket.extension.plugins.ProxyProtocolPlugin;
import io.github.smartboot.socket.extension.plugins.SslPlugin;
import io.github.smartboot.socket.extension.plugins.StreamMonitorPlugin;
import tech.smartboot.feat.core.common.ByteTree;
import tech.smartboot.feat.core.common.HeaderName;
import tech.smartboot.feat.core.server.impl.HttpEndpoint;
import tech.smartboot.feat.core.server.waf.WafOptions;

import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Feat HTTP 服务器配置类，用于配置服务器的各项参数。
 * <p>
 * 该类提供了丰富的配置选项，包括但不限于：
 * <ul>
 *   <li>基础配置：服务器名称、Banner显示等</li>
 *   <li>性能配置：线程数、缓冲区大小等</li>
 *   <li>安全配置：请求大小限制、Header数量限制等</li>
 *   <li>调试配置：调试模式开关等</li>
 * </ul>
 * <p>
 * 示例用法：
 * <pre>
 * Feat.httpServer(options -> {
 *     options.serverName("my-app");
 *     options.threadNum(8);
 *     options.readBufferSize(16 * 1024);
 * }).listen();
 * </pre>
 *
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class ServerOptions {
    private long startTime = System.currentTimeMillis();
    private int port = 8080;

    private String host = "0.0.0.0";

    /**
     * 字节缓存树，用于缓存字符串以提高性能
     * <p>
     * 在 HTTP 报文解析过程中，将频繁使用的字符串缓存到 ByteTree 中可以显著提高性能。
     * 适用范围包括：URL、HeaderName、HeaderValue 等。
     */
    private final ByteTree<Object> byteCache = new ByteTree<>();

    /**
     * URI 路由缓存树
     * <p>
     * 用于存储 URI 与对应处理器的映射关系，提高路由查找效率。
     */
    private final ByteTree<HttpHandler> uriByteTree = new ByteTree<>();

    /**
     * HTTP 请求头名称缓存树
     * <p>
     * 用于缓存常用的 HTTP 请求头名称，提高请求头解析效率。
     */
    private final ByteTree<HeaderName> headerNameByteTree = new ByteTree<>();

    /**
     * smart-socket 插件列表
     * <p>
     * 用于扩展服务器功能，如 SSL 支持、代理协议支持、流量监控等。
     */
    private final List<Plugin<HttpEndpoint>> plugins = new ArrayList<>();

    private Plugin<HttpEndpoint> sslPlugin;

    private Plugin<HttpEndpoint> debugPlugin;

    /**
     * 读缓冲区大小（字节）
     * <p>
     * 该缓冲区用于存储客户端发送的 HTTP 请求数据。
     * 缓冲区大小至少要能容纳一个完整的 URL 或 Header 值，否则将触发异常。
     * <p>
     * 对于处理大量小请求的场景，保持默认值即可；
     * 对于需要处理大请求体的场景，可适当增加缓冲区大小。
     */
    private int readBufferSize = 8 * 1024;

    /**
     * 写缓冲区大小（字节）
     * <p>
     * 该缓冲区用于存储发送给客户端的 HTTP 响应数据。
     * 对于需要响应大文件或大数据集的场景，可适当增加缓冲区大小。
     */
    private int writeBufferSize = 8 * 1024;

    /**
     * 服务线程数
     * <p>
     * 默认值为 CPU 核心数（至少为 2）。
     * <p>
     * 最佳实践：
     * <ul>
     *   <li>IO 密集型应用：线程数可设置为 CPU 核数的 2-4 倍</li>
     *   <li>CPU 密集型应用：线程数建议设置为 CPU 核数的 1-2 倍</li>
     * </ul>
     */
    private int threadNum = Math.max(Runtime.getRuntime().availableProcessors(), 2);

    /**
     * HTTP 请求头数量上限
     * <p>
     * 若客户端提交的 Header 数量超过该值，超过部分将被忽略。
     * 该限制有助于防止 HTTP 头部攻击，保护服务器资源。
     */
    private int headerLimiter = 100;

    /**
     * 连接闲置超时时间（毫秒）
     * <p>
     * 当客户端连接在指定时间内没有数据交互时，服务器将关闭该连接。
     * 默认值为 60000 毫秒（1分钟）。
     */
    private long idleTimeout = 60000;

    /**
     * 是否启用加密通信（HTTPS）
     * <p>
     * 当添加 SslPlugin 插件时，该值会自动设置为 true。
     */
    private boolean secure;

    /**
     * 最大请求报文大小（字节）
     * <p>
     * 用于限制客户端可发送的最大请求大小，防止恶意大请求攻击。
     * 对于文件上传场景，需要适当调大此值。
     */
    private long maxRequestSize = Integer.MAX_VALUE;

    /**
     * 是否启用低内存模式
     * <p>
     * 在资源受限的环境中（如嵌入式设备），可启用此模式以减少内存占用。
     */
    private boolean lowMemory = false;

    /**
     * 异步通道组
     * <p>
     * 用于管理异步 IO 操作的线程组。
     */
    private AsynchronousChannelGroup group;

    /**
     * Web 应用防火墙配置选项
     */
    private final WafOptions wafOptions = new WafOptions();

    /**
     * 服务器关闭钩子
     * <p>
     * 当服务器关闭时，会执行该钩子函数。
     * 可用于执行资源清理、状态保存等操作。
     */
    private Runnable shutdownHook;

    /**
     * 获取读缓冲区大小
     *
     * @return 读缓冲区大小（字节）
     */
    int getReadBufferSize() {
        return readBufferSize;
    }

    /**
     * 设置读缓冲区大小
     * <p>
     * 读缓冲区的大小至少要能容纳一个完整的 URL 或 Header 值，否则将触发异常。
     * 对于处理大请求体的场景（如文件上传），可适当增加缓冲区大小。
     *
     * @param readBufferSize 读缓冲区大小（字节）
     * @return 当前 ServerOptions 实例，支持链式调用
     */
    public ServerOptions readBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
    }

    /**
     * 获取服务线程数
     *
     * @return 服务线程数
     */
    int getThreadNum() {
        return threadNum;
    }

    /**
     * 设置服务线程数
     * <p>
     * 线程数直接影响服务器的并发处理能力。
     * <p>
     * 最佳实践：
     * <ul>
     *   <li>IO 密集型应用：线程数可设置为 CPU 核数的 2-4 倍</li>
     *   <li>CPU 密集型应用：线程数建议设置为 CPU 核数的 1-2 倍</li>
     * </ul>
     *
     * @param threadNum 服务线程数
     * @return 当前 ServerOptions 实例，支持链式调用
     */
    public ServerOptions threadNum(int threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    /**
     * 获取写缓冲区大小
     *
     * @return 写缓冲区大小（字节）
     */
    protected int getWriteBufferSize() {
        return writeBufferSize;
    }

    /**
     * 设置写缓冲区大小
     * <p>
     * 写缓冲区用于存储发送给客户端的 HTTP 响应数据。
     * 对于需要响应大文件或大数据集的场景，可适当增加缓冲区大小。
     *
     * @param writeBufferSize 写缓冲区大小（字节）
     * @return 当前 ServerOptions 实例，支持链式调用
     */
    public ServerOptions writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
    }

    /**
     * 获取 HTTP 请求头数量上限
     *
     * @return HTTP 请求头数量上限
     */
    public int getHeaderLimiter() {
        return headerLimiter;
    }

    /**
     * 设置 HTTP 请求头数量上限
     * <p>
     * 若客户端提交的 Header 数量超过该值，超过部分将被忽略。
     * 该限制有助于防止 HTTP 头部攻击，保护服务器资源。
     *
     * @param headerLimiter HTTP 请求头数量上限
     * @return 当前 ServerOptions 实例，支持链式调用
     */
    public ServerOptions headerLimiter(int headerLimiter) {
        this.headerLimiter = headerLimiter;
        return this;
    }

    /**
     * 启用代理协议支持
     * <p>
     * 当服务器部署在代理（如 Nginx、HAProxy）后面时，启用此选项可以获取客户端的真实 IP 地址。
     *
     * @return 当前 ServerOptions 实例，支持链式调用
     */
    public ServerOptions proxyProtocolSupport() {
        plugins.add(0, new ProxyProtocolPlugin<>());
        return this;
    }

    /**
     * 设置是否启用调试模式
     * <p>
     * 启用调试模式后，服务器会在控制台打印请求和响应的详细信息，便于开发调试。
     * 在生产环境中，应关闭调试模式以避免性能下降和信息泄露。
     *
     * @param debug 是否启用调试模式
     * @return 当前 ServerOptions 实例，支持链式调用
     */
    public ServerOptions debug(boolean debug) {
        if (debug) {
            debugPlugin = new StreamMonitorPlugin<>(StreamMonitorPlugin.BLUE_TEXT_INPUT_STREAM, StreamMonitorPlugin.RED_TEXT_OUTPUT_STREAM);
        } else {
            debugPlugin = null;
        }
        return this;
    }

    /**
     * 获取 URI 路由缓存树
     *
     * @return URI 路由缓存树
     */
    public ByteTree<HttpHandler> getUriByteTree() {
        return uriByteTree;
    }

    /**
     * 获取字节缓存树
     * <p>
     * 将字符串缓存至 ByteTree 中，在 HTTP 报文解析过程中将获得更好的性能表现。
     * 适用范围包括：URL、HeaderName、HeaderValue 等。
     *
     * @return 字节缓存树
     */
    public ByteTree<Object> getByteCache() {
        return byteCache;
    }

    /**
     * 获取 HTTP 请求头名称缓存树
     *
     * @return HTTP 请求头名称缓存树
     */
    public ByteTree<HeaderName> getHeaderNameByteTree() {
        return headerNameByteTree;
    }

    /**
     * 添加服务器插件
     * <p>
     * 插件可用于扩展服务器功能，如 SSL 支持、代理协议支持、流量监控等。
     * 如果添加的是 SslPlugin 插件，服务器将自动启用 HTTPS。
     *
     * @param plugin 要添加的插件
     * @return 当前 ServerOptions 实例，支持链式调用
     */
    public ServerOptions addPlugin(Plugin<HttpEndpoint> plugin) {
        if (plugin instanceof SslPlugin) {
            sslPlugin = plugin;
            secure = true;
        } else if (plugin instanceof StreamMonitorPlugin) {
            debugPlugin = plugin;
        } else {
            plugins.add(plugin);
        }
        return this;
    }

    /**
     * 判断是否启用加密通信（HTTPS）
     *
     * @return 如果启用返回 true，否则返回 false
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * 获取最大请求报文大小
     *
     * @return 最大请求报文大小（字节）
     */
    public long getMaxRequestSize() {
        return maxRequestSize;
    }

    /**
     * 设置最大请求报文大小
     * <p>
     * 用于限制客户端可发送的最大请求大小，防止恶意大请求攻击。
     * 对于文件上传场景，需要适当调大此值。
     *
     * @param maxRequestSize 最大请求报文大小（字节）
     */
    public ServerOptions setMaxRequestSize(long maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
        return this;
    }

    /**
     * 获取所有已添加的服务器插件
     *
     * @return 服务器插件列表
     */
    List<Plugin<HttpEndpoint>> getPlugins() {
        return plugins;
    }

    Plugin<HttpEndpoint> getSslPlugin() {
        return sslPlugin;
    }

    Plugin<HttpEndpoint> getDebugPlugin() {
        return debugPlugin;
    }

    /**
     * 获取异步通道组
     *
     * @return 异步通道组
     */
    public AsynchronousChannelGroup group() {
        return group;
    }

    /**
     * 设置异步通道组
     * <p>
     * 用于管理异步 IO 操作的线程组。
     *
     * @param group 异步通道组
     * @return 当前 ServerOptions 实例，支持链式调用
     */
    public ServerOptions group(AsynchronousChannelGroup group) {
        this.group = group;
        return this;
    }

    /**
     * 获取 Web 应用防火墙配置选项
     *
     * @return Web 应用防火墙配置选项
     */
    public WafOptions getWafOptions() {
        return wafOptions;
    }

    /**
     * 获取连接闲置超时时间
     *
     * @return 连接闲置超时时间（毫秒）
     */
    public long getIdleTimeout() {
        return idleTimeout;
    }

    /**
     * 设置连接闲置超时时间
     * <p>
     * 当客户端连接在指定时间内没有数据交互时，服务器将关闭该连接。
     *
     * @param idleTimeout 连接闲置超时时间（毫秒）
     * @return 当前 ServerOptions 实例，支持链式调用
     */
    public ServerOptions setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }

    /**
     * 判断是否启用低内存模式
     *
     * @return 如果启用返回 true，否则返回 false
     */
    boolean isLowMemory() {
        return lowMemory;
    }

    /**
     * 设置是否启用低内存模式
     * <p>
     * 在资源受限的环境中（如嵌入式设备），可启用此模式以减少内存占用。
     *
     * @param lowMemory 是否启用低内存模式
     * @return 当前 ServerOptions 实例，支持链式调用
     */
    public ServerOptions setLowMemory(boolean lowMemory) {
        this.lowMemory = lowMemory;
        return this;
    }

    /**
     * 获取服务器关闭钩子
     *
     * @return 服务器关闭钩子
     */
    public Runnable shutdownHook() {
        return shutdownHook;
    }

    /**
     * 设置服务器关闭钩子
     * <p>
     * 当服务器关闭时，会执行该钩子函数。
     * 可用于执行资源清理、状态保存等操作。
     *
     * @param shutdownHook 服务器关闭钩子
     * @return 当前 ServerOptions 实例，支持链式调用
     */
    public ServerOptions shutdownHook(Runnable shutdownHook) {
        this.shutdownHook = shutdownHook;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ServerOptions port(int port) {
        this.port = port;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ServerOptions host(String host) {
        this.host = host;
        return this;
    }

    public ServerOptions startTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    public long getStartTime() {
        return startTime;
    }
}
