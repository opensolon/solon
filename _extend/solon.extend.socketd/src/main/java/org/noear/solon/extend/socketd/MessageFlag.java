package org.noear.solon.extend.socketd;

public class MessageFlag {
    public static final int heartbeat = -2;
    public static final int handshake = -1;
    public static final int message = 0;
    public static final int response = 1;
}
