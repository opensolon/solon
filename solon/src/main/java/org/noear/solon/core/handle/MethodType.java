package org.noear.solon.core.handle;

import org.noear.solon.core.SignalType;

/**
 * 方法枚举
 *
 * @author noear
 * @since 1.0
 * */
public enum MethodType {
    //http
    GET("GET", SignalType.HTTP), //获取资源
    POST("POST", SignalType.HTTP), //新建资源

    PUT("PUT", SignalType.HTTP), //修改资源 //客户端要提供改变后的完整资源
    DELETE("DELETE", SignalType.HTTP), //删除资源
    PATCH("PATCH", SignalType.HTTP), //修改资源 //客户端只提供改变的局部属性

    HEAD("HEAD", SignalType.HTTP), //相当于GET，但不返回内容

    OPTIONS("OPTIONS", SignalType.HTTP), //获取服务器支持的HTTP请求方法

    TRACE("TRACE", SignalType.HTTP),//回馈服务器收到的请求，用于远程诊断服务器。
    CONNECT("CONNECT", SignalType.HTTP),//用于代理进行传输

    /**
     * http general all
     */
    HTTP("HTTP", SignalType.HTTP),

    /**
     * web socket send
     */
    WEBSOCKET("WEBSOCKET", SignalType.WEBSOCKET),

    /**
     * socket listen
     */
    SOCKET("SOCKET", SignalType.SOCKET),

    /**
     * unknown
     */
    UNKNOWN("UNKNOWN", SignalType.ALL),

    ALL("ALL", SignalType.ALL);

    public final String name;
    public final SignalType signal;

    MethodType(String name, SignalType signal) {
        this.name = name;
        this.signal = signal;
    }
}
