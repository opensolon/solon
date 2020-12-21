package org.noear.solon.extend.socketd;

import org.noear.solon.Solon;

//
//
// solon.socketd.readBufferSize
// solon.socketd.writeBufferSize
//
public class SocketProps {
    private static int readBufferSize = 0;
    private static int writeBufferSize = 0;

    private static int connectTimeout = 0;
    private static int socketTimeout = 0;


    public static int readBufferSize() {
        return readBufferSize;
    }

    public static int writeBufferSize() {
        return writeBufferSize;
    }


    public static int connectTimeout() {
        return connectTimeout;
    }

    public static int socketTimeout() {
        return socketTimeout;
    }


    static {
        loadReadBufferSize();
        loadWriteBufferSize();
        loadConnectTimeout();
        loadSocketTimeout();
    }

    private static void loadReadBufferSize() {
        String tmp = Solon.cfg().get("solon.socketd.readBufferSize", "").toLowerCase();

        if (tmp.length() > 2) {
            if (tmp.endsWith("kb")) {
                readBufferSize = Integer.parseInt(tmp.substring(0, tmp.length() - 2)) * 1024;
            }

            if (tmp.endsWith("mb")) {
                readBufferSize = Integer.parseInt(tmp.substring(0, tmp.length() - 2)) * 1024 * 1024;
            }

            if (tmp.indexOf("b") < 0) {
                readBufferSize = Integer.parseInt(tmp);
            }
        }
    }

    private static void loadWriteBufferSize() {
        String tmp = Solon.cfg().get("solon.socketd.writeBufferSize", "").toLowerCase();

        if (tmp.length() > 2) {
            if (tmp.endsWith("kb")) {
                writeBufferSize = Integer.parseInt(tmp.substring(0, tmp.length() - 2)) * 1024;
            }

            if (tmp.endsWith("mb")) {
                writeBufferSize = Integer.parseInt(tmp.substring(0, tmp.length() - 2)) * 1024 * 1024;
            }

            if (tmp.indexOf("b") < 0) {
                writeBufferSize = Integer.parseInt(tmp);
            }
        }
    }

    private static void loadConnectTimeout() {
        String tmp = Solon.cfg().get("solon.socketd.connectTimeout", "").toLowerCase();

        if (tmp.length() > 2) {
            if (tmp.endsWith("ms")) {
                connectTimeout = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            }

            if (tmp.endsWith("s")) {
                connectTimeout = Integer.parseInt(tmp.substring(0, tmp.length() - 1)) * 1000;
            }

            if (tmp.indexOf("s") < 0) {
                connectTimeout = Integer.parseInt(tmp);
            }
        }
    }

    private static void loadSocketTimeout() {
        String tmp = Solon.cfg().get("solon.socketd.socketTimeout", "").toLowerCase();

        if (tmp.length() > 2) {
            if (tmp.endsWith("ms")) {
                socketTimeout = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            }

            if (tmp.endsWith("s")) {
                socketTimeout = Integer.parseInt(tmp.substring(0, tmp.length() - 1)) * 1000;
            }

            if (tmp.indexOf("s") < 0) {
                socketTimeout = Integer.parseInt(tmp);
            }
        }
    }
}
