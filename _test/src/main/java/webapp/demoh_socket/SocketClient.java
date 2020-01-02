package webapp.demoh_socket;

import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;
import org.noear.solon.ext.Act1;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient {
    private Socket connector;
    static SocketProtocol protocol = new SocketProtocol();
    private ExecutorService pool = Executors.newCachedThreadPool();

    public void start() throws Exception {
        // 要连接的服务端IP地址和端口
        String host = "127.0.0.1";
        int port = 20000 + XApp.global().port();
        // 与服务端建立连接
        connector = new Socket(host, port);
    }

    public void send(String path, String message, Act1<SocketMessage> callback) throws Exception {
        // 建立连接后获得输出流
//        pool.execute(()->{
            try {
                SocketMessage msg = SocketMessage.wrap(path, message.getBytes("utf-8"));

                connector.getOutputStream().write(msg.encode().array());
                connector.getOutputStream().flush();

                msg = protocol.decode(connector, connector.getInputStream());

                if(msg != null) {
                    callback.run(msg);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
//        });
    }

    public void stop() throws IOException {
        connector.close();
    }
}
