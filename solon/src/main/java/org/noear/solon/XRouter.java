package org.noear.solon;

import org.noear.solon.core.*;
import org.noear.solon.core.XListener;
import org.noear.solon.core.XSession;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用路由器
 * */
public class XRouter {
    //for handler
    private final RouteTable<XHandler>[] _routes_h;
    //for handler controller
    private final List<XHandler>[] _routes_h_c;
    //for listener
    private final RouteTable<XListener> _routes_l;

    public XRouter() {
        _routes_h = new RouteTable[6];

        _routes_h[0] = new RouteTable<>();//before:0
        _routes_h[1] = new RouteTable<>();//main
        _routes_h[2] = new RouteTable<>();//after:2
        _routes_h[3] = new RouteTable<>();//at before:3
        _routes_h[4] = new RouteTable<>();
        _routes_h[5] = new RouteTable<>();//at after:5

        _routes_h_c = new List[3];
        _routes_h_c[0] = new ArrayList<>();
        _routes_h_c[1] = new ArrayList<>();
        _routes_h_c[2] = new ArrayList<>();

        _routes_l = new RouteTable<>();
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
        RouteTable.Route xl = new RouteTable.Route(path, method, index, handler);

        if (endpoint != XEndpoint.main && "@@".equals(path)) {
            RouteTable<XHandler> tmp = _routes_h[endpoint + 3];
            tmp.add(xl);
            tmp.sort(Comparator.comparing(l -> l.index));

            _routes_h_c[endpoint] = tmp.stream().map(r -> r.target).collect(Collectors.toList());
        } else {
            _routes_h[endpoint].add(xl);
        }
    }

    /**
     * 添加路由关系 for XListener
     */
    public void add(String path, XListener listener) {
        add(path, XMethod.ALL, listener);
    }

    public void add(String path, XMethod method, XListener listener) {
        add(path, method, 0, listener);
    }

    /**
     * 添加路由关系 for XListener
     */
    public void add(String path, XMethod method, int index, XListener listener) {
        _routes_l.add(new RouteTable.Route(path, method, index, listener));
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
    public XListener matchOne(XSession session) {
        String path = session.path();

        return _routes_l.matchOne(path, session.method());
    }


    public List<XHandler> atBefore() {
        return Collections.unmodifiableList(_routes_h_c[0]);
    }

    public List<XHandler> atAfter() {
        return Collections.unmodifiableList(_routes_h_c[2]);
    }

}
