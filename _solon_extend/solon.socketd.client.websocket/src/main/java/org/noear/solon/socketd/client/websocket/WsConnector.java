package org.noear.solon.socketd.client.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ConnectorBase;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

class WsConnector extends ConnectorBase<WebSocket> {

    private SSLSocketFactory sslSocketFactory;

    public WsConnector(URI uri, boolean autoReconnect) {
        super(uri, autoReconnect);
    }


    @Override
    public Class<WebSocket> driveType() {
        return WebSocket.class;
    }

    @Override
    public WebSocket open(Session session) {
        try {
            WebSocketClient socket = new WsSocketClientImp(uri(), session);

            if (uri().getScheme().startsWith("wss")) {
                enableTls(socket);
            }

            socket.connectBlocking();

            return socket;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private void enableTls(WebSocketClient client) throws Exception {
        //
        // wss support
        //
        if (sslSocketFactory == null) {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain,
                                                       String authType) {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain,
                                                       String authType) {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, new SecureRandom());

            sslSocketFactory = sslContext.getSocketFactory();
        }

        client.setSocketFactory(sslSocketFactory);
    }
}
