package org.noear.solon.net.stomp;

import org.noear.solon.net.websocket.WebSocket;

/**
 * 消息监听器
 *
 * @author limliu
 * @since 2.7
 */
public interface StompListener {

    /**
     * 打开链接，可以鉴权；参数通过url和head方式指定
     *
     * @param socket
     */
    void onOpen(WebSocket socket);

    /**
     * 创建链接
     *
     * @param socket
     * @param message
     */
    void onConnect(WebSocket socket, Message message);

    /**
     * 链接关闭，被动监听；当断开时触发
     *
     * @param socket
     */
    void onClose(WebSocket socket);


    /**
     * 主动断开链接
     *
     * @param socket
     * @param message
     */
    void onDisconnect(WebSocket socket, Message message);


    /**
     * 订阅消息
     *
     * @param socket
     * @param message
     */
    void onSubscribe(WebSocket socket, Message message);

    /**
     * 取消消息订阅
     *
     * @param socket
     * @param message
     */
    void onUnsubscribe(WebSocket socket, Message message);


    /**
     * 发送消息
     *
     * @param socket
     * @param message
     */
    void onSend(WebSocket socket, Message message);


    /**
     * 消息确认
     *
     * @param socket
     * @param message
     */
    void onAck(WebSocket socket, Message message);
}
