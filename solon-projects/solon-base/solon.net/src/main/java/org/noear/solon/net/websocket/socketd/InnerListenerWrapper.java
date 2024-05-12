package org.noear.solon.net.websocket.socketd;

import org.noear.socketd.transport.core.ListenerWrapper;
import org.noear.socketd.transport.core.Session;

import java.io.IOException;
import java.util.Map;

/**
 * @author noear
 * @since 2.8
 */
public class InnerListenerWrapper extends ListenerWrapper {
    public static final String WS_HANDSHAKE_HEADER = "ws-handshake-headers";

    @Override
    public void onOpen(Session s) throws IOException {
        Map<String, String> headerMap = s.attr(WS_HANDSHAKE_HEADER);
        if (headerMap != null) {
            s.handshake().paramMap().putAll(headerMap);
            s.attrDel(WS_HANDSHAKE_HEADER);
        }

        super.onOpen(s);
    }
}
