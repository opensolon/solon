package org.noear.solon.net.stomp;

public class Header {

    /**
     * 订阅者 ID
     */
    public static final String ID = "id";

    /**
     * 接受版本
     */
    public static final String ACCEPT_VERSION = "accept-version";

    /**
     * 心跳
     */
    public static final String HEART_BEAT = "heart-beat";

    /**
     * 目的地
     */
    public static final String DESTINATION = "destination";

    /**
     * 内容类型
     */
    public static final String CONTENT_TYPE = "content-type";

    /**
     * 消息 ID
     */
    public static final String MESSAGE_ID = "message-id";

    /**
     * 凭据
     */
    public static final String RECEIPT = "receipt";

    /**
     * 凭据 ID
     */
    public static final String RECEIPT_ID = "receipt-id";

    /**
     * 订阅者
     */
    public static final String SUBSCRIPTION = "subscription";

    /**
     * 确认
     */
    public static final String ACK = "ack";

    /**
     * 确认
     */
    public static final String NACK = "nack";

    /**
     * 账号
     */
    public static final String LOGIN = "login";

    /**
     * 密码
     */
    public static final String PASSCODE = "passcode";

    /**
     * 鉴权-有权限
     */
    public static final String AUTHORIZED = "Authorized";

    /**
     * 鉴权-无权限
     */
    public static final String UNAUTHORIZED = "Unauthorized";

    /**
     *
     */
    public static final String SERVER = "server";

    /**
     *
     */
    public static final String VERSION = "version";

    /**
     * 是否开启持久化, boolean类型
     */
    public static final String OPEN_PERSISTENCE = "openPersistence";

    /**
     * 是否开启ack, boolean类型
     */
    public static final String OPEN_ACK = "openAck";


    private final String key;
    private final String value;


    public Header(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + ':' + value;
    }

}