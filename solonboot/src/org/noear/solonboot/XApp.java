package org.noear.solonboot;

import com.sun.istack.internal.NotNull;
import org.noear.solonboot.protocol.*;
import org.noear.solonboot.router.XExpRouter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

/*通用应用（同时是个通用代理）*/
public class XApp implements XHandler {
    public static XApp lastXapp;

    /*启动应用（全局只启动一个）*/
    public static XApp start(String[] args) {
        XApp app = new XApp(args);

        //根据配置分别启运http和rpcx服务（也可能都不启动，看配置）
        String http = app.prop().get("solonboot.http");
        String rpcx = app.prop().get("solonboot.rpcx");

        if (http != null) {
            XServer server = XUtil.newClass(http + ".XServerImp");
            if (server != null) {
                server.start(app);
            }
        }

        if (rpcx != null) {
            XServer server = XUtil.newClass(rpcx + ".XServerImp");
            if (server != null) {
                server.start(app);
            }
        }

        return app;
    }
    //////////////////////////////////


    private XRouter<XHandler> _router;
    private int _port;
    private XProperties _propx;

    protected XApp(String[] args) {
        _router = new XExpRouter();
        _propx = new XProperties(args);

        _port = _propx.serverPort();

        lastXapp = this;
    }

    //获取端口
    public int port() {
        return _port;
    }

    public XProperties prop() {
        return _propx;
    }

    ///////////////////////////////////////////////


    private Deque<String> pathDeque = new ArrayDeque<>();

    public void path(@NotNull String path, @NotNull XRouting routing) {
        path = path.startsWith("/") ? path : "/" + path;

        pathDeque.addLast(path);
        routing.run();
        pathDeque.removeLast();
    }

    private String prefixPath(@NotNull String path) {
        return pathDeque
                .stream()
                .collect(Collectors.joining("")) + ((path.startsWith("/") || path.isEmpty()) ? path : "/" + path);
    }
    ///////////////////////////////////////////////

    public void before(@NotNull String path, String method, XHandler handler) {
        _router.add(prefixPath(path), XEndpoint.before, method, handler);
    }

    public void after(@NotNull String path, String method, XHandler handler) {
        _router.add(prefixPath(path), XEndpoint.after, method, handler);
    }

    //添加监听
    public void add(@NotNull String path, String method, XHandler handler) {
        _router.add(prefixPath(path), XEndpoint.main, method, handler);
//        _router.add(path, XEndpoint.main, method, handler);
    }

    public void add(XHandlerEx handler) {
        for (String m : handler.method()) {
            for (String p : handler.path()) {
                add(p, m, handler);
            }
        }
    }

    //添加所有方法的监听
    public void all(@NotNull String path, XHandler handler) {
        add(path, XMethod.ALL, handler);
    }

    //http

    //添加GET方法的监听
    public void get(@NotNull String path, XHandler handler) {
        add(path, XMethod.GET, handler);
    }

    //添加POST方法的监听
    public void post(@NotNull String path, XHandler handler) {
        add(path, XMethod.POST, handler);
    }

    //添加PUT方法的监听
    public void put(@NotNull String path, XHandler handler) {
        add(path, XMethod.PUT, handler);
    }

    //添加DELETE方法的监听
    public void delete(@NotNull String path, XHandler handler) {
        add(path, XMethod.DELETE, handler);
    }


    /////////////////////////////

    public XRouter router() {
        return _router;
    }

    private final static ThreadLocal<XContext> threadLocal  = new ThreadLocal<>();

    public static XContext currentContext(){
        return threadLocal.get();
    }

    @Override
    public void handle(XContext context) throws Exception {
        threadLocal.set(context);

        try {
            boolean is_handled = false;
            //前置处理
            is_handled = do_handle(context, XEndpoint.before) || is_handled; //前后不能反

            //主体处理
            is_handled = do_handle(context, XEndpoint.main) || is_handled; //前后不能反

            //后置处理
            is_handled = do_handle(context, XEndpoint.after) || is_handled; //前后不能反

            if (is_handled) {
                context.status(200);
                context.setHandled(is_handled);
            }
        }finally {
            threadLocal.remove();
        }
    }

    private boolean do_handle(XContext context, int endpoint) throws Exception {
        XHandler handler = _router.matched(context, endpoint);

        if (handler != null) {
            handler.handle(context);
            return true;
        } else {
            return false;
        }
    }
}
