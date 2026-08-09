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

import tech.smartboot.feat.core.common.FeatUtils;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class GoAwayFrame extends Http2Frame {
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_DEBUG_DATA = 1;
    private int state = STATE_DEFAULT;
    private int lastStream;
    private int errorCode;
    private byte[] debugData;

    public GoAwayFrame(int streamId, int flags, int remaining) {
        super(streamId, flags, remaining);
    }


    @Override
    public boolean decode(ByteBuffer buffer) {
        if (finishDecode()) {
            return true;
        }
        switch (state) {
            case STATE_DEFAULT:
                if (buffer.remaining() < 8) {
                    return false;
                }
                lastStream = buffer.getInt();
                errorCode = buffer.getInt();
                remaining -= 8;
                state = STATE_DEBUG_DATA;
            case STATE_DEBUG_DATA:
                if (remaining > 0) {
                    if (buffer.remaining() < remaining) {
                        return false;
                    }
                    debugData = new byte[remaining];
                    buffer.get(debugData);
                } else {
                    debugData = FeatUtils.EMPTY_BYTE_ARRAY;
                }
                remaining = 0;
                break;
            default:
                throw new IllegalStateException();
        }
        checkEndRemaining();
        return true;
    }

    @Override
    public int type() {
        return FRAME_TYPE_GOAWAY;
    }


    @Override
    public String toString() {
        return super.toString() + " Debugdata: " + new String(debugData, UTF_8);
    }

    public int getLastStream() {
        return this.lastStream;
    }

    public byte[] getDebugData() {
        return debugData.clone();
    }

}
