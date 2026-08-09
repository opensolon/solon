/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common.codec.websocket;

import tech.smartboot.feat.core.common.FeatUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class CloseReason {
    //1000 表示正常关闭，意思是建议的连接已经完成了。
    public static final int NORMAL_CLOSURE = 1000;
    //1001 表示端点“离开”（going away），例如服务器关闭或浏览器导航到其他页面
    public static final int GOING_AWAY = 1001;
    //1002 表示端点因为协议错误而终止连接。
    public static final int WRONG_CODE = 1002;
    //1003 表示端点由于它收到了不能接收的数据类型（例如，端点仅理解文本数据，但接收到了二进制消息）而终止连接。
    public static final int PROTOCOL_ERROR = 1003;
    //1007 表示端点因为消息中接收到的数据是不符合消息类型而终止连接（比如，文本消息中存在非 UTF-8[RFC3629]数据）。
    public static final int MSG_CONTAINS_INVALID_DATA = 1007;
    //1008 表示端点因为接收到的消息违反其策略而终止连接。这是一个当没有其他合适状态码（例如 1003 或 1009）或如果需要隐藏策略的具体细节时能被返回的通用状态码。
    public static final int MSG_VIOLATES_POLICY = 1008;
    //1009 表示端点因接收到的消息对它的处理来说太大而终止连接。
    public static final int MSG_TOO_BIG = 1009;
    //1010 表示端点（客户端）因为它期望服务器协商一个或多个扩展，但服务器没有在 WebSocket 握手响应消息中返回它们而终止连接。 所需要的扩展列表应该出现在关闭帧的/reason/部分。
    //注意，这个状态码不能被服务器端使用，因为它可以失败 WebSocket 握手。
    public static final int MISSING_EXTENSIONS = 1010;
    //1011表示服务器端因为遇到了一个不期望的情况使它无法满足请求而终止连接。
    public static final int UNEXPECTED_ERROR = 1011;
    private final int code;
    private final String reason;

    public CloseReason(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public CloseReason(byte[] payload) {
        if (payload.length == 0) {
            this.code = NORMAL_CLOSURE;
            this.reason = "";
        } else {
            this.code = (payload[0] & 0xFF) << 8 | (payload[1] & 0xFF);
            if (payload.length > 2) {
                reason = new String(payload, 2, payload.length - 2, StandardCharsets.UTF_8);
            } else {
                this.reason = "";
            }
        }
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public byte[] toBytes() {
        byte[] payload;
        if (FeatUtils.isBlank(reason)) {
            payload = new byte[2];
            payload[0] = (byte) ((code >>> 8) & 0xFF);
            payload[1] = (byte) (code & 0xFF);
        } else {
            byte[] data = reason.getBytes(StandardCharsets.UTF_8);
            payload = new byte[data.length + 2];
            payload[0] = (byte) ((code >>> 8) & 0xFF);
            payload[1] = (byte) (code & 0xFF);
            System.arraycopy(data, 0, payload, 2, data.length);
        }
        return payload;
    }
}
