package org.noear.solon.extend.xsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XListener;
import org.noear.solon.core.XMessage;
import org.noear.solon.core.XSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * XSocket 监听者代理
 *
 * @author noear
 * @since 1.0
 * */
public class XListenerProxy implements XListener {
    //实例维护
    private static XListener global = new XListenerProxy();
    public static XListener getGlobal() {
        return global;
    }
    public static void setGlobal(XListener global) {
        XListenerProxy.global = global;
    }

    private XSocketContextHandler socketContextHandler;

    public XListenerProxy(){
        socketContextHandler = new XSocketContextHandler();
    }


    public static long REQUEST_AND_RESPONSE_TIMEOUT_SECONDS = 5;

    //
    // 内部使用
    //
    private static Map<String, CompletableFuture<XMessage>> requests = new ConcurrentHashMap<>();
    protected static void regRequest(XMessage message, CompletableFuture<XMessage> future) {
        requests.putIfAbsent(message.key(), future);
    }


    //
    // XListener 实现
    //
    @Override
    public void onOpen(XSession session) {
        XListener sl = get(session);
        if (sl != null) {
            sl.onOpen(session);
        }
    }

    @Override
    public void onMessage(XSession session, XMessage message, boolean messageIsString) {
        if(message.flag() == 1) {
            //flag = 1，为响应标志
            CompletableFuture<XMessage> request = requests.get(message.key());

            //请求模式
            if (request != null) {
                request.complete(message);
                return;
            }
        }


        //监听模式
        XListener sl = get(session);
        if (sl != null) {
            sl.onMessage(session, message, messageIsString);
        }

        //代理模式
        if (message.getHandled() == false) {
            socketContextHandler.handle(session, message, messageIsString);
        }
    }

    @Override
    public void onClose(XSession session) {
        XListener sl = get(session);
        if (sl != null) {
            sl.onClose(session);
        }
    }

    @Override
    public void onError(XSession session, Throwable error) {
        XListener sl = get(session);
        if (sl != null) {
            sl.onError(session, error);
        }
    }

    //获取监听器
    private XListener get(XSession s) {
        return XApp.global().router().matchOne(s);
    }
}
