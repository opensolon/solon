package org.noear.nami;

import org.noear.nami.channel.http.HttpD;
import org.noear.nami.channel.socket.SocketD;

import java.net.URI;

public class NamiD {
    public static <T> T create(URI server, Class<T> service) {
        if (server.getScheme().startsWith("http")) {
            return HttpD.create(server, service);
        } else {
            return SocketD.create(server, service);
        }
    }

    public static <T> T create(String server, Class<T> service) {
        return create(URI.create(server), service);
    }
}
