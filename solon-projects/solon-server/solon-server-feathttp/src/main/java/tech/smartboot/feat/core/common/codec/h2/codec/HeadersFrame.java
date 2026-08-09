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
import tech.smartboot.feat.core.common.FeatUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class HeadersFrame extends Http2Frame {
    private int padLength;
    private int streamDependency;
    private int weight;
    private boolean exclusive;
    private ByteBuffer fragment = EMPTY_BUFFER;
    private byte[] padding = FeatUtils.EMPTY_BYTE_ARRAY;

    public HeadersFrame(int streamId, int flags, int remaining) {
        super(streamId, flags, remaining);
    }


    @Override
    public boolean decode(ByteBuffer buffer) {
        if (finishDecode()) {
            return true;
        }
        switch (state) {
            case STATE_PAD_LENGTH:
                if (getFlag(FLAG_PADDED)) {
                    if (!buffer.hasRemaining()) {
                        return false;
                    }
                    padLength = buffer.get();
                    if (padLength < 0) {
                        throw new IllegalStateException();
                    }
                    remaining = -1;
                }
                state = STATE_STREAM_DEPENDENCY;
            case STATE_STREAM_DEPENDENCY:
                if (getFlag(FLAG_PRIORITY)) {
                    if (buffer.remaining() < 5) {
                        return false;
                    }
                    streamDependency = buffer.getInt();
                    weight = buffer.get() & 0xFF;
                    remaining -= 5;
                }
                state = STATE_FRAGMENT;
                fragment = ByteBuffer.allocate(remaining - padLength);
            case STATE_FRAGMENT:
                int min = Math.min(buffer.remaining(), fragment.remaining());
                int limit = buffer.limit();
                buffer.limit(buffer.position() + min);
                fragment.put(buffer);
                buffer.limit(limit);
                remaining -= min;
                if (fragment.hasRemaining()) {
                    return false;
                }
                fragment.flip();
                // Now 'headers' contains the decoded HTTP/2 headers
                state = STATE_PADDING;
            case STATE_PADDING:
                if (buffer.remaining() < padLength) {
                    return false;
                }
                if (padLength > 0) {
                    padding = new byte[padLength];
                    buffer.get(padding);
                    remaining -= padLength;
                }
        }
        checkEndRemaining();
        return true;
    }

    @Override
    public void writeTo(WriteBuffer writeBuffer) throws IOException {
        int payloadLength = 0;
        byte flags = (byte) this.flags;

        // Calculate payload length and set flags
        boolean padded = padding != null && padding.length > 0;
        if (padded) {
            payloadLength += 1 + padding.length;
            flags |= FLAG_PADDED;
        }
        if (weight > 0) {
            payloadLength += 5;
            flags |= FLAG_PRIORITY;
        }

        payloadLength += fragment.remaining();

        // Write frame header
        writeBuffer.writeInt(payloadLength << 8 | FRAME_TYPE_HEADERS);
        writeBuffer.writeByte(flags);
        System.out.println("write header ,streamId:" + streamId);
        writeBuffer.writeInt(streamId);

        // Write pad length if padded
        if (padded) {
            writeBuffer.writeByte((byte) padding.length);
        }

        // Write stream dependency and weight if priority is set
        if (hasFlag(flags, FLAG_PRIORITY)) {
            writeBuffer.writeInt(streamDependency);
            writeBuffer.writeByte((byte) weight);
        }

        // Write fragment

        writeBuffer.write(fragment.array(), 0, fragment.remaining());

        // Write padding if padded
        if (padded) {
            writeBuffer.write(padding);
        }
    }

    public ByteBuffer getFragment() {
        return fragment;
    }

    public void setFragment(ByteBuffer fragment) {
        this.fragment = fragment;
    }

    @Override
    public int type() {
        return FRAME_TYPE_HEADERS;
    }


}
