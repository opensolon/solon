package org.noear.solon.boot;

/**
 * @author noear
 * @since 1.6
 */
public class ServerConstants {
    public static final String SERVER_HTTP_NAME = "server.http.name";
    public static final String SERVER_HTTP_PORT = "server.http.port";
    public static final String SERVER_HTTP_HOST = "server.http.host";
    public static final String SERVER_HTTP_CORETHREADS = "server.http.coreThreads";
    public static final String SERVER_HTTP_MAXTHREADS = "server.http.maxThreads";
    public static final String SERVER_HTTP_IDLETIMEOUT = "server.http.idleTimeout";

    public static final String SERVER_SOCKET_NAME = "server.socket.name";
    public static final String SERVER_SOCKET_PORT = "server.socket.port";
    public static final String SERVER_SOCKET_HOST = "server.socket.host";
    public static final String SERVER_SOCKET_CORETHREADS = "server.socket.coreThreads";
    public static final String SERVER_SOCKET_MAXTHREADS = "server.socket.maxThreads";
    public static final String SERVER_SOCKET_IDLETIMEOUT = "server.socket.idleTimeout";

    public static final String SERVER_WEBSOCKET_NAME = "server.websocket.name";
    public static final String SERVER_WEBSOCKET_PORT = "server.websocket.port";
    public static final String SERVER_WEBSOCKET_HOST = "server.websocket.host";
    public static final String SERVER_WEBSOCKET_CORETHREADS = "server.websocket.coreThreads";
    public static final String SERVER_WEBSOCKET_MAXTHREADS = "server.websocket.maxThreads";
    public static final String SERVER_WEBSOCKET_IDLETIMEOUT = "server.websocket.idleTimeout";

    public static final String SERVER_SESSION_COOKIENAME = "server.session.cookieName";


    public static final String SERVER_KEY_STORE = "server.ssl.keyStore";
    public static final String SERVER_KEY_TYPE = "server.ssl.keyType";
    public static final String SERVER_KEY_PASSWORD = "server.ssl.keyPassword";

    public static final String SSL_KEYSTORE = "javax.net.ssl.keyStore";
    public static final String SSL_KEYSTORE_TYPE = "javax.net.ssl.keyStoreType";
    public static final String SSL_KEYSTORE_PASSWORD = "javax.net.ssl.keyStorePassword";

    @Deprecated
    public final static String SESSIONID_MD5_SALT = "&L8e!@T0";
}
