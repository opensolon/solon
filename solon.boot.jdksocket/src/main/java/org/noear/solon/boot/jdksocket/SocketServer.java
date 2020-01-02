package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.SocketMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    ServerSocket server;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private SocketContextHandler handler;

    public void setHandler(SocketContextHandler handler){
        this.handler = handler;
    }

    public void start(int port)  {
        new Thread(()->{
            try {
                do_start(port);
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }).start();
    }

    private void do_start(int port) throws IOException{
        server = new ServerSocket(port);

        System.out.println("Server started, waiting for customer connection...");

        while (true) {
            Socket socket = server.accept();
            SocketSession session = new SocketSession(socket);

            pool.execute(() -> {
                while (true) {
                    SocketMessage msg = session.getMessage();
                    pool.execute(()->{
                        handler.handler(session, msg);
                    });
                }
            });
        }
    }

    public void stop(){
        if(server.isClosed()){
            return;
        }

        try {
            server.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
