package org.noear.solon.net.stomp;


import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;

public class ToStompWebSocketListener implements WebSocketListener {
    static Logger log = LoggerFactory.getLogger(StompListener.class);

    private StompListener listener;

    public ToStompWebSocketListener() {
        this(null);
    }

    public ToStompWebSocketListener(StompListener listener) {
        setListener(listener);
    }

    public void setListener(StompListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }


    @Override
    public void onOpen(WebSocket socket) {
        listener.onOpen(socket);
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        AtomicBoolean atomicBoolean = new AtomicBoolean(Boolean.TRUE);
        StompUtil.msgCodec.decode(text, msg -> {
            atomicBoolean.set(Boolean.FALSE);
            String command = msg.getCommand() == null ? "" : msg.getCommand();
            switch (command) {
                case Commands.CONNECT: {
                    listener.onConnect(socket, msg);
                    break;
                }
                case Commands.DISCONNECT: {
                    listener.onDisconnect(socket, msg);
                    break;
                }
                case Commands.SUBSCRIBE: {
                    listener.onSubscribe(socket, msg);
                    break;
                }
                case Commands.UNSUBSCRIBE: {
                    listener.onUnsubscribe(socket, msg);
                    break;
                }
                case Commands.SEND: {
                    listener.onSend(socket, msg);
                    break;
                }
                case Commands.ACK:
                case Commands.NACK: {
                    listener.onAck(socket, msg);
                    break;
                }
                default: {
                    //未知命令
                    log.warn("session unknown, {}\r\n{}", socket.id(), text);
                    doSend(socket, new Message(Commands.UNKNOWN, text));
                }
            }
        });

        if (atomicBoolean.get()) {
            if (log.isDebugEnabled()) {
                log.debug("session ping, {}", socket.id());
            }
            //可能是ping，响应
            doSend(socket, new Message(Commands.MESSAGE, text));
        }
    }

    protected void doSend(WebSocket socket, Message message) {
        StompUtil.send(socket, message);
    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer binary) throws IOException {
        String txt = Charset.forName("UTF-8").decode(binary).toString();
        this.onMessage(socket, txt);
    }

    @Override
    public void onClose(WebSocket socket) {
        listener.onClose(socket);
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        log.error("", error);
    }
}