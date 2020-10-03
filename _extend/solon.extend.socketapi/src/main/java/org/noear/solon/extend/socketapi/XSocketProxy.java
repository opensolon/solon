package org.noear.solon.extend.socketapi;

import org.noear.solon.core.BeanWrap;

import java.util.HashMap;
import java.util.Map;

/**
 * 监听代理
 * */
public class XSocketProxy implements XSocketListener {
    private static final XSocketProxy instance = new XSocketProxy();

    public static XSocketProxy getInstance() {
        return instance;
    }

    private Map<String, BeanWrap> cached = new HashMap<>();

    protected void add(String uri, BeanWrap bw) {
        cached.put(uri, bw);
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
    public void onClosing(XSession session) {
        XSocketListener sl = get(session);
        if (sl != null) {
            sl.onClosing(session);
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

    private XSocketListener get(XSession s) {
        BeanWrap bw = cached.get(s.resourceDescriptor());
        return bw == null ? null : bw.get();
    }
}
