package org.noear.solon.api.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;

public interface Session {
    void send(String message);
    void send(byte[] message);

    void close() throws IOException;

    boolean isOpen();
    boolean isClosed();

    InetSocketAddress getRemoteSocketAddress() throws IOException;
    InetSocketAddress getLocalSocketAddress() throws IOException;

    void setAttachment(Object obj);
    <T> T getAttachment();

    Collection<Session> getOpenSessions();
}
