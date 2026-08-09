/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */
package tech.smartboot.feat.core.common.codec.h2.hpack;

import java.nio.ByteBuffer;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public final class HPACK {


    private HPACK() {
    }

    @FunctionalInterface
    interface BufferUpdateConsumer {
        void accept(long data, int len);
    }

    @SuppressWarnings("fallthrough")
    public static int read(ByteBuffer source,
                           long buffer,
                           int bufferLen,
                           BufferUpdateConsumer consumer) {
        // read as much as possible (up to 8 bytes)
        int nBytes = Math.min((64 - bufferLen) >> 3, source.remaining());
        switch (nBytes) {
            case 0:
                break;
            case 3:
                buffer |= ((source.get() & 0x00000000000000ffL) << (56 - bufferLen));
                bufferLen += 8;
            case 2:
                buffer |= ((source.get() & 0x00000000000000ffL) << (56 - bufferLen));
                bufferLen += 8;
            case 1:
                buffer |= ((source.get() & 0x00000000000000ffL) << (56 - bufferLen));
                bufferLen += 8;
                consumer.accept(buffer, bufferLen);
                break;
            case 7:
                buffer |= ((source.get() & 0x00000000000000ffL) << (56 - bufferLen));
                bufferLen += 8;
            case 6:
                buffer |= ((source.get() & 0x00000000000000ffL) << (56 - bufferLen));
                bufferLen += 8;
            case 5:
                buffer |= ((source.get() & 0x00000000000000ffL) << (56 - bufferLen));
                bufferLen += 8;
            case 4:
                buffer |= ((source.getInt() & 0x00000000ffffffffL) << (32 - bufferLen));
                bufferLen += 32;
                consumer.accept(buffer, bufferLen);
                break;
            case 8:
                buffer = source.getLong();
                bufferLen = 64;
                consumer.accept(buffer, bufferLen);
                break;
            default:
                throw new InternalError(String.valueOf(nBytes));
        }
        return nBytes;
    }

    // The number of bytes that can be written at once
    // (calculating in bytes, not bits, since
    //  destination.remaining() * 8 might overflow)
    @SuppressWarnings("fallthrough")
    public static int write(long buffer,
                            int bufferLen,
                            BufferUpdateConsumer consumer,
                            ByteBuffer destination) {
        int nBytes = Math.min(bufferLen >> 3, destination.remaining());
        switch (nBytes) {
            case 0:
                break;
            case 3:
                destination.put((byte) (buffer >>> 56));
                buffer <<= 8;
                bufferLen -= 8;
            case 2:
                destination.put((byte) (buffer >>> 56));
                buffer <<= 8;
                bufferLen -= 8;
            case 1:
                destination.put((byte) (buffer >>> 56));
                buffer <<= 8;
                bufferLen -= 8;
                consumer.accept(buffer, bufferLen);
                break;
            case 7:
                destination.put((byte) (buffer >>> 56));
                buffer <<= 8;
                bufferLen -= 8;
            case 6:
                destination.put((byte) (buffer >>> 56));
                buffer <<= 8;
                bufferLen -= 8;
            case 5:
                destination.put((byte) (buffer >>> 56));
                buffer <<= 8;
                bufferLen -= 8;
            case 4:
                destination.putInt((int) (buffer >>> 32));
                buffer <<= 32;
                bufferLen -= 32;
                consumer.accept(buffer, bufferLen);
                break;
            case 8:
                destination.putLong(buffer);
                buffer = 0;
                bufferLen = 0;
                consumer.accept(buffer, bufferLen);
                break;
            default:
                throw new InternalError(String.valueOf(nBytes));
        }
        return nBytes;
    }

    /*
     * Returns the number of bytes the given number of bits constitute.
     */
    static int bytesForBits(int n) {
        assert (n / 8 + (n % 8 != 0 ? 1 : 0)) == (n + 7) / 8
                && (n + 7) / 8 == ((n + 7) >> 3) : n;
        return (n + 7) >> 3;
    }
}
