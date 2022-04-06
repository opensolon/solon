package org.noear.solon.core.message;

import java.io.IOException;

/**
 * 空监听器
 *
 * @author noear
 * @since 1.6
 */
public abstract class ListenerEmpty implements Listener {
    @Override
    public void onMessage(Session session, Message message) throws IOException {

    }
}
