package org.noear.solon.extend.xsocket;

import org.noear.solon.Solon;
import org.noear.solon.core.message.MessageListener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.MessageSession;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * XSocket 监听者代理
 *
 * @author noear
 * @since 1.0
 * */
public class MessageListenerProxy implements MessageListener {
    //实例维护
    private static MessageListener global = new MessageListenerProxy();
    public static MessageListener getGlobal() {
        return global;
    }
    public static void setGlobal(MessageListener global) {
        MessageListenerProxy.global = global;
    }

    private SocketContextHandler socketContextHandler;

    public MessageListenerProxy(){
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
    public void onOpen(MessageSession session) {
        MessageListener sl = get(session);
        if (sl != null) {
            sl.onOpen(session);
        }
    }

    @Override
    public void onMessage(MessageSession session, Message message, boolean messageIsString) {
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
        MessageListener sl = get(session);
        if (sl != null) {
            sl.onMessage(session, message, messageIsString);
        }

        //代理模式
        if (message.getHandled() == false) {
            socketContextHandler.handle(session, message, messageIsString);
        }
    }

    @Override
    public void onClose(MessageSession session) {
        MessageListener sl = get(session);
        if (sl != null) {
            sl.onClose(session);
        }
    }

    @Override
    public void onError(MessageSession session, Throwable error) {
        MessageListener sl = get(session);
        if (sl != null) {
            sl.onError(session, error);
        }
    }

    //获取监听器
    private MessageListener get(MessageSession s) {
        return Solon.global().router().matchOne(s);
    }
}
