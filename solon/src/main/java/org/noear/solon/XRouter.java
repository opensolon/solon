package org.noear.solon;

import org.noear.solon.core.*;
import org.noear.solon.core.XListener;
import org.noear.solon.core.XSession;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用路由器
 *
 *
 * @author noear
 * @since 1.0
 * */
public class XRouter {
    //for handler
    private final RouteTable<XHandler>[] routesH;
    //for handler of controller
    private final List<XHandler>[] routesHC;
    //for listener
    private final RouteTable<XListener> routesL;

    public XRouter() {
        routesH = new RouteTable[6];

        routesH[0] = new RouteTable<>();//before:0
        routesH[1] = new RouteTable<>();//main
        routesH[2] = new RouteTable<>();//after:2
        routesH[3] = new RouteTable<>();//at before:3
        routesH[4] = new RouteTable<>();
        routesH[5] = new RouteTable<>();//at after:5

        routesHC = new List[3];
        routesHC[0] = new ArrayList<>();
        routesHC[1] = new ArrayList<>();
        routesHC[2] = new ArrayList<>();

        routesL = new RouteTable<>();
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
            RouteTable<XHandler> tmp = routesH[endpoint + 3];
            tmp.add(xl);
            tmp.sort(Comparator.comparing(l -> l.index));

            routesHC[endpoint] = tmp.stream().map(r -> r.target).collect(Collectors.toList());
        } else {
            routesH[endpoint].add(xl);
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
        routesL.add(new RouteTable.Route(path, method, index, listener));
    }

    /**
     * 清空路由关系
     */
    public void clear() {
        routesH[0].clear();
        routesH[1].clear();
        routesH[2].clear();

        routesH[3].clear();
        routesH[4].clear();
        routesH[5].clear();
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public XHandler matchOne(XContext context, int endpoint) {
        String path = context.path();
        XMethod method = XMethod.valueOf(context.method());

        return routesH[endpoint].matchOne(path, method);
    }

    /**
     * 区配多个目标（根据上上文）
     */
    public List<XHandler> matchAll(XContext context, int endpoint) {
        String path = context.path();
        XMethod method = XMethod.valueOf(context.method());

        return routesH[endpoint].matchAll(path, method);
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public XListener matchOne(XSession session) {
        String path = session.path();

        return routesL.matchOne(path, session.method());
    }


    public List<XHandler> atBefore() {
        return Collections.unmodifiableList(routesHC[0]);
    }

    public List<XHandler> atAfter() {
        return Collections.unmodifiableList(routesHC[2]);
    }

}
