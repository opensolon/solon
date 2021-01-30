package org.noear.solon.core;

/**
 * @author noear
 * @since 1.3
 */
public class SignalSim implements Signal{
    private int port;
    private String protocol;
    private SignalType type;

    public int port() {
        return port;
    }

    public String protocol() {
        return protocol;
    }

    public SignalType type() {
        return type;
    }

    public SignalSim(int port, String protocol, SignalType type) {
        this.port = port;
        this.protocol = protocol.toLowerCase();
        this.type = type;
    }
}
