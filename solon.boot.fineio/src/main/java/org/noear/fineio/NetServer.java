package org.noear.fineio;

import org.noear.fineio.nio.NioServer;

import java.io.IOException;

/**
 * 网络服务器
 * */
public abstract class NetServer<T> {
    /**
     * Nio net server
     * */
    public static <T> NetServer<T> nio(Protocol<T> protocol, SessionProcessor<T> processor) {
        NetServer<T> server = new NioServer<T>();
        server.protocol = protocol;
        server.processor = processor;

        return server;
    }

    /**
     * 消息处理器
     * */
    protected SessionProcessor<T> processor;
    protected Protocol<T> protocol;


    /**
     * 启动
     * */
    public abstract void start(int port) throws IOException;

    /**
     * 停止
     */
    public void stop() {
        _stop = true;
    }
    protected boolean _stop = false;
}
