package org.noear.solon.api.socket;

import java.io.IOException;
import java.util.Collection;

public interface Session {
    void send(String message);
    void send(byte[] message);

    void close() throws IOException;

    boolean isOpen();
    boolean isClosing();
    boolean isClosed();

    void setAttachment(Object obj);
    <T> T getAttachment();

    Collection<Session> getOpenSessions();
}
