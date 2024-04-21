package org.noear.solon.net.stomp;


import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * web ws消息转stomp消息
 * @author limliu
 * @since 2.7
 */
public class ToStompWebSocketListener implements WebSocketListener {
    static Logger log = LoggerFactory.getLogger(ToStompWebSocketListener.class);


    private StompListener stompListener;


    public ToStompWebSocketListener(StompListener stompListener){
        this.stompListener = stompListener;
    }


    @Override
    public void onOpen(WebSocket socket) {
        stompListener.onOpen(socket);
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        AtomicBoolean atomicBoolean = new AtomicBoolean(Boolean.TRUE);
        StompUtil.msgCodec.decode(text, msg -> {
            atomicBoolean.set(Boolean.FALSE);
            String command = msg.getCommand() == null ? "" : msg.getCommand();
            switch (command) {
                case Commands.CONNECT: {
                    stompListener.onConnect(socket, msg);
                    break;
                }
                case Commands.DISCONNECT: {
                    stompListener.onDisconnect(socket, msg);
                    break;
                }
                case Commands.SUBSCRIBE: {
                    stompListener.onSubscribe(socket, msg);
                    break;
                }
                case Commands.UNSUBSCRIBE: {
                    stompListener.onUnsubscribe(socket, msg);
                    break;
                }
                case Commands.SEND: {
                    stompListener.onSend(socket, msg);
                    break;
                }
                case Commands.ACK:
                case Commands.NACK: {
                    stompListener.onAck(socket, msg);
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
        socket.send(ByteBuffer.wrap(StompUtil.msgCodec.encode(message).getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer binary) throws IOException {
        String txt = Charset.forName("UTF-8").decode(binary).toString();
        this.onMessage(socket, txt);
    }

    @Override
    public void onClose(WebSocket socket) {
        stompListener.onClose(socket);
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        log.error("", error);
    }
}