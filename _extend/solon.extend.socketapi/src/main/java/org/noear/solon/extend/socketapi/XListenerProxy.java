package org.noear.solon.extend.socketapi;

import org.noear.solon.core.BeanWrap;

import java.util.HashMap;
import java.util.Map;

/**
 * XSocket 监听者代理
 * */
public class XListenerProxy implements XListener {
    //实例维护
    private static XListenerProxy instance = new XListenerProxy();
    public static XListenerProxy getInstance() {
        return instance;
    }
    public static void setInstance(XListenerProxy instance) {
        XListenerProxy.instance = instance;
    }

    //监听器
    private Map<String, BeanWrap> listeners = new HashMap<>();

    protected void add(String uri, BeanWrap bw) {
        listeners.put(uri, bw);
    }

    @Override
    public void onOpen(XSession session) {
        XListener sl = get(session);
        if (sl != null) {
            sl.onOpen(session);
        }
    }

    @Override
    public void onMessage(XSession session, XMessage message) {
        XListener sl = get(session);
        if (sl != null) {
            sl.onMessage(session, message);
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
        BeanWrap bw = listeners.get(s.resourceDescriptor());
        return bw == null ? null : bw.get();
    }
}
