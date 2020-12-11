package org.noear.solon.extend.socketd;

public interface MessageFlag {
    int heartbeat = 10;
    int handshake = 11;
    int message = 12;
    int response = 13;
}
