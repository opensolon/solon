/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.net.websocket.socketd;

import org.noear.socketd.transport.core.ListenerWrapper;
import org.noear.socketd.transport.core.Session;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;

import java.io.IOException;

/**
 * @author noear
 * @since 2.8
 */
public class InnerListenerWrapper extends ListenerWrapper {
    public static final String WS_HANDSHAKE_HEADER = "ws-handshake-headers";

    @Override
    public void onOpen(Session s) throws IOException {
        MultiMap<String> headerMap = s.attr(WS_HANDSHAKE_HEADER);
        if (headerMap != null) {
            for (KeyValues<String> kv : headerMap) {
                s.handshake().paramMap().put(kv.getKey(), kv.getFirstValue());
            }

            s.attrDel(WS_HANDSHAKE_HEADER);
        }

        super.onOpen(s);
    }
}
