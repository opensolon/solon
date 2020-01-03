package org.noear.solon.boot.aiosocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AioServer {
    private AsynchronousServerSocketChannel serverChannel;

    public AsynchronousServerSocketChannel getServerChannel() {
        return serverChannel;
    }


    public void start(int port){
        new Thread(()->{
            do_start(port);
        }).start();
    }

    public void close() throws IOException{
        serverChannel.close();
    }

    private void do_start(int port) {
        System.out.println("server starting at port "+port+"..");
        // 初始化定长线程池

        try {
            // 初始化 AsyncronousServersocketChannel
            serverChannel = AsynchronousServerSocketChannel.open();
            // 监听端口
            serverChannel.bind(new InetSocketAddress(port));
            // 监听客户端连接,但在AIO，每次accept只能接收一个client，所以需要
            // 在处理逻辑种再次调用accept用于开启下一次的监听
            // 类似于链式调用
            serverChannel.accept(this, new AioHandler());

            try {
                // 阻塞程序，防止被GC回收
                TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
