package webapp.utils;

import org.noear.solon.core.message.Message;
import org.noear.solon.socketd.ProtocolManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 此类，用于简单测试；复杂的，有bug...
 * */
public class SocketUtils {
    private static ThreadLocal<Map<String, SocketUtils>> threadLocal = new ThreadLocal<>();
    public static SocketUtils get(String uri){
        URI uri1 = URI.create(uri);

        if("tcp".equals(uri1.getScheme()) == false) {
            throw new IllegalStateException("Only [s] scheme is supported");
        }

        Map<String, SocketUtils> clientMap = threadLocal.get();
        if(clientMap == null) {
            clientMap = new HashMap<>();
            threadLocal.set(clientMap);
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

    public static SocketUtils create(String uri){
        URI uri1 = URI.create(uri);

        if("tcp".equals(uri1.getScheme()) == false) {
            throw new IllegalStateException("Only [s] scheme is supported");
        }

        return new SocketUtils(uri1.getHost(), uri1.getPort());
    }

    public static Message send(String uri, String message) throws Throwable {
        return send(uri, message.getBytes("UTF-8"));
    }

    public static Message send(String uri, byte[] message) throws Throwable {
        if(message == null){
            return null;
        }

        SocketMessageWrap msgD = new SocketMessageWrap(Message.wrap(uri,null, message));

        get(uri).sendDo(msgD, (m) -> {
            msgD.complete(null);
        });

        msgD.get(3, TimeUnit.SECONDS);

        if (msgD.err == null) {
            return msgD.res;
        } else {
            throw msgD.err;
        }
    }

    public static void send(String uri, String message, BiConsumer<Message,Throwable> callback) throws Throwable {
        send(uri,message.getBytes("UTF-8"),callback);
    }

    public static void send(String uri, byte[] message, BiConsumer<Message,Throwable> callback) throws Throwable {
        if (message == null) {
            return;
        }

        SocketMessageWrap msgD = new SocketMessageWrap(Message.wrap(uri, null, message));
        msgD.handler = callback;

        CompletableFuture.runAsync(() -> {
            get(uri).sendDo(msgD, (m) -> {
                msgD.handler.accept(msgD.res, msgD.err);
            });
        });
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

    private void sendDo(final SocketMessageWrap msgD, Consumer<SocketMessageWrap> callback) {
        // 建立连接后获得输出流
        try {
            tryConnect();

            ByteBuffer buf = ProtocolManager.encode(msgD.req);

            outputStream.write(buf.array());
            outputStream.flush();

            msgD.res = decode(connector.getInputStream());

            callback.accept(msgD);

        } catch (Throwable ex) {
            msgD.err = ex;
            callback.accept(msgD);
        }
    }

    public void stop() throws IOException {
        outputStream.close();
        connector.close();
        outputStream = null;
        connector = null;
    }



    private static int MESSAGE_MAX_SIZE = 1024 * 20;
    private Message decode(InputStream input) throws IOException {
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

        return ProtocolManager.decode(ByteBuffer.wrap(bytes));
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

    public static class SocketMessageWrap extends CompletableFuture<Integer> {
        public Message req;
        public Message res;
        public Throwable err;

        public BiConsumer<Message,Throwable> handler;

        public String getKey(){
            return req.key();
        }

        public SocketMessageWrap(Message req){
            this.req = req;
        }
    }
}
