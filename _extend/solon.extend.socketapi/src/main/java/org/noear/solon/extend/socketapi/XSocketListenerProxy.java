package org.noear.solon.extend.socketapi;

import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;

/**
 * 监听代理
 * */
public class XSocketListenerProxy implements XSocketListener {
    private static final XSocketListenerProxy instance = new XSocketListenerProxy();
    public static XSocketListenerProxy getInstance() {
        return instance;
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
        BeanWrap bw;

        if (XUtil.isEmpty(s.resourceDescriptor())) {
            bw = Aop.factory().getWrap(XSocketListener.class);
        } else {
            bw = Aop.factory().getWrap(s.resourceDescriptor());
        }

        return bw == null ? null : bw.raw();
    }
}
