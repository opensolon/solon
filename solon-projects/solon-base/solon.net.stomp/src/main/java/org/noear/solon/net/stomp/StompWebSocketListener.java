package org.noear.solon.net.stomp;


import cn.hutool.core.util.StrUtil;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;

public class StompWebSocketListener implements WebSocketListener {
    static Logger log = LoggerFactory.getLogger(StompEventListener.class);


    private StompEventListener stompEventListener;

    private MsgCodec msgCodec = new MsgCodecImpl();


    public StompWebSocketListener(StompEventListener stompEventListener){
        this.stompEventListener = stompEventListener;
    }


    @Override
    public void onOpen(WebSocket socket) {
        stompEventListener.onOpen(socket);
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        AtomicBoolean atomicBoolean = new AtomicBoolean(Boolean.TRUE);

        msgCodec.decode(text, msg -> {
            atomicBoolean.set(Boolean.FALSE);
            switch (StrUtil.blankToDefault(msg.getCommand(), "")) {
                case Commands.CONNECT: {
                    stompEventListener.onConnect(socket, msg);
                    break;
                }
                case Commands.DISCONNECT: {
                    stompEventListener.onDisconnect(socket, msg);
                    break;
                }
                case Commands.SUBSCRIBE: {
                    stompEventListener.onSubscribe(socket, msg);
                    break;
                }
                case Commands.UNSUBSCRIBE: {
                    stompEventListener.onUnsubscribe(socket, msg);
                    break;
                }
                case Commands.SEND: {
                    stompEventListener.onSend(socket, msg);
                    break;
                }
                case Commands.ACK:
                case Commands.NACK: {
                    stompEventListener.onAck(socket, msg);
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

    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer binary) throws IOException {
        String txt = Charset.forName("UTF-8").decode(binary).toString();
        this.onMessage(socket, txt);
    }

    @Override
    public void onClose(WebSocket socket) {
        stompEventListener.onClose(socket);
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        log.error("", error);
    }
}