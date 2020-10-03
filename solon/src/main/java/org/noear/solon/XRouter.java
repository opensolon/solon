package org.noear.solon;

import org.noear.solon.core.*;

import java.util.*;

/**
 * 通用路由器
 * */
public class XRouter {
    private final RouteList<XHandler>[] _routes;

    public XRouter() {
        _routes = new RouteList[6];

        _routes[0]  =new RouteList<>();//before:0
        _routes[1]  =new RouteList<>();//main
        _routes[2]  =new RouteList<>();//after:2
        _routes[3]  =new RouteList<>();//at before:3
        _routes[4]  =new RouteList<>();
        _routes[5]  =new RouteList<>();//at after:5

    }

    /**
     * 添加路由关系
     */
    public void add(String path, XHandler handler) {
        add(path, XEndpoint.main, XMethod.HTTP, handler);
    }

    /**
     * 添加路由关系
     */
    public void add(String path, int endpoint, XMethod method, XHandler handler) {
        add(path, endpoint, method, 0, handler);
    }

    /**
     * 添加路由关系
     */
    public void add(String path, int endpoint, XMethod method, int index, XHandler handler) {
        Route xl = new Route(path, method, index, handler);

        if (endpoint != XEndpoint.main && "@@".equals(path)) {
            endpoint += 3;

            RouteList<XHandler> tmp = new RouteList<XHandler>(_routes[endpoint]);
            tmp.add(xl);
            tmp.sort(Comparator.comparing(l -> l.index));
            _routes[endpoint] = tmp;
        } else {
            _routes[endpoint].add(xl);
        }
    }

    /**
     * 清空路由关系
     */
    public void clear() {
        _routes[0].clear();
        _routes[1].clear();
        _routes[2].clear();

        _routes[3].clear();
        _routes[4].clear();
        _routes[5].clear();
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public XHandler matchOne(XContext context, int endpoint) {
        String path = context.path();
        XMethod method = XMethod.valueOf(context.method());

        return _routes[endpoint].matchOne(path, method);
    }

    /**
     * 区配多个目标（根据上上文）
     */
    public List<XHandler> matchAll(XContext context, int endpoint) {
        String path = context.path();
        XMethod method = XMethod.valueOf(context.method());

        return _routes[endpoint].matchAll(path, method);
    }


    public List<Route<XHandler>> atBefore() {
        return Collections.unmodifiableList(_routes[3]);
    }

    public List<Route<XHandler>> atAfter() {
        return Collections.unmodifiableList(_routes[5]);
    }

}
