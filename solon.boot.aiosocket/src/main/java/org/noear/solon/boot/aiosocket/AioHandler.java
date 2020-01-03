package org.noear.solon.boot.aiosocket;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {
    @Override
    public void completed(AsynchronousSocketChannel result, AioServer attachment) {
        // 处理下一次的client连接。类似链式调用
        attachment.getServerChannel().accept(attachment, this);

        // 执行业务逻辑
        AioSession session = new AioSession(result);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 从client读取数据,在我们调用clientChannel.read()之前OS，已经帮我们完成了IO操作
        // 我们只需要用一个缓冲区来存放读取的内容即可
        session.channel.read(
                buffer,   // 用于数据中转缓冲区
                buffer,   // 用于存储client发送的数据的缓冲区
                session.processor);
    }

    /**
     * 异常处理逻辑
     *
     * @param exc
     * @param attachment
     */
    @Override
    public void failed(Throwable exc, AioServer attachment) {
        exc.printStackTrace();
    }
}
