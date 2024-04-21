package org.noear.solon.net.stomp;

/**
 * 命令
 * @author limliu
 * @since 2.7
 */
public class Commands {

    /**
     * 发起连接
     */
    public static final String CONNECT = "CONNECT";

    /**
     * 已连接
     */
    public static final String CONNECTED = "CONNECTED";

    /**
     * 断开连接
     */
    public static final String DISCONNECT = "DISCONNECT";

    /**
     * 发送消息
     */
    public static final String SEND = "SEND";

    /**
     * 收到消息
     */
    public static final String MESSAGE = "MESSAGE";

    /**
     * 收到凭据
     */
    public static final String RECEIPT = "RECEIPT";

    /**
     * 发起订阅
     */
    public static final String SUBSCRIBE = "SUBSCRIBE";

    /**
     * 发起退订
     */
    public static final String UNSUBSCRIBE = "UNSUBSCRIBE";

    /**
     * 确认消息
     */
    public static final String ACK = "ACK";

    /**
     * 确认消息
     */
    public static final String NACK = "NACK";

    /**
     * 收到错误
     */
    public static final String ERROR = "ERROR";

    /**
     * 未知命令
     */
    public static final String UNKNOWN = "UNKNOWN";

    /**
     * PING
     */
    public static final String PING = "PING";

}
