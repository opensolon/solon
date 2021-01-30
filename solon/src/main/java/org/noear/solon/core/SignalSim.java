package org.noear.solon.core;

/**
 * @author noear
 * @since 1.3
 */
public class SignalSim implements Signal {
    private int port;
    private String protocol;
    private SignalType type;
    private String name;

    @Override
    public String name() {
        return name;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public String protocol() {
        return protocol;
    }

    @Override
    public SignalType type() {
        return type;
    }

    public SignalSim(String name, int port, String protocol, SignalType type) {
        this.name = name;
        this.port = port;
        this.protocol = protocol.toLowerCase();
        this.type = type;
    }
}
