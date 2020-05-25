package org.noear.fineio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 网络会话
 * */
public abstract class NetSession<T> {
    /**
     * 解码后的消息
     * */
    public abstract T message();

    /**
     * 本地地址
     * */
    public abstract InetSocketAddress getLocalAddress() throws IOException;
    /**
     * 远程地址
     * */
    public abstract InetSocketAddress getRemoteAddress() throws IOException;

    /**
     * 写入
     * */
    public abstract void writeAndFlush(ByteBuffer buffer) throws IOException;

    /**
     * 通道是否开放中
     * */
    public abstract boolean isOpen();

    /**
     * 关闭通道
     * */
    public abstract void close() throws IOException;



    private Object _attachment;
    /**
     * 附件
     * */
    public Object attachment(){
        return _attachment;
    }

    /**
     * 设置附件
     * */
    public void attachmentSet(Object obj){
        _attachment = obj;
    }
}
