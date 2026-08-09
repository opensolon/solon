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
public class SettingsFrame extends Http2Frame {

    public static final int TYPE = 0x4;

    // Flags
    public static final int ACK = 0x1;

    private int headerTableSize = 4096;
    private int enablePush = 1;
    private int maxConcurrentStreams = Integer.MAX_VALUE;
    private int initialWindowSize = 65535;
    private int maxFrameSize = 16 * 1024;
    private int maxHeaderListSize = -1;
    /**
     * 允许发送方以八位字节通知远程端点用于解码头块的头压缩表的最大大小。
     * 编码器可以通过使用特定于报头块内的报头压缩格式的信号来选择等于或小于该值的任何大小(参见COMPRESSION)。
     * 初始值为 4,096 个八位字节。
     */
    public static final short HEADER_TABLE_SIZE = 0x1;
    public static final short ENABLE_PUSH = 0x2;
    public static final short MAX_CONCURRENT_STREAMS = 0x3;
    public static final short INITIAL_WINDOW_SIZE = 0x4;
    public static final short MAX_FRAME_SIZE = 0x5;
    public static final short MAX_HEADER_LIST_SIZE = 0x6;

    public static final int MAX_PARAM = 0x6;


    public SettingsFrame(int streamId, int flags, int payloadLength) {
        super(streamId, flags, payloadLength);
    }

    public SettingsFrame(int streamId, boolean ack) {
        super(streamId, ack ? ACK : 0, 0);
    }

    @Override
    public boolean decode(ByteBuffer buffer) {
        while (buffer.remaining() >= 6 && remaining > 0) {
            remaining -= 6;
            short identifier = buffer.getShort();
            int value = buffer.getInt();
            switch (identifier) {
                case HEADER_TABLE_SIZE:
                    headerTableSize = value;
                    break;
                case ENABLE_PUSH:
                    enablePush = value;
                    break;
                case MAX_CONCURRENT_STREAMS:
                    maxConcurrentStreams = value;
                    break;
                case INITIAL_WINDOW_SIZE:
                    initialWindowSize = value;
                    break;
                case MAX_FRAME_SIZE:
                    maxFrameSize = value;
                    break;
                case MAX_HEADER_LIST_SIZE:
                    maxHeaderListSize = value;
                    break;
                default:
                    throw new IllegalArgumentException("illegal parameter");
            }
        }
        if (remaining < 0) {
            throw new IllegalStateException();
        }
        return remaining == 0;
    }

    @Override
    public void writeTo(WriteBuffer writeBuffer) throws IOException {
        System.out.println("write setting,streamId+" + streamId);
        if (getFlag(ACK)) {
            writeBuffer.writeInt(TYPE);
            writeBuffer.writeByte((byte) ACK);
            writeBuffer.writeInt(streamId);
        } else {
            int payloadLength = 0;
            if (headerTableSize != -1) payloadLength += 6;
            if (enablePush != -1) payloadLength += 6;
            if (maxConcurrentStreams != -1) payloadLength += 6;
            if (initialWindowSize != -1) payloadLength += 6;
            if (maxFrameSize != -1) payloadLength += 6;
            if (maxHeaderListSize != -1) payloadLength += 6;

            writeBuffer.writeInt(payloadLength << 8 | TYPE);
            writeBuffer.writeByte((byte) 0); // flags
            writeBuffer.writeInt(streamId);

            if (headerTableSize != -1) {
                writeBuffer.writeShort(HEADER_TABLE_SIZE);
                writeBuffer.writeInt(headerTableSize);
            }
            if (enablePush != -1) {
                writeBuffer.writeShort(ENABLE_PUSH);
                writeBuffer.writeInt(enablePush);
            }
            if (maxConcurrentStreams != -1) {
                writeBuffer.writeShort(MAX_CONCURRENT_STREAMS);
                writeBuffer.writeInt(maxConcurrentStreams);
            }
            if (initialWindowSize != -1) {
                writeBuffer.writeShort(INITIAL_WINDOW_SIZE);
                writeBuffer.writeInt(initialWindowSize);
            }
            if (maxFrameSize != -1) {
                writeBuffer.writeShort(MAX_FRAME_SIZE);
                writeBuffer.writeInt(maxFrameSize);
            }
            if (maxHeaderListSize != -1) {
                writeBuffer.writeShort(MAX_HEADER_LIST_SIZE);
                writeBuffer.writeInt(maxHeaderListSize);
            }
        }
    }

    public int getHeaderTableSize() {
        return headerTableSize;
    }

    public void setHeaderTableSize(int headerTableSize) {
        this.headerTableSize = headerTableSize;
    }

    public int getEnablePush() {
        return enablePush;
    }

    public void setEnablePush(int enablePush) {
        this.enablePush = enablePush;
    }

    public int getMaxConcurrentStreams() {
        return maxConcurrentStreams;
    }

    public void setMaxConcurrentStreams(int maxConcurrentStreams) {
        this.maxConcurrentStreams = maxConcurrentStreams;
    }

    public int getInitialWindowSize() {
        return initialWindowSize;
    }

    public void setInitialWindowSize(int initialWindowSize) {
        this.initialWindowSize = initialWindowSize;
    }

    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    public void setMaxFrameSize(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    public int getMaxHeaderListSize() {
        return maxHeaderListSize;
    }

    public void setMaxHeaderListSize(int maxHeaderListSize) {
        this.maxHeaderListSize = maxHeaderListSize;
    }

    @Override
    public int type() {
        return FRAME_TYPE_SETTINGS;
    }

    @Override
    public String toString() {
        return "SettingsFrame{" +
                "headerTableSize=" + headerTableSize +
                ", enablePush=" + enablePush +
                ", maxConcurrentStreams=" + maxConcurrentStreams +
                ", initialWindowSize=" + initialWindowSize +
                ", maxFrameSize=" + maxFrameSize +
                ", maxHeaderListSize=" + maxHeaderListSize +
                '}';
    }
}
