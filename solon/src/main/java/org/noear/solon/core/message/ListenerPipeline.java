package org.noear.solon.core.message;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 监听器管道
 *
 * @author noear
 * @since 1.6
 */
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

    /**
     * 打开时
     * */
    @Override
    public void onOpen(Session session) {
        for (Listener l : chain) {
            l.onOpen(session);
        }
    }

    /**
     * 收到消息时
     * */
    @Override
    public void onMessage(Session session, Message message) throws IOException {
        for (Listener l : chain) {
            l.onMessage(session, message);
        }
    }

    /**
     * 关闭时
     * */
    @Override
    public void onClose(Session session) {
        for (Listener l : chain) {
            l.onClose(session);
        }
    }

    /**
     * 出错时
     * */
    @Override
    public void onError(Session session, Throwable error) {
        for (Listener l : chain) {
            l.onError(session, error);
        }
    }
}
