/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server;

import tech.smartboot.feat.core.common.FeatUtils;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public interface WebSocketResponse {
    /**
     * 发送文本响应
     *
     * @param text
     */
    void sendTextMessage(String text);

    /**
     * 发送二进制响应
     *
     * @param bytes
     */
    void sendBinaryMessage(byte[] bytes);

    /**
     * 发送二进制响应
     *
     * @param bytes
     */
    void sendBinaryMessage(byte[] bytes, int offset, int length);

    default void pong() {
        pong(FeatUtils.EMPTY_BYTES);
    }


    void pong(byte[] bytes);

    void ping(byte[] bytes);

    default void ping() {
        ping(FeatUtils.EMPTY_BYTES);
    }

    /**
     * 关闭ws通道
     */
    void close();

    void close(int code, String reason);

    /**
     * 输出数据
     */
    void flush();
}
