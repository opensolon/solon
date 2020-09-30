package org.noear.solon.boot.jdksocket;

import org.noear.solon.api.socket.SocketMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    private ServerSocket server;
    private SocketProtocol protocol;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private SocketContextHandler handler;

    public void setHandler(SocketContextHandler handler){
        this.handler = handler;
    }

    public void setProtocol(SocketProtocol protocol) {
        this.protocol = protocol;
    }

    public void start(int port)  {
        new Thread(()->{
            try {
                start0(port);
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }).start();
    }

    private void start0(int port) throws IOException {
        server = new ServerSocket(port);

        System.out.println("Server started, waiting for customer connection...");

        while (true) {
            Socket connector = server.accept();
            SocketSession session = new SocketSession(connector);

            pool.execute(() -> {
                while (true) {
                    if(session.isOpen() == false){
                        return;
                    }

                    SocketMessage msg = session.receive(protocol);
                    if (msg != null) {
                        pool.execute(() -> {
                            handler.handle(session, msg);
                        });
                    }
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
