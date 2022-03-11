package org.noear.solon.core.message;

import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.PathUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * SocketD 会话（为 Message + Listener 架构服务 ）
 *
 * @author noear
 * @since 1.0
 * */
public interface Session {
    Object real();

    /**
     * 会话ID
     */
    String sessionId();

    /**
     * 方法
     */
    MethodType method();

    /**
     * URI（socket 可能为null）
     */
    URI uri();

    /**
     * 路径（socket 可能为null）
     */
    String path();


    /**
     * 获取请求的URI路径变量,根据路径表达式
     */
    default NvMap pathMap(String expr) {
        return PathUtil.pathVarMap(path(), expr);
    }

    /**
     * 请求头
     */
    String header(String name);

    /**
     * 设置请求头
     */
    void headerSet(String name, String value);

    /**
     * 请求头集合
     */
    NvMap headerMap();

    /**
     * 请求参数
     */
    String param(String name);

    /**
     * 设置请求参数
     */
    void paramSet(String name, String value);

    /**
     * 请求参数集合
     */
    NvMap paramMap();


    /**
     * 标识（为特定业务提供帮助）
     */
    int flag();

    /**
     * 标识设置
     */
    void flagSet(int flag);

    /**
     * 发送消息
     */
    void sendAsync(String message);

    /**
     * 发送消息
     */
    void sendAsync(Message message);

    /**
     * 发送消息
     */
    void send(String message);

    /**
     * 发送消息
     */
    void send(Message message);


    /**
     * 发送消息并等待响应
     */
    String sendAndResponse(String message);

    /**
     * 发送消息并等待响应
     */
    String sendAndResponse(String message, int timeout);

    /**
     * 发送消息并等待响应
     */
    Message sendAndResponse(Message message);

    /**
     * 发送消息并等待响应
     */
    Message sendAndResponse(Message message, int timeout);

    /**
     * 发送消息并异步回调
     */
    void sendAndCallback(String message, BiConsumer<String, Throwable> callback);

    /**
     * 发送消息并异步回调
     */
    void sendAndCallback(Message message, BiConsumer<Message, Throwable> callback);


    /**
     * 当前实例监听者（ListenEndpoint 为路径监听者，不限实例）
     */
    default void listener(Listener listener) {

    }

    /**
     * 当前实例监听者
     */
    default Listener listener() {
        return null;
    }

    /**
     * 关闭会话
     */
    void close() throws IOException;

    /**
     * 是否是有效的
     */
    boolean isValid();

    /**
     * 是否是安全的
     */
    boolean isSecure();

    //////////////////////////////////////////

    /**
     * 设置握手状态
     */
    void setHandshaked(boolean handshaked);

    /**
     * 获取握手状态
     */
    boolean getHandshaked();

    //////////////////////////////////////////

    /**
     * 远程地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * 本地地址
     */
    InetSocketAddress getLocalAddress();

    /**
     * 设置附件
     */
    void setAttachment(Object obj);

    /**
     * 获取附件
     */
    <T> T getAttachment();

    /**
     * 获取所有会话
     */
    Collection<Session> getOpenSessions();


    /**
     * 发送心跳
     */
    void sendHeartbeat();

    /**
     * 发送心跳
     */
    void sendHeartbeatAuto(int intervalSeconds);

    /**
     * 发送握手
     */
    void sendHandshake(Message message);

    /**
     * 发送握手并等待响应
     */
    Message sendHandshakeAndResponse(Message message);
}
