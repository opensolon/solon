package org.noear.solon.net.socketd;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.listener.PipelineListener;
import org.noear.solon.Solon;
import org.noear.solon.net.socketd.listener.PathListenerPlus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * WebSoskcet 路由器
 *
 * @author noear
 * @since 2.6
 */
public class SocketdRouter {
    private final PipelineListener rootListener = new PipelineListener();
    private final PathListenerPlus pathListener = new PathListenerPlus();
    private final Set<String> paths = new HashSet<>();

    private SocketdRouter() {
        rootListener.next(pathListener);
    }

    public static SocketdRouter getInstance() {
        //方便在单测环境下切换 SolonApp，可以相互独立
        return Solon.context().attachmentOf(SocketdRouter.class, SocketdRouter::new);
    }


    /**
     * 前置监听
     */
    public void before(Listener listener) {
        rootListener.prev(listener);
    }

    /**
     * 主监听
     */
    public void of(String path, Listener listener) {
        pathListener.doOf(path, listener);
        paths.add(path);
    }

    /**
     * 后置监听
     */
    public void after(Listener listener) {
        rootListener.next(listener);
    }

    public Listener getListener() {
        return rootListener;
    }

    public Collection<String> getPaths() {
        return Collections.unmodifiableSet(paths);
    }
}
