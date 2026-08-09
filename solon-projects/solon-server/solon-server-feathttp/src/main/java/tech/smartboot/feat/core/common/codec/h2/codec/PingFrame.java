/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common.codec.h2.codec;

import java.nio.ByteBuffer;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class PingFrame extends Http2Frame {


    private long data;

    public PingFrame(int streamId, int flags, int remaining) {
        super(streamId, flags, remaining);
    }

    @Override
    public boolean decode(ByteBuffer buffer) {
        if (finishDecode()) {
            return true;
        }
        if (buffer.remaining() < 8) {
            return false;
        }
        data = buffer.getLong();
        remaining -= 8;
        return true;
    }

    @Override
    public int type() {
        return FRAME_TYPE_PING;
    }
}
