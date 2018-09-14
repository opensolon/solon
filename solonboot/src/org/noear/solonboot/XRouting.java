package org.noear.solonboot;

import com.sun.istack.internal.NotNull;
import org.noear.solonboot.protocol.XHandler;
import org.noear.solonboot.protocol.XHandlerEx;
import org.noear.solonboot.protocol.XMethod;

/*通用路由器*/
@FunctionalInterface
public interface XRouting {
    void run();

    static void path(@NotNull String path, @NotNull XRouting routing) {
        XApp.lastXapp.path(path, routing);
    }

    static void before(XHandler handler){XApp.lastXapp.before("",XMethod.ALL,handler);}
    static void after(XHandler handler){XApp.lastXapp.after("",XMethod.ALL,handler);}

    //添加GET方法的监听
    static void all(XHandler handler) {
        XApp.lastXapp.add("", XMethod.ALL, handler);
    }

    //添加GET方法的监听
    static void get(XHandler handler) {
        XApp.lastXapp.add("", XMethod.GET, handler);
    }

    //添加POST方法的监听
    static void post(XHandler handler) {
        XApp.lastXapp.add("", XMethod.POST, handler);
    }

    //添加PUT方法的监听
    static void put(XHandler handler) {
        XApp.lastXapp.add("", XMethod.PUT, handler);
    }

    //添加DELETE方法的监听
    static void delete(XHandler handler) {
        XApp.lastXapp.add("", XMethod.DELETE, handler);
    }

    //rpcx

    //添加CALL方法的监听
    static void call(XHandler handler) {
        XApp.lastXapp.add("", XMethod.CALL, handler);
    }

    //添加SEND方法的监听
    static void send(XHandler handler) {
        XApp.lastXapp.add("", XMethod.SEND, handler);
    }

    ///============================================================

    static void before(@NotNull String path,XHandler handler){XApp.lastXapp.before(path,XMethod.ALL,handler);}
    static void after(@NotNull String path,XHandler handler){XApp.lastXapp.after(path,XMethod.ALL,handler);}
    //http

    static void add(@NotNull String path, String method, XHandler handler) {
        XApp.lastXapp.add(path, method, handler);
    }

    static void add(XHandlerEx handler) {
        for (String m : handler.method()) {
            for (String p : handler.path()) {
                add(p, m, handler);
            }
        }
    }

    //添加GET方法的监听
    static void all(@NotNull String path, XHandler handler) {
        XApp.lastXapp.add(path, XMethod.ALL, handler);
    }

    //添加GET方法的监听
    static void get(@NotNull String path, XHandler handler) {
        XApp.lastXapp.add(path, XMethod.GET, handler);
    }

    //添加POST方法的监听
    static void post(@NotNull String path, XHandler handler) {
        XApp.lastXapp.add(path, XMethod.POST, handler);
    }

    //添加PUT方法的监听
    static void put(@NotNull String path, XHandler handler) {
        XApp.lastXapp.add(path, XMethod.PUT, handler);
    }

    //添加DELETE方法的监听
    static void delete(@NotNull String path, XHandler handler) {
        XApp.lastXapp.add(path, XMethod.DELETE, handler);
    }
}
