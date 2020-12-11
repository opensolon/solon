package org.noear.solon.core.message;

public interface FrameFlag {
    int container = 1;

    int message = 10;
    int heartbeat = 11;
    int handshake = 12;
    int response = 13;
}
