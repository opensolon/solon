package org.noear.solon.core;

/**
 * HTTP方法
 * */
public enum XMethod {
    //http
    GET("GET", 1), //获取资源
    POST("POST", 2), //新建资源

    PUT("PUT", 3), //修改资源 //客户端要提供改变后的完整资源
    DELETE("DELETE", 4), //删除资源
    PATCH("PATCH", 5), //修改资源 //客户端只提供改变的局部属性

    HEAD("HEAD", 6), //相当于GET，但不返回内容


    TRACE("TRACE", 11),//回馈服务器收到的请求，用于远程诊断服务器。
    OPTIONS("OPTIONS", 12), //获取服务器支持的HTTP请求方法
    CONNECT("CONNECT", 13),//用于代理进行传输

    /** http general all */
    HTTP("HTTP", 20),

    /** web socket send */
    WEBSOCKET("WEBSOCKET", 31),

    /** socket listen */
    SOCKET("SOCKET", 41),


    ALL("ALL", 99);

    public final String name;
    public final int code;

    XMethod(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public boolean isHttpMethod() {
        return this.code <= XMethod.HTTP.code;
    }
}
