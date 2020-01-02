package org.noear.solon.boot.jdksocket;

import org.noear.solon.core.SocketMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class SocketSession {
    private final Socket socket;

    public SocketSession(Socket socket) {
        this.socket = socket;
    }

    public boolean isOpen() {
        return socket.isClosed() == false;
    }

    public void close() throws IOException {
        socket.close();
    }

    public InetAddress getRemoteAddress() {
        return socket.getInetAddress();
    }

    public InputStream getInputStream() {
        try {
            return socket.getInputStream();
        } catch (Exception ex) {
            return null;
        }
    }

    public byte[] getBytes() throws IOException {
        InputStream input = getInputStream();
        if (input == null) {
            return null;
        }

        byte[] lenBts = new byte[4];
        if (input.read(lenBts) < -1) {
            return null;
        }

        int len = bytesToInt32(lenBts);
        byte[] bytes = new byte[len];
        bytes[0] = lenBts[0];
        bytes[1] = lenBts[1];
        bytes[2] = lenBts[2];
        bytes[3] = lenBts[3];

        input.read(bytes, 4, len - 4);

        return bytes;
    }

    public SocketMessage getMessage() {
        try {
            byte[] bytes = getBytes();
            if (bytes == null) {
                return null;
            }

            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            SocketMessage tmp = SocketMessage.decode(buffer);

            return tmp;
        } catch (Throwable ex) {
            System.out.println("Decoding failure::");
            ex.printStackTrace();
            return null;
        }
    }

    public void writeAndFlush(SocketMessage message) throws IOException {
        socket.getOutputStream().write(message.encode().array());
        socket.getOutputStream().flush();
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
