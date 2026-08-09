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

import java.io.IOException;
import java.nio.ByteBuffer;

//
// Custom implementation of ISO/IEC 8859-1:1998
//
// The rationale behind this is not to deal with CharsetEncoder/CharsetDecoder,
// basically because it would require wrapping every single CharSequence into a
// CharBuffer and then copying it back.
//
// But why not to give a CharBuffer instead of Appendable? Because I can choose
// an Appendable (e.g. StringBuilder) that adjusts its length when needed and
// therefore not to deal with pre-sized CharBuffers or copying.
//
// The encoding is simple and well known: 1 byte <-> 1 char
//
/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
final class ISO_8859_1 {

    private ISO_8859_1() {
    }

    public static final class Reader {

        private final HPACK.BufferUpdateConsumer UPDATER =
                (buf, bufLen) -> {
                    buffer = buf;
                    bufferLen = bufLen;
                };

        private long buffer;
        private int bufferLen;

        public void read(ByteBuffer source, Appendable destination)
                throws IOException {
            while (true) {
                int nBytes = HPACK.read(source, buffer, bufferLen, UPDATER);
                if (nBytes == 0) {
                    return;
                }
                assert bufferLen % 8 == 0 : bufferLen;
                while (bufferLen > 0) {
                    char c = (char) (buffer >>> 56);
                    try {
                        destination.append(c);
                    } catch (IOException e) {
                        throw new IOException(
                                "Error appending to the destination", e);
                    }
                    buffer <<= 8;
                    bufferLen -= 8;
                }
            }
        }

        public Reader reset() {
            buffer = 0;
            bufferLen = 0;
            return this;
        }
    }

    public static final class Writer {

        private final HPACK.BufferUpdateConsumer UPDATER =
                (buf, bufLen) -> {
                    buffer = buf;
                    bufferLen = bufLen;
                };

        private CharSequence source;
        private int pos;
        private int end;
        private long buffer;
        private int bufferLen;

        public Writer configure(CharSequence source, int start, int end) {
            this.source = source;
            this.pos = start;
            this.end = end;
            return this;
        }

        public boolean write(ByteBuffer destination) {
            while (true) {
                while (true) { // stuff codes into long
                    if (pos >= end) {
                        break;
                    }
                    char c = source.charAt(pos);
                    if (c > 255) {
                        throw new IllegalArgumentException(Integer.toString((int) c));
                    }
                    if (bufferLen <= 56) {
                        buffer |= (((long) c) << (56 - bufferLen)); // append
                        bufferLen += 8;
                        pos++;
                    } else {
                        break;
                    }
                }
                if (bufferLen == 0) {
                    return true;
                }
                int nBytes = HPACK.write(buffer, bufferLen, UPDATER, destination);
                if (nBytes == 0) {
                    return false;
                }
            }
        }

        public Writer reset() {
            source = null;
            pos = -1;
            end = -1;
            buffer = 0;
            bufferLen = 0;
            return this;
        }
    }
}
