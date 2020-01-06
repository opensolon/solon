package org.noear.solonclient.channel;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketUtils {
    private static ExecutorService pool = Executors.newCachedThreadPool();
    private static Map<String, SocketUtils> clientMap = new HashMap<>();

    public static SocketUtils get(String uri){
        URI uri1 = URI.create(uri);

        if("s".equals(uri1.getScheme()) == false) {
            throw new RuntimeException("Only [s] scheme is supported");
        }

        String hostAndPort = uri1.getAuthority();

        SocketUtils client = clientMap.get(hostAndPort);
        if(client == null){
            synchronized (hostAndPort.intern()){
                client = clientMap.get(hostAndPort);
                if(client == null){
                    client = new SocketUtils(uri1.getHost(), uri1.getPort());
                    clientMap.put(hostAndPort,client);
                }
            }
        }

        return client;
    }

    public static SocketMessage send(String uri, String message) throws Exception {
        SocketMessageDock msgD = new SocketMessageDock(SocketMessage.wrap(uri, message.getBytes("utf-8")));

        synchronized (msgD) {
            get(uri).sendDo(msgD, (m) -> {
                synchronized (m){
                    m.notify();
                }
            });
            msgD.wait();
        }

        if (msgD.err == null) {
            return msgD.res;
        } else {
            throw msgD.err;
        }
    }

    private Socket connector;
    private OutputStream outputStream;
    private final String host;
    private final int port;

    private SocketUtils(String host, int port){
        this.host = host;
        this.port = port;
    }

    private void tryConnect() throws Exception {
        if(connector == null) {
            connector = new Socket(host, port);
            outputStream = connector.getOutputStream();
        }
    }

    private void sendDo(final SocketMessageDock msgD, Act1<SocketMessageDock> callback) throws Exception {
        // 建立连接后获得输出流
        pool.execute(() -> {
            try {
                tryConnect();

                outputStream.write(msgD.req.encode().array());
                outputStream.flush();

                msgD.res = decode(connector, connector.getInputStream());

                callback.run(msgD);

            } catch (Exception ex) {
                msgD.err = ex;
                callback.run(msgD);
            }
        });
    }

    public void stop() throws IOException {
        outputStream.close();
        connector.close();
        outputStream = null;
        connector = null;
    }



    private static int MESSAGE_MAX_SIZE = 1024 * 20;
    private SocketMessage decode(Socket connector, InputStream input) throws IOException {
        if(input == null){
            return null;
        }

        byte[] lenBts = new byte[4];
        if (input.read(lenBts) < -1) {
            return null;
        }

        int len = bytesToInt32(lenBts);

        if(len < 6 ){
            return null;
        }

        if(len > MESSAGE_MAX_SIZE){
            stop();
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

    public static class SocketMessageDock {
        public SocketMessage req;
        public SocketMessage res;
        public Exception err;

        public String getKey(){
            return req.key;
        }

        public SocketMessageDock(SocketMessage req){
            this.req = req;
        }
    }
}
