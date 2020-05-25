package org.noear.fineio.nio;

import org.noear.fineio.NetSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 会话
 * */
public class NioSession<T> extends NetSession<T> {
    private SocketChannel _channel;
    private  T _message;

    protected NioSession(SocketChannel channel, T message){
        _channel = channel;
        _message = message;
    }

    /**
     * 写缓存
     * */
    @Override
    public void writeAndFlush(ByteBuffer buffer) throws IOException{
         _channel.write(buffer);
    }

    /**
     * 解码后的消息
     * */
    @Override
    public T message(){
        return _message;
    }

    @Override
    public  InetSocketAddress getLocalAddress() throws IOException{
        return (InetSocketAddress)_channel.getLocalAddress();
    }

    @Override
    public  InetSocketAddress getRemoteAddress() throws IOException{
        return (InetSocketAddress)_channel.getRemoteAddress();
    }

    @Override
    public boolean isOpen() {
        return _channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        _channel.close();
    }
}
