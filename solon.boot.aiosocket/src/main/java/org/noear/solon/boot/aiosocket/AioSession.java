package org.noear.solon.boot.aiosocket;

import org.noear.solon.core.SocketMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

public class AioSession {
    AsynchronousSocketChannel channel;
    AioProcessor processor;

    public AioSession(AsynchronousSocketChannel channel){
        this.channel = channel;
        this.processor = new AioProcessor(this);
    }

    public InetSocketAddress getRemoteAddress() throws IOException {
        return (InetSocketAddress)channel.getRemoteAddress();
    }

    public boolean isOpen(){
        return channel.isOpen();
    }

    public void close() throws IOException{
        channel.close();
    }

    public void publish(SocketMessage msg){
        channel.write(msg.encode());
    }
}
