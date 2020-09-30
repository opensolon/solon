package org.noear.solon.boot.jdksocket;

import org.noear.solonx.socket.api.XSocketMessage;
import org.noear.solonx.socket.api.XSocketMessageUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class SocketSession {
    private final Socket connector;

    public SocketSession(Socket connector) {
        this.connector = connector;
    }

    public boolean isOpen() {
        return connector.isClosed() == false;
    }

    public void close() throws IOException {
        synchronized (connector){
            connector.shutdownInput();
            connector.shutdownOutput();
            connector.close();
        }
    }

    public InetAddress getRemoteAddress() {
        return connector.getInetAddress();
    }

    public XSocketMessage receive(SocketProtocol protocol) {
        try {
            return protocol.decode(connector, connector.getInputStream());
        }
        catch (SocketException ex){
            return null;
        }
        catch (Throwable ex) {
            System.out.println("Decoding failure::");
            ex.printStackTrace();
            return null;
        }
    }

    public void publish(XSocketMessage msg) throws IOException {
        connector.getOutputStream().write(XSocketMessageUtils.encode(msg).array());
        connector.getOutputStream().flush();
    }
}
