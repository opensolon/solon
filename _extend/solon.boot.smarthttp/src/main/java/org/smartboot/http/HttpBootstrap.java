/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpBootstrap.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http;

import org.smartboot.http.server.HttpMessageProcessor;
import org.smartboot.http.server.HttpRequestProtocol;
import org.smartboot.http.server.Request;
import org.smartboot.http.server.handle.Pipeline;
import org.smartboot.socket.buffer.BufferFactory;
import org.smartboot.socket.buffer.BufferPagePool;
import org.smartboot.socket.transport.AioQuickServer;

import java.io.IOException;

public class HttpBootstrap {


    private AioQuickServer<Request> server;

    /**
     * Http服务端口号
     */
    private int port = 8080;
    /**
     * read缓冲区大小
     */
    private int readBufferSize = 1024;
    /**
     * 服务线程数
     */
    private int threadNum = Runtime.getRuntime().availableProcessors() + 2;

    private int pageSize = 1024 * 1024;

    private int pageNum = threadNum;

    private int writeBufferSize = 1024;
    private String host;
    /**
     * 是否启用控制台banner
     */
    private boolean bannerEnabled = true;
    private HttpMessageProcessor processor = new HttpMessageProcessor();
    /**
     * http消息解码器
     */
    private HttpRequestProtocol protocol = new HttpRequestProtocol();

    /**
     * 设置HTTP服务端端口号
     *
     * @param port
     * @return
     */
    public HttpBootstrap setPort(int port) {
        this.port = port;
        return this;
    }

    public HttpBootstrap host(String host) {
        this.host = host;
        return this;
    }

    public Pipeline<HttpRequest, HttpResponse> pipeline() {
        return processor.pipeline();
    }

    public Pipeline<WebSocketRequest, WebSocketResponse> wsPipeline() {
        return processor.wsPipeline();
    }

    /**
     * 设置read缓冲区大小
     *
     * @param readBufferSize
     * @return
     */
    public HttpBootstrap setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
    }

    /**
     * 设置服务线程数
     *
     * @param threadNum
     * @return
     */
    public HttpBootstrap setThreadNum(int threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    public HttpBootstrap setBufferPool(int pageSize, int pageNum, int chunkSize) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.writeBufferSize = chunkSize;
        return this;
    }

    /**
     * 启动HTTP服务
     */
    public void start() {
        server = new AioQuickServer<>(host, port, protocol, processor);
        server.setReadBufferSize(readBufferSize)
                .setThreadNum(threadNum)
                .setBannerEnabled(this.bannerEnabled)
                .setBufferFactory(new BufferFactory() {
                    @Override
                    public BufferPagePool create() {
                        return new BufferPagePool(pageSize, pageNum, true);
                    }
                })
                .setWriteBuffer(writeBufferSize, 16);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBannerEnabled(boolean bannerEnabled) {
        this.bannerEnabled = bannerEnabled;
    }

    /**
     * 停止服务
     */
    public void shutdown() {
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }
}
