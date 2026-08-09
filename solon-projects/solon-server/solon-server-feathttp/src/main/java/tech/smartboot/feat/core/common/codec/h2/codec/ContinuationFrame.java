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

import io.github.smartboot.socket.transport.WriteBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class ContinuationFrame extends Http2Frame {
    private ByteBuffer fragment = EMPTY_BUFFER;

    public ContinuationFrame(int streamId, int flags, int remaining) {
        super(streamId, flags, remaining);
    }


    @Override
    public boolean decode(ByteBuffer buffer) {
        return false;
    }

    @Override
    public void writeTo(WriteBuffer writeBuffer) throws IOException {
        int payloadLength = 0;
        byte flags = (byte) this.flags;

        payloadLength += fragment.remaining();

        // Write frame header
        writeBuffer.writeInt(payloadLength << 8 | FRAME_TYPE_CONTINUATION);
        writeBuffer.writeByte(flags);
        System.out.println("write continuation ,streamId:" + streamId);
        writeBuffer.writeInt(streamId);

        // Write fragment

        writeBuffer.write(fragment.array(), 0, fragment.remaining());

    }

    public ByteBuffer getFragment() {
        return fragment;
    }

    public void setFragment(ByteBuffer fragment) {
        this.fragment = fragment;
    }

    @Override
    public int type() {
        return FRAME_TYPE_CONTINUATION;
    }

}
