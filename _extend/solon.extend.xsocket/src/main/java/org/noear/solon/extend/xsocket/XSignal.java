package org.noear.solon.extend.xsocket;

/**
 * 信号
 * */
public enum XSignal {
    /**
     * unspecified，未指明
     * */
    UNSPECIFIED("", 0),

    /**
     * http
     */
    HTTP("HTTP", 20),

    /**
     * web socket
     */
    WEBSOCKET("WEBSOCKET", 31),

    /**
     * socket
     */
    SOCKET("SOCKET", 41);

    public final String name;
    public final int code;

    XSignal(String name, int code) {
        this.name = name;
        this.code = code;
    }
}
