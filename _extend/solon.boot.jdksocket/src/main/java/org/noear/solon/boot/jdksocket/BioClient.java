package org.noear.solon.boot.jdksocket;

import org.noear.solon.Utils;

import java.net.Socket;

class BioClient {
    String host;
    int port;

    public BioClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Socket start() {
        try {
            return new Socket(host, port);
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }
}
