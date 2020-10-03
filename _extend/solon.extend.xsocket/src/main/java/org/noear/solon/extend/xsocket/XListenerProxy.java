package org.noear.solon.extend.xsocket;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Route;
import org.noear.solon.core.RouteList;
import org.noear.solon.core.XMethod;

/**
 * XSocket 监听者代理
 *
 * @author noear
 * @since 1.0
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
    protected RouteList<BeanWrap> routes = new RouteList<>();

    protected void add(String path, BeanWrap bw) {
        routes.add(new Route<>(path, XMethod.ALL,0,bw));
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
        BeanWrap bw = routes.matchOne(s.resourceDescriptor(), XMethod.ALL);
        return bw == null ? null : bw.get();
    }
}
