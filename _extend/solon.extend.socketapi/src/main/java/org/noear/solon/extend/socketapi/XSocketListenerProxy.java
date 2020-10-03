package org.noear.solon.extend.socketapi;

import org.noear.solon.core.BeanWrap;

import java.util.HashMap;
import java.util.Map;

/**
 * 监听代理
 * */
public class XSocketListenerProxy implements XSocketListener {
    //实例维护
    private static XSocketListenerProxy instance = new XSocketListenerProxy();
    public static XSocketListenerProxy getInstance() {
        return instance;
    }
    public static void setInstance(XSocketListenerProxy instance) {
        XSocketListenerProxy.instance = instance;
    }

    //监听器
    private Map<String, BeanWrap> listeners = new HashMap<>();

    protected void add(String uri, BeanWrap bw) {
        listeners.put(uri, bw);
    }

    @Override
    public void onOpen(XSession session) {
        XSocketListener sl = get(session);
        if (sl != null) {
            sl.onOpen(session);
        }
    }

    @Override
    public void onMessage(XSession session, XSocketMessage message) {
        XSocketListener sl = get(session);
        if (sl != null) {
            sl.onMessage(session, message);
        }
    }

    @Override
    public void onClose(XSession session) {
        XSocketListener sl = get(session);
        if (sl != null) {
            sl.onClose(session);
        }
    }

    @Override
    public void onError(XSession session, Throwable error) {
        XSocketListener sl = get(session);
        if (sl != null) {
            sl.onError(session, error);
        }
    }

    //获取监听器
    private XSocketListener get(XSession s) {
        BeanWrap bw = listeners.get(s.resourceDescriptor());
        return bw == null ? null : bw.get();
    }
}
