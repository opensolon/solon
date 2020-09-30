package org.noear.solon.api.socket;

import java.io.IOException;
import java.util.Set;

public interface Session {
    String sessionId();

    void send(String message);
    void send(byte[] message);

    void close() throws IOException;

    boolean isOpen();
    boolean isClosing();
    boolean isClosed();

    long getMaxIdleTimeout();
    void setMaxIdleTimeout(long interval);

    void setAttachment(Object obj);
    <T> T getAttachment();

    Set<Session> getOpenSessions();
}
