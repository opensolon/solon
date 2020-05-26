package org.noear.fineio.nio;

import org.noear.fineio.NetServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioTcpServer<T> extends NetServer<T> {
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private Selector selector;

    /**
     * 开始
     * */
    @Override
    public void start(int port) throws IOException {
        ServerSocketChannel ssc =  ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(port));

        selector = Selector.open();

        ssc.register(selector, SelectionKey.OP_ACCEPT);

        startDo();
    }

    private void startDo(){
        new Thread(()->{
            while (!_stop){
                try {
                    if (selector.select(1000) < 1) {
                        continue;
                    }

                    Iterator<SelectionKey> keyS = selector.selectedKeys().iterator();

                    while (keyS.hasNext()) {
                        SelectionKey key = keyS.next();
                        keyS.remove();

                        if (key.isValid() == false) {
                            continue;
                        }

                        selectDo(key);
                    }
                }catch (Throwable ex){
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void selectDo(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            if (channel == null) {
                return;
            }

            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            return;
        }

        if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();

            buffer.clear();
            channel.read(buffer);
            buffer.flip();

            T message = protocol.decode(buffer);

            if(message != null) {
                //
                //如果message没有问题，则执行处理
                //
                NioTcpSession<T> session = new NioTcpSession<>(channel, message);

                processor.process(session);
            }
        }
    }
}
