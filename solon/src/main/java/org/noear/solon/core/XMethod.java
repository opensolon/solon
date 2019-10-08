package org.noear.solon.core;

/**
 * HTTP方法
 * */
public final class XMethod {
    public static final String ALL = "";

    //http
    public static final String GET = "GET";
    public static final String POST = "POST";

    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String PATCH = "PATCH";

    private static final String _ALL_STRING = "HEAD,GET,POST,PUT,DELETE,PATCH";
    public static boolean isAll(String method) {
        return _ALL_STRING.indexOf(method) >= 0;
    }
}
