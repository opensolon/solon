package webapp.demoh_socket;

import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;
import org.noear.solon.ext.Act1;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient {
    private Socket connector;
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
        pool.execute(()->{
            try {
                SocketMessage msg = SocketMessage.wrap(path, message.getBytes("utf-8"));

                connector.getOutputStream().write(msg.encode().array());
                connector.getOutputStream().flush();

                msg = decode(connector, connector.getInputStream());

                if(msg != null) {
                    callback.run(msg);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }

    public void stop() throws IOException {
        connector.close();
    }


    
    private static SocketMessage decode(Socket connector, InputStream input) throws IOException {
        if(input == null){
            return null;
        }

        byte[] lenBts = new byte[4];
        if (input.read(lenBts) < -1) {
            return null;
        }

        int len = bytesToInt32(lenBts);

        if(len < 6){
            return null;
        }

        byte[] bytes = new byte[len];
        bytes[0] = lenBts[0];
        bytes[1] = lenBts[1];
        bytes[2] = lenBts[2];
        bytes[3] = lenBts[3];

        input.read(bytes, 4, len - 4);

        return SocketMessage.decode(ByteBuffer.wrap(bytes));
    }


    private static int bytesToInt32(byte[] bs) {
        //获取最高八位
        int num1 = 0;
        num1 = (num1 ^ (int) bs[0]);
        num1 = num1 << 24;
        //获取第二高八位
        int num2 = 0;
        num2 = (num2 ^ (int) bs[1]);
        num2 = num2 << 16;
        //获取第二低八位
        int num3 = 0;
        num3 = (num3 ^ (int) bs[2]);
        num3 = num3 << 8;
        //获取低八位
        int num4 = 0;
        num4 = (num4 ^ (int) bs[3]);
        return num1 ^ num2 ^ num3 ^ num4;
    }
}
