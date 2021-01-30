package org.noear.solon.core;

/**
 * @author noear
 * @since 1.3
 */
public class Signal {
    public final int port;
    public final String protocol;
    public final SignalType type;

    public Signal(int port, String protocol, SignalType type){
        this.port = port;
        this.protocol = protocol.toLowerCase();
        this.type = type;
    }
}
