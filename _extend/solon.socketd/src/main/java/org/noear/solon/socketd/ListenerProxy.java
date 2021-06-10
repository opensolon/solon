package org.noear.solon.socketd;

import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.MessageFlag;
import org.noear.solon.core.message.Session;
import org.noear.solon.core.util.PrintUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * SocketD 监听者代理
 *
 * @author noear
 * @since 1.0
 * */
public class ListenerProxy implements Listener {
    static final Logger log = LoggerFactory.getLogger(ListenerProxy.class);

    //消息处理线程池
    static final ExecutorService executors = Executors.newCachedThreadPool(r->new Thread(r,"SocketD"));

    //全局实例维护
    private static Listener global = new ListenerProxy();

    public static Listener getGlobal() {
        return global;
    }

    public static void setGlobal(Listener global) {
        ListenerProxy.global = global;
    }

    public ListenerProxy() {
    }


    public static int REQUEST_AND_RESPONSE_TIMEOUT_SECONDS = 30;

    //
    // 内部使用
    //
    private static Map<String, CompletableFuture<Message>> requests = new ConcurrentHashMap<>();

    protected static void regRequest(Message message, CompletableFuture<Message> future) {
        requests.putIfAbsent(message.key(), future);
    }


    //
    // XListener 实现
    //
    @Override
    public void onOpen(Session session) {
        executors.submit(() -> {
            onOpen0(session);
        });
    }

    private void onOpen0(Session session) {
        try {
            //路由监听模式（起到过滤器作用）
            Listener sl = get(session);
            if (sl != null) {
                sl.onOpen(session);
            }

            //实例监听者
            if (session.listener() != null) {
                session.listener().onOpen(session);
            }
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        if (message == null) {
            return;
        }

        //
        //线程池处理，免得被卡住
        //
        executors.submit(() -> {
            onMessage0(session, message);
        });
    }

    private void onMessage0(Session session, Message message) {
        try {
            log.trace("Listener proxy receive: {}", message);

            //路由监听模式（起到过滤器作用）
            Listener sl = get(session);
            if (sl != null) {
                sl.onMessage(session, message);
            }

            //实例监听者
            if (session.listener() != null) {
                session.listener().onMessage(session, message);
            }

            //心跳包不进入处理流程
            if (message.flag() == MessageFlag.heartbeat) {
                return;
            }

            //如果是响应体，尝试直接通知Request
            if (message.flag() == MessageFlag.response) {
                //flag 消息标志（-1握手包；0发起包； 1响应包）
                //
                CompletableFuture<Message> request = requests.get(message.key());

                //请求模式
                if (request != null) {
                    requests.remove(message.key());
                    request.complete(message);
                    return;
                }
            }

            //代理模式
            if (message.getHandled() == false) {
                SocketContextHandler.instance.handle(session, message);
            }
        } catch (Throwable ex) {
            onError0(session, ex);
            EventBus.push(ex);
        }
    }

    @Override
    public void onClose(Session session) {
        executors.submit(() -> {
            onClose0(session);
        });
    }

    private void onClose0(Session session) {
        try {
            //路由监听模式
            Listener sl = get(session);
            if (sl != null) {
                sl.onClose(session);
            }

            //实例监听者
            if (session.listener() != null) {
                session.listener().onClose(session);
            }
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        executors.submit(() -> {
            onError0(session, error);
        });
    }

    private void onError0(Session session, Throwable error) {
        try {
            //路由监听模式（起到过滤器作用）
            Listener sl = get(session);
            if (sl != null) {
                sl.onError(session, error);
            }

            //实例监听者
            if (session.listener() != null) {
                session.listener().onError(session, error);
            }
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    //获取监听器
    private Listener get(Session s) {
        //
        //路由监听模式，可实现双向RPC模式
        //

        return Solon.global().router().matchOne(s);
    }
}
