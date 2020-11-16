package org.noear.solon.extend.xsocket;

import org.noear.solon.Solon;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * XSocket 监听者代理
 *
 * @author noear
 * @since 1.0
 * */
public class ListenerProxy implements Listener {
    //实例维护
    private static Listener global = new ListenerProxy();
    public static Listener getGlobal() {
        return global;
    }
    public static void setGlobal(Listener global) {
        ListenerProxy.global = global;
    }

    private SocketContextHandler socketContextHandler;

    public ListenerProxy(){
        socketContextHandler = new SocketContextHandler();
    }


    public static long REQUEST_AND_RESPONSE_TIMEOUT_SECONDS = 10l;

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
        Listener sl = get(session);
        if (sl != null) {
            sl.onOpen(session);
        }
    }

    @Override
    public void onMessage(Session session, Message message, boolean messageIsString) {
        if(message.flag() == 1) {
            //flag = 1，为响应标志
            CompletableFuture<Message> request = requests.get(message.key());

            //请求模式
            if (request != null) {
                request.complete(message);
                return;
            }
        }


        //监听模式
        Listener sl = get(session);
        if (sl != null) {
            sl.onMessage(session, message, messageIsString);
        }

        //代理模式
        if (message.getHandled() == false) {
            socketContextHandler.handle(session, message, messageIsString);
        }
    }

    @Override
    public void onClose(Session session) {
        Listener sl = get(session);
        if (sl != null) {
            sl.onClose(session);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        Listener sl = get(session);
        if (sl != null) {
            sl.onError(session, error);
        }
    }

    //获取监听器
    private Listener get(Session s) {
        return Solon.global().router().matchOne(s);
    }
}
