package org.noear.solon.socketd;

import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author noear
 * @since 1.6
 */
@Deprecated
public class ListenerPipeline implements Listener {
    private List<Listener> chain = new LinkedList<>();

    /**
     * 下一步
     * */
    public ListenerPipeline next(Listener listener) {
        chain.add(listener);
        return this;
    }

    /**
     * 上一步
     * */
    public ListenerPipeline prev(Listener listener) {
        chain.add(0, listener);
        return this;
    }

    public ListenerPipeline prevOnOpen(Consumer<Session> consumer) {
        return prev(new Listener() {
            @Override
            public void onOpen(Session session) {
                consumer.accept(session);
            }

            @Override
            public void onMessage(Session session, Message message) throws IOException {

            }
        });
    }

    @Override
    public void onOpen(Session session) {
        for (Listener l : chain) {
            l.onOpen(session);
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        for (Listener l : chain) {
            l.onMessage(session, message);
        }
    }

    @Override
    public void onClose(Session session) {
        for (Listener l : chain) {
            l.onClose(session);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        for (Listener l : chain) {
            l.onError(session, error);
        }
    }
}
