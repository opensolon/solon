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
import io.github.smartboot.socket.buffer.BufferPagePool;
import io.github.smartboot.socket.transport.AioQuickServer;
import tech.smartboot.feat.Feat;
import tech.smartboot.feat.core.common.*;
import tech.smartboot.feat.core.server.impl.HttpEndpoint;
import tech.smartboot.feat.core.server.impl.HttpMessageProcessor;
import tech.smartboot.feat.core.server.impl.HttpRequestProtocol;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class HttpServer {

    private static boolean bannerEnabled = true;
    /**
     * http消息解码器
     */
    private final HttpMessageProcessor processor;
    private final ServerOptions options;
    private final HttpRequestProtocol protocol;
    private AioQuickServer server;
    private boolean started = false;
    private BufferPagePool bufferPagePool;

    public HttpServer() {
        this(new ServerOptions());
    }

    public HttpServer(ServerOptions options) {
        this.options = options;
        this.processor = new HttpMessageProcessor(options);
        this.protocol = new HttpRequestProtocol(options);
    }

    public HttpServer httpHandler(HttpHandler handler) {
        processor.httpServerHandler(handler);
        return this;
    }


    /**
     * 服务配置
     */
    public final ServerOptions options() {
        return options;
    }

    public final HttpServer listen() {
        return listen(options.getPort());
    }

    public final HttpServer listen(int port) {
        listen(options.getHost(), port);
        return this;
    }

    /**
     * 启动HTTP服务
     */
    public synchronized void listen(String host, int port) {
        if (started) {
            throw new RuntimeException("server is running");
        }
        started = true;
        bufferPagePool = new BufferPagePool(options.getThreadNum(), true);
        initByteCache();

        options.getPlugins().forEach(processor::addPlugin);

        Plugin<HttpEndpoint> plugin = options.getSslPlugin();
        if (plugin != null) {
            processor.addPlugin(plugin);
        }
        plugin = options.getDebugPlugin();
        if (plugin != null) {
            processor.addPlugin(plugin);
        }

        server = new AioQuickServer(host, port, protocol, processor);
        server.setThreadNum(options.getThreadNum()).setBannerEnabled(false).setReadBufferSize(options.getReadBufferSize()).setBufferPagePool(bufferPagePool, bufferPagePool).setWriteBuffer(options.getWriteBufferSize(), 16);
        if (!options.isLowMemory()) {
            server.retainReadBuffer();
        }
        try {
            if (options.group() == null) {
                server.start();
            } else {
                server.start(options.group());
            }

            if (bannerEnabled) {
                bannerEnabled = false;
                System.out.println(FeatUtils.getResourceAsString("feat-banner.txt") + "\r\n :: Feat :: (" + Feat.VERSION + ")");
                System.out.println(FeatUtils.getResourceAsString("feat-support.txt"));
                System.out.println("\u001B[32m\uD83C\uDF89Congratulations, the feat startup is successful" + ". cost: " + (System.currentTimeMillis() - options.getStartTime()) + "ms\u001B[0m");
            }
            System.out.println((options.isSecure() ? "https://" : "http://") + (FeatUtils.isBlank(host) ? "0.0.0.0" : host) + ":" + port + "/");
        } catch (Throwable e) {
            System.out.println("\u001B[31m❗feat has failed to start for some reason.\u001B[0m");
            throw new RuntimeException("server start error.", e);
        }
    }

    private void initByteCache() {
        options.getByteCache().addNode(HttpMethod.GET);
        options.getByteCache().addNode(HttpMethod.POST);
        options.getByteCache().addNode(HttpProtocol.HTTP_11.getProtocol(), HttpProtocol.HTTP_11);
        // 缓存一些常用HeaderName
        ByteTree<HeaderName> headerNameByteTree = options.getHeaderNameByteTree();
        headerNameByteTree.addNode(HeaderName.CONTENT_TYPE.getName(), HeaderName.CONTENT_TYPE);
        headerNameByteTree.addNode(HeaderName.CONTENT_LENGTH.getName(), HeaderName.CONTENT_LENGTH);
        headerNameByteTree.addNode(HeaderName.CONNECTION.getName(), HeaderName.CONNECTION);
        headerNameByteTree.addNode(HeaderName.SERVER.getName(), HeaderName.SERVER);
        headerNameByteTree.addNode(HeaderName.DATE.getName(), HeaderName.DATE);
        headerNameByteTree.addNode(HeaderName.ACCEPT.getName(), HeaderName.ACCEPT);
        headerNameByteTree.addNode(HeaderName.ACCEPT_ENCODING.getName(), HeaderName.ACCEPT_ENCODING);
        headerNameByteTree.addNode(HeaderName.ACCEPT_LANGUAGE.getName(), HeaderName.ACCEPT_LANGUAGE);
        headerNameByteTree.addNode(HeaderName.ACCEPT_CHARSET.getName(), HeaderName.ACCEPT_CHARSET);
        headerNameByteTree.addNode(HeaderName.CACHE_CONTROL.getName(), HeaderName.CACHE_CONTROL);
        headerNameByteTree.addNode(HeaderName.CONTENT_ENCODING.getName(), HeaderName.CONTENT_ENCODING);
        headerNameByteTree.addNode(HeaderName.CONTENT_LANGUAGE.getName(), HeaderName.CONTENT_LANGUAGE);
        headerNameByteTree.addNode(HeaderName.CONTENT_LOCATION.getName(), HeaderName.CONTENT_LOCATION);
        headerNameByteTree.addNode(HeaderName.CONTENT_DISPOSITION.getName(), HeaderName.CONTENT_DISPOSITION);
        headerNameByteTree.addNode(HeaderName.USER_AGENT.getName(), HeaderName.USER_AGENT);
        headerNameByteTree.addNode(HeaderName.HOST.getName(), HeaderName.HOST);
        headerNameByteTree.addNode(HeaderName.PRAGMA.getName(), HeaderName.PRAGMA);
        headerNameByteTree.addNode(HeaderName.REFERER.getName(), HeaderName.REFERER);
        // 缓存一些常用字符串
        options.getByteCache().addNode(HeaderValue.TransferEncoding.CHUNKED);
        options.getByteCache().addNode(HeaderValue.ContentEncoding.GZIP);
        options.getByteCache().addNode(HeaderValue.Connection.UPGRADE);
        options.getByteCache().addNode(HeaderValue.Connection.KEEPALIVE);
        options.getByteCache().addNode(HeaderValue.Connection.keepalive);
        options.getByteCache().addNode(HeaderValue.ContentType.MULTIPART_FORM_DATA);
        options.getByteCache().addNode(HeaderValue.ContentType.APPLICATION_JSON);
        options.getByteCache().addNode(HeaderValue.ContentType.X_WWW_FORM_URLENCODED);
        options.getByteCache().addNode(HeaderValue.ContentType.APPLICATION_JSON_UTF8);
        options.getByteCache().addNode(HeaderValue.ContentType.TEXT_HTML_UTF8);
        options.getByteCache().addNode(HeaderValue.ContentType.TEXT_PLAIN_UTF8);
        options.getByteCache().addNode(HeaderValue.NO_CACHE);
    }

    /**
     * 停止服务
     */
    public void shutdown() {
        if (server == null) {
            return;
        }
        //提前将server设置为null，避免在shutdownHook中再次调用shutdown方法
        AioQuickServer thisServer = server;
        server = null;
        Runnable runnable = options.shutdownHook();
        if (runnable != null) {
            try {
                runnable.run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        thisServer.shutdown();
        bufferPagePool.release();
        bufferPagePool = null;
    }
}
