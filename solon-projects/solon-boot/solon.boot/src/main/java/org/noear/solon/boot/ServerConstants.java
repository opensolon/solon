package org.noear.solon.boot;

/**
 * @author noear
 * @since 1.6
 */
public interface ServerConstants {
    String SERVER_SSL_KEY_STORE = "server.ssl.keyStore";
    String SERVER_SSL_KEY_TYPE = "server.ssl.keyType";
    String SERVER_SSL_KEY_PASSWORD = "server.ssl.keyPassword";

    String SERVER_HTTP_GZIP_ENABLE = "server.http.gzip.enable";
    String SERVER_HTTP_GZIP_MINSIZE = "server.http.gzip.minSize";
    String SERVER_HTTP_GZIP_MIMETYPES = "server.http.gzip.mimeTypes";

    String SERVER_REQUEST_MAXHEADERSIZE = "server.request.maxHeaderSize";
    String SERVER_REQUEST_MAXBODYSIZE ="server.request.maxBodySize";
    String SERVER_REQUEST_MAXFILESIZE = "server.request.maxFileSize";
    String SERVER_REQUEST_USETEMPFILE = "server.request.useTempfile";
    String SERVER_REQUEST_ENCODING = "server.request.encoding";

    String SIGNAL_HTTP = "http";
    String SIGNAL_SOCKET = "socket";
    String SIGNAL_WEBSOCKET = "websocket";

    int SIGNAL_LIFECYCLE_INDEX = 99;
}
