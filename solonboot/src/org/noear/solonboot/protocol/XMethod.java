package org.noear.solonboot.protocol;

public final class XMethod {
    public static final String ALL = null;
    //http
    public static final String GET = "GET";
    public static final String POST = "POST";


    public static final String HEAD = "HEAD";
//    public static final String OPTIONS = "OPTIONS";

    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
//    public static final String TRACE = "TRACE";
//    public static final String CONNECT = "CONNECT";

    //rpcx
    public static final String CALL = "CALL";
    public static final String SEND = "SEND";

    private static final String _ALL_STRING = "HEAD,GET,POST,PUT,DELETE,CALL,SEND";
    public static boolean isAll(String method) {
        return _ALL_STRING.indexOf(method) >= 0;
    }
}
