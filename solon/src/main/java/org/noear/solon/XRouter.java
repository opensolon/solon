package org.noear.solon;

import org.noear.solon.core.*;
import org.noear.solon.core.XListener;
import org.noear.solon.core.XSession;

import java.util.*;

/**
 * 通用路由器
 *
 * <pre><code>
 * public class DemoApp{
 *     public static void main(String[] args){
 *         XApp.start(DemoApp.class, args,app->{
 *             //
 *             //路由手写模式
 *             //
 *             app.get("/hello/*",c->coutput("heollo world;"));
 *         });
 *     }
 * }
 *
 * //
 * //容器自动模式
 * //
 * @XController
 * public class HelloController{
 *     @XMapping("/hello/*")
 *     public String hello(){
 *         return "heollo world;";
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public class XRouter {
    //for handler
    private final XRouteTable<XHandler>[] routesH;
    //for listener
    private final XRouteTable<XListener> routesL;

    public XRouter() {
        routesH = new XRouteTable[3];

        routesH[0] = new XRouteTable<>();//before:0
        routesH[1] = new XRouteTable<>();//main
        routesH[2] = new XRouteTable<>();//after:2

        routesL = new XRouteTable<>();
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
    public void add(String path, XEndpoint endpoint, XMethod method, XHandler handler) {
        add(path, endpoint, method, 0, handler);
    }

    /**
     * 添加路由关系 for XHandler
     */
    public void add(String path, XEndpoint endpoint, XMethod method, int index, XHandler handler) {
        routesH[endpoint.code].add(new XRouteTable.Route(path, method, index, handler));
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
        routesL.add(new XRouteTable.Route(path, method, index, listener));
    }

    /**
     * 清空路由关系
     */
    public void clear() {
        routesH[0].clear();
        routesH[1].clear();
        routesH[2].clear();

        routesL.clear();
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public XHandler matchOne(XContext context, XEndpoint endpoint) {
        String path = context.path();
        XMethod method = XMethod.valueOf(context.method());

        return routesH[endpoint.code].matchOne(path, method);
    }

    /**
     * 区配多个目标（根据上上文）
     */
    public List<XHandler> matchAll(XContext context, XEndpoint endpoint) {
        String path = context.path();
        XMethod method = XMethod.valueOf(context.method());

        return routesH[endpoint.code].matchAll(path, method);
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public XListener matchOne(XSession session) {
        String path = session.path();

        return routesL.matchOne(path, session.method());
    }
}
