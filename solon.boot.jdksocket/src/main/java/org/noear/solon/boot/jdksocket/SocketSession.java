package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.SocketMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class SocketSession {
    private final Socket socket;

    public SocketSession(Socket socket) {
        this.socket = socket;
    }

    public boolean isOpen(){
        return socket.isClosed() == false;
    }

    public void close() throws IOException{
        socket.close();
    }

    public InetAddress getRemoteAddress(){
        return socket.getInetAddress();
    }

    public InputStream getInputStream(){
        try {
            return socket.getInputStream();
        }catch (Exception ex){
            return null;
        }
    }

    public byte[] getBytes() throws IOException {
        InputStream input = getInputStream();
        if (input == null) {
            return null;
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buf = new byte[1024 * 4];
        int n = 0;
        while (-1 != (n = input.read(buf))) {
            output.write(buf, 0, n);
        }

        return output.toByteArray();
    }

    public SocketMessage getMessage(){
        try {
            byte[] bytes = getBytes();
            if (bytes == null) {
                return null;
            }

            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            return SocketMessage.decode(buffer);
        }catch (Throwable ex){
           System.out.println("Decoding failure::");
           ex.printStackTrace();
           return null;
        }
    }

    public void writeAndFlush(SocketMessage message) throws IOException{
        socket.getOutputStream().write(message.encode().array());
        socket.getOutputStream().flush();
    }
}
