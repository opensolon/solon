package org.noear.solon.extend.socketd;

import org.noear.solon.Solon;

//
//
// solon.socketd.readBufferSize
// solon.socketd.writeBufferSize
//
public class SocketProps {
    private static int readBufferSize = 1024 * 1024;
    private static int writeBufferSize = 1024;

    public static int readBufferSize() {
        return readBufferSize;
    }

    public static int writeBufferSize() {
        return writeBufferSize;
    }

    static {
        loadReadBufferSize();
        loadwriteBufferSize();
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
        }
    }

    private static void loadwriteBufferSize() {
        String tmp = Solon.cfg().get("solon.socketd.writeBufferSize", "").toLowerCase();

        if (tmp.length() > 2) {
            if (tmp.endsWith("kb")) {
                writeBufferSize = Integer.parseInt(tmp.substring(0, tmp.length() - 2)) * 1024;
            }

            if (tmp.endsWith("mb")) {
                writeBufferSize = Integer.parseInt(tmp.substring(0, tmp.length() - 2)) * 1024 * 1024;
            }
        }
    }
}
