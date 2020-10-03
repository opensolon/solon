package org.noear.solon;

import org.noear.solon.core.*;
import org.noear.solon.xsocket.XListener;
import org.noear.solon.xsocket.XSession;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用路由器
 * */
public class XRouter {
    private final RouteList<XHandler>[] _routes_h; //for handler
    private final List<XHandler>[] _routes_h_c; //for controller
    private final RouteList<XListener>[] _routes_l; //for listener

    public XRouter() {
        _routes_h = new RouteList[6];

        _routes_h[0]  =new RouteList<>();//before:0
        _routes_h[1]  =new RouteList<>();//main
        _routes_h[2]  =new RouteList<>();//after:2
        _routes_h[3]  =new RouteList<>();//at before:3
        _routes_h[4]  =new RouteList<>();
        _routes_h[5]  =new RouteList<>();//at after:5

        _routes_h_c = new List[3];
        _routes_h_c[0] = new ArrayList<>();
        _routes_h_c[1] = new ArrayList<>();
        _routes_h_c[2] = new ArrayList<>();

        _routes_l = new RouteList[3];
        _routes_l[0] = new RouteList<>();
        _routes_l[1] = new RouteList<>();
        _routes_l[2] = new RouteList<>();
    }

    /**
     * 添加路由关系 for XHandler
     */
    public void add(String path, XHandler handler) {
        add(path, XEndpoint.main, XMethod.HTTP, handler);
    }

    /**
     * 添加路由关系 for XHandler
     */
    public void add(String path, int endpoint, XMethod method, XHandler handler) {
        add(path, endpoint, method, 0, handler);
    }

    /**
     * 添加路由关系 for XHandler
     */
    public void add(String path, int endpoint, XMethod method, int index, XHandler handler) {
        Route xl = new Route(path, method, index, handler);

        if (endpoint != XEndpoint.main && "@@".equals(path)) {
            RouteList<XHandler> tmp = _routes_h[endpoint+3];
            tmp.add(xl);
            tmp.sort(Comparator.comparing(l -> l.index));

            _routes_h_c[endpoint] = tmp.stream().map(r->r.target).collect(Collectors.toList());
        } else {
            _routes_h[endpoint].add(xl);
        }
    }

    /**
     * 添加路由关系 for XListener
     */
    public void add(String path, XListener listener){
        add(path, XMethod.ALL,  listener);
    }

    public void add(String path, XMethod method, XListener listener) {
        add(path, XEndpoint.main, method, 0, listener);
    }

    /**
     * 添加路由关系 for XListener
     */
    public void add(String path, int endpoint, XMethod method, int index, XListener listener){
        Route xl = new Route(path, method, index, listener);
        _routes_l[endpoint].add(xl);
    }

    /**
     * 清空路由关系
     */
    public void clear() {
        _routes_h[0].clear();
        _routes_h[1].clear();
        _routes_h[2].clear();

        _routes_h[3].clear();
        _routes_h[4].clear();
        _routes_h[5].clear();
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public XHandler matchOne(XContext context, int endpoint) {
        String path = context.path();
        XMethod method = XMethod.valueOf(context.method());

        return _routes_h[endpoint].matchOne(path, method);
    }

    /**
     * 区配多个目标（根据上上文）
     */
    public List<XHandler> matchAll(XContext context, int endpoint) {
        String path = context.path();
        XMethod method = XMethod.valueOf(context.method());

        return _routes_h[endpoint].matchAll(path, method);
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public XListener matchOne(XSession session, int endpoint) {
        String path = session.resourceDescriptor();

        return _routes_l[endpoint].matchOne(path, session.method());
    }

    /**
     * 区配多个目标（根据上上文）
     */
    public List<XListener> matchAll(XSession session, int endpoint) {
        String path = session.resourceDescriptor();

        return _routes_l[endpoint].matchAll(path, session.method());
    }


    public List<XHandler> atBefore() {
        return Collections.unmodifiableList(_routes_h_c[0]);
    }

    public List<XHandler> atAfter() {
        return Collections.unmodifiableList(_routes_h_c[2]);
    }

}
