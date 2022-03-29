package org.noear.solon.socketd;

import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * 会话基类
 *
 * @author noear
 * @since 1.2
 * */
public abstract class SessionBase implements Session {

    static final Logger log = LoggerFactory.getLogger(SessionBase.class);

    //路径
    public void pathNew(String pathNew) {
        this.pathNew = pathNew;
    }

    private String pathNew;
    public String pathNew() {
        if (pathNew == null) {
            return path();
        } else {
            return pathNew;
        }
    }

    //
    //标志
    //
    private int _flag = SessionFlag.unknown;
    @Override
    public int flag() {
        return _flag;
    }

    @Override
    public void flagSet(int flag) {
        _flag = flag;
    }

    //
    //请求头
    //
    @Override
    public String header(String name) {
        return headerMap().get(name);
    }

    @Override
    public void headerSet(String name, String value) {
        headerMap().put(name, value);
    }

    private NvMap headerMap;
    public NvMap headerMap() {
        if(headerMap == null){
            headerMap = new NvMap();
        }

        return headerMap;
    }

    //
    //请求参数
    //
    @Override
    public String param(String name) {
        return paramMap().get(name);
    }

    @Override
    public void paramSet(String name, String value) {
        paramMap().put(name, value);
    }

    private NvMap paramMap;
    public NvMap paramMap() {
        if (paramMap == null) {
            paramMap = new NvMap();

            if(uri() != null) {
                String query = uri().getQuery();

                if (Utils.isNotEmpty(query)) {
                    String[] ss = query.split("&");
                    for (String kv : ss) {
                        String[] s = kv.split("=");
                        paramMap.put(s[0], s[1]);
                    }
                }
            }
        }

        return paramMap;
    }


    //////////////////////////////////////////

    private AtomicBoolean _handshaked = new AtomicBoolean();

    /**
     * 设置握手状态
     */
    public void setHandshaked(boolean handshaked) {
        _handshaked.set(handshaked);
    }

    /**
     * 获取握手状态
     */
    public boolean getHandshaked() {
        return _handshaked.get();
    }

    //////////////////////////////////////////


    @Override
    public void send(Message message) {
        log.trace("Session send: {}", message);
    }

    @Override
    public String sendAndResponse(String message) {
        return sendAndResponse(Message.wrap(message)).bodyAsString();
    }

    @Override
    public String sendAndResponse(String message, int timeout) {
        return sendAndResponse(Message.wrap(message), timeout).bodyAsString();
    }

    /**
     * 用于支持双向RPC
     */
    @Override
    public Message sendAndResponse(Message message) {
        return sendAndResponse(message, 0);
    }

    /**
     * 用于支持双向RPC
     *
     * @param timeout 单位为秒
     * */
    @Override
    public Message sendAndResponse(Message message, int timeout) {
        if (Utils.isEmpty(message.key())) {
            throw new IllegalArgumentException("SendAndResponse message no key");
        }

        if (timeout < 1) {
            timeout = RequestManager.REQUEST_AND_RESPONSE_TIMEOUT_SECONDS;
        }

        //注册请求
        CompletableFuture<Message> request = new CompletableFuture<>();
        RequestManager.register(message, request);

        //发送消息
        send(message);

        try {
            //等待响应
            return request.get(timeout, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void sendAndCallback(String message, BiConsumer<String, Throwable> callback) {
        sendAndCallback(Message.wrap(message), (msg, err) -> {
            if (msg == null) {
                callback.accept(null, err);
            } else {
                callback.accept(msg.bodyAsString(), err);
            }
        });
    }

    /**
     * 用于支持异步回调
     */
    @Override
    public void sendAndCallback(Message message, BiConsumer<Message, Throwable> callback) {
        if (Utils.isEmpty(message.key())) {
            throw new IllegalArgumentException("sendAndCallback message no key");
        }

        //注册请求
        CompletableFuture<Message> request = new CompletableFuture<>();
        RequestManager.register(message, request);

        //等待响应
        request.whenCompleteAsync(callback);

        //发送消息
        send(message);
    }

    private Listener listener;

    /**
     * 当前实例监听者（ListenEndpoint 为路径监听者，不限实例）
     */
    @Override
    public Listener listener() {
        return listener;
    }

    @Override
    public void listener(Listener listener) {
        this.listener = listener;
    }

    protected void onOpen() {
        if (listener() != null) {
            listener().onOpen(this);
        }
    }


    /**
     * 发送心跳
     */
    @Override
    public void sendHeartbeat() {
        send(Message.wrapHeartbeat());
    }

    private boolean _sendHeartbeatAuto = false;

    @Override
    public void sendHeartbeatAuto(int intervalSeconds) {
        if (_sendHeartbeatAuto) {
            return;
        }

        _sendHeartbeatAuto = true;

        Utils.scheduled.scheduleWithFixedDelay(
                () -> {
                    try {
                        sendHeartbeat();
                    } catch (Throwable ex) {
                        EventBus.push(ex);
                    }
                }, 1, intervalSeconds, TimeUnit.SECONDS);
    }

    //保存最后一次握手的信息；之后重链时使用
    protected Message handshakeMessage;

    @Override
    public void sendHandshake(Message message) {
        if (message.flag() == MessageFlag.handshake) {
            try {
                send(message);
            }finally {
                //发完之后，再缓存 //不然，会发两次
                handshakeMessage = message;
            }
        } else {
            throw new IllegalArgumentException("The message flag not handshake");
        }
    }

    @Override
    public Message sendHandshakeAndResponse(Message message) {
        if (message.flag() == MessageFlag.handshake) {
            Message rst = sendAndResponse(message);

            //发完之后，再缓存 //不然，会发两次
            handshakeMessage = message;

            return rst;
        } else {
            throw new IllegalArgumentException("The message flag not handshake");
        }
    }
}