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

import tech.smartboot.feat.core.common.HeaderValue;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class HpackDecoder {
    private static final int STATIC_TABLE_LENGTH = 61;
    private static final Map<Integer, HeaderValue> STATIC_TABLE = createStaticTable();
    private static final HuffmanDecoder HUFFMAN_DECODER = new HuffmanDecoder();

    private final List<HeaderValue> dynamicTable;
    private int dynamicTableSize;
    private int maxDynamicTableSize;

    public HpackDecoder(int maxDynamicTableSize) {
        this.dynamicTable = new ArrayList<>();
        this.dynamicTableSize = 0;
        this.maxDynamicTableSize = maxDynamicTableSize;
    }

    public List<HeaderValue> decode(ByteBuffer headerBlock) throws HpackException {
        List<HeaderValue> headers = new ArrayList<>();

        while (headerBlock.hasRemaining()) {
            int b = headerBlock.get() & 0xFF;
            if ((b & 0x80) != 0) {
                // Indexed Header Field
                int index = decodeInteger(headerBlock, 7);
                headers.add(getIndexedHeader(index));
            } else if ((b & 0x40) != 0) {
                // Literal Header Field with Incremental Indexing
                int index = decodeInteger(headerBlock, 6);
                String name = getHeaderFieldName(index, headerBlock);
                String value = decodeString(headerBlock);
                HeaderValue header = new HeaderValue(name, value);
                headers.add(header);
                addToDynamicTable(header);
            } else if ((b & 0x20) != 0) {
                // Dynamic Table Size Update
                int newMaxDynamicTableSize = decodeInteger(headerBlock, 5);
                setMaxDynamicTableSize(newMaxDynamicTableSize);
            } else {
                // Literal Header Field without Indexing or Never Indexed
                int index = decodeInteger(headerBlock, 4);
                String name = getHeaderFieldName(index, headerBlock);
                String value = decodeString(headerBlock);
                headers.add(new HeaderValue(name, value));
            }
        }

        return headers;
    }

    private HeaderValue getIndexedHeader(int index) throws HpackException {
        if (index <= 0 || index > STATIC_TABLE_LENGTH + dynamicTable.size()) {
            throw new HpackException("Invalid header index: " + index);
        }
        if (index <= STATIC_TABLE_LENGTH) {
            return STATIC_TABLE.get(index);
        } else {
            return dynamicTable.get(index - STATIC_TABLE_LENGTH - 1);
        }
    }

    private String getHeaderFieldName(int index, ByteBuffer headerBlock) throws HpackException {
        if (index == 0) {
            return decodeString(headerBlock);
        } else {
            return getIndexedHeader(index).getName();
        }
    }

    private void addToDynamicTable(HeaderValue header) {
        int headerSize = header.getName().length() + header.getValue().length() + 32;
        while (dynamicTableSize + headerSize > maxDynamicTableSize && !dynamicTable.isEmpty()) {
            HeaderValue removed = dynamicTable.remove(dynamicTable.size() - 1);
            dynamicTableSize -= removed.getName().length() + removed.getValue().length() + 32;
        }
        if (headerSize <= maxDynamicTableSize) {
            dynamicTable.add(0, header);
            dynamicTableSize += headerSize;
        }
    }

    private void setMaxDynamicTableSize(int newMaxDynamicTableSize) {
        maxDynamicTableSize = newMaxDynamicTableSize;
        while (dynamicTableSize > maxDynamicTableSize && !dynamicTable.isEmpty()) {
            HeaderValue removed = dynamicTable.remove(dynamicTable.size() - 1);
            dynamicTableSize -= removed.getName().length() + removed.getValue().length() + 32;
        }
    }

    private int decodeInteger(ByteBuffer buf, int n) throws HpackException {
        int max = (1 << n) - 1;
        int value = buf.get() & max;
        if (value < max) {
            return value;
        }
        int m = 0;
        int b;
        do {
            if (!buf.hasRemaining()) {
                throw new HpackException("Incomplete integer encoding");
            }
            b = buf.get() & 0xFF;
            value += (b & 127) << m;
            m += 7;
            if (m > 32) {
                throw new HpackException("Integer encoding too large");
            }
        } while ((b & 128) == 128);
        return value;
    }

    private String decodeString(ByteBuffer buf) throws HpackException {
        if (!buf.hasRemaining()) {
            throw new HpackException("Empty string encoding");
        }
        int h = buf.get() & 0xFF;
        int length = decodeInteger(buf, 7);
        if (buf.remaining() < length) {
            throw new HpackException("Incomplete string encoding");
        }
        byte[] array = new byte[length];
        buf.get(array);
        if ((h & 0x80) == 0x80) {
            // Huffman encoded
            return HUFFMAN_DECODER.decode(array);
        } else {
            return new String(array, StandardCharsets.UTF_8);
        }
    }

    private static Map<Integer, HeaderValue> createStaticTable() {
        Map<Integer, HeaderValue> table = new HashMap<>();
        table.put(1, new HeaderValue(":authority", ""));
        table.put(2, new HeaderValue(":method", "GET"));
        table.put(3, new HeaderValue(":method", "POST"));
        table.put(4, new HeaderValue(":path", "/"));
        table.put(5, new HeaderValue(":path", "/index.html"));
        table.put(6, new HeaderValue(":scheme", "http"));
        table.put(7, new HeaderValue(":scheme", "https"));
        table.put(8, new HeaderValue(":status", "200"));
        table.put(9, new HeaderValue(":status", "204"));
        table.put(10, new HeaderValue(":status", "206"));
        table.put(11, new HeaderValue(":status", "304"));
        table.put(12, new HeaderValue(":status", "400"));
        table.put(13, new HeaderValue(":status", "404"));
        table.put(14, new HeaderValue(":status", "500"));
        table.put(15, new HeaderValue("accept-charset", ""));
        table.put(16, new HeaderValue("accept-encoding", "gzip, deflate"));
        table.put(17, new HeaderValue("accept-language", ""));
        table.put(18, new HeaderValue("accept-ranges", ""));
        table.put(19, new HeaderValue("accept", ""));
        table.put(20, new HeaderValue("access-control-allow-origin", ""));
        table.put(21, new HeaderValue("age", ""));
        table.put(22, new HeaderValue("allow", ""));
        table.put(23, new HeaderValue("authorization", ""));
        table.put(24, new HeaderValue("cache-control", ""));
        table.put(25, new HeaderValue("content-disposition", ""));
        table.put(26, new HeaderValue("content-encoding", ""));
        table.put(27, new HeaderValue("content-language", ""));
        table.put(28, new HeaderValue("content-length", ""));
        table.put(29, new HeaderValue("content-location", ""));
        table.put(30, new HeaderValue("content-range", ""));
        table.put(31, new HeaderValue("content-type", ""));
        table.put(32, new HeaderValue("cookie", ""));
        table.put(33, new HeaderValue("date", ""));
        table.put(34, new HeaderValue("etag", ""));
        table.put(35, new HeaderValue("expect", ""));
        table.put(36, new HeaderValue("expires", ""));
        table.put(37, new HeaderValue("from", ""));
        table.put(38, new HeaderValue("host", ""));
        table.put(39, new HeaderValue("if-match", ""));
        table.put(40, new HeaderValue("if-modified-since", ""));
        table.put(41, new HeaderValue("if-none-match", ""));
        table.put(42, new HeaderValue("if-range", ""));
        table.put(43, new HeaderValue("if-unmodified-since", ""));
        table.put(44, new HeaderValue("last-modified", ""));
        table.put(45, new HeaderValue("link", ""));
        table.put(46, new HeaderValue("location", ""));
        table.put(47, new HeaderValue("max-forwards", ""));
        table.put(48, new HeaderValue("proxy-authenticate", ""));
        table.put(49, new HeaderValue("proxy-authorization", ""));
        table.put(50, new HeaderValue("range", ""));
        table.put(51, new HeaderValue("referer", ""));
        table.put(52, new HeaderValue("refresh", ""));
        table.put(53, new HeaderValue("retry-after", ""));
        table.put(54, new HeaderValue("server", ""));
        table.put(55, new HeaderValue("set-cookie", ""));
        table.put(56, new HeaderValue("strict-transport-security", ""));
        table.put(57, new HeaderValue("transfer-encoding", ""));
        table.put(58, new HeaderValue("user-agent", ""));
        table.put(59, new HeaderValue("vary", ""));
        table.put(60, new HeaderValue("via", ""));
        table.put(61, new HeaderValue("www-authenticate", ""));
        return table;
    }

    public static class Header {
        public final String name;
        public final String value;

        public Header(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return name + ": " + value;
        }
    }

    public static class HpackException extends Exception {
        public HpackException(String message) {
            super(message);
        }
    }

    private static class HuffmanDecoder {
        private static final int[] CODES = {
                0x1ff8, 0x7fffd8, 0xfffffe2, 0xfffffe3, 0xfffffe4, 0xfffffe5, 0xfffffe6, 0xfffffe7,
                0xfffffe8, 0xffffea, 0x3ffffffc, 0xfffffe9, 0xfffffea, 0x3ffffffd, 0xfffffeb, 0xfffffec,
                0xfffffed, 0xfffffee, 0xfffffef, 0xffffff0, 0xffffff1, 0xffffff2, 0x3ffffffe, 0xffffff3,
                0xffffff4, 0xffffff5, 0xffffff6, 0xffffff7, 0xffffff8, 0xffffff9, 0xffffffa, 0xffffffb,
                0x14, 0x3f8, 0x3f9, 0xffa, 0x1ff9, 0x15, 0xf8, 0x7fa, 0x3fa, 0x3fb, 0xf9, 0x7fb, 0xfa,
                0x16, 0x17, 0x18, 0x0, 0x1, 0x2, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x5c, 0xfb,
                0x7ffc, 0x20, 0xffb, 0x3fc, 0x1ffa, 0x21, 0x5d, 0x5e, 0x5f, 0x60, 0x61, 0x62, 0x63,
                0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70, 0x71,
                0x72, 0xfc, 0x73, 0xfd, 0x1ffb, 0x7fff0, 0x1ffc, 0x3ffc, 0x22, 0x7ffd, 0x3, 0x23, 0x4,
                0x24, 0x5, 0x25, 0x26, 0x27, 0x6, 0x74, 0x75, 0x28, 0x29, 0x2a, 0x7, 0x2b, 0x76, 0x2c,
                0x8, 0x9, 0x2d, 0x77, 0x78, 0x79, 0x7a, 0x7b, 0x7ffe, 0x7fc, 0x3ffd, 0x1ffd, 0xffffffc,
                0xfffe6, 0x3fffd2, 0xfffe7, 0xfffe8, 0x3fffd3, 0x3fffd4, 0x3fffd5, 0x7fffd9, 0x3fffd6,
                0x7fffda, 0x7fffdb, 0x7fffdc, 0x7fffdd, 0x7fffde, 0xffffeb, 0x7fffdf, 0xffffec, 0xffffed,
                0x3fffd7, 0x7fffe0, 0xffffee, 0x7fffe1, 0x7fffe2, 0x7fffe3, 0x7fffe4, 0x1fffdc, 0x3fffd8,
                0x7fffe5, 0x3fffd9, 0x7fffe6, 0x7fffe7, 0xffffef, 0x3fffda, 0x1fffdd, 0xfffe9, 0x3fffdb,
                0x3fffdc, 0x7fffe8, 0x7fffe9, 0x1fffde, 0x7fffea, 0x3fffdd, 0x3fffde, 0xfffff0, 0x1fffdf,
                0x3fffdf, 0x7fffeb, 0x7fffec, 0x1fffe0, 0x1fffe1, 0x3fffe0, 0x1fffe2, 0x7fffed, 0x3fffe1,
                0x7fffee, 0x7fffef, 0xfffea, 0x3fffe2, 0x3fffe3, 0x3fffe4, 0x7ffff0, 0x3fffe5, 0x3fffe6,
                0x7ffff1, 0x3ffffe0, 0x3ffffe1, 0xfffeb, 0x7fff1, 0x3fffe7, 0x7ffff2, 0x3fffe8, 0x1ffffec,
                0x3ffffe2, 0x3ffffe3, 0x3ffffe4, 0x7ffffde, 0x7ffffdf, 0x3ffffe5, 0xfffff1, 0x1ffffed,
                0x7fff2, 0x1fffe3, 0x3ffffe6, 0x7ffffe0, 0x7ffffe1, 0x3ffffe7, 0x7ffffe2, 0xfffff2,
                0x1fffe4, 0x1fffe5, 0x3ffffe8, 0x3ffffe9, 0xffffffd, 0x7ffffe3, 0x7ffffe4, 0x7ffffe5,
                0xfffec, 0xfffff3, 0xfffed, 0x1fffe6, 0x3fffe9, 0x1fffe7, 0x1fffe8, 0x7ffff3, 0x3fffea,
                0x3fffeb, 0x1ffffee, 0x1ffffef, 0xfffff4, 0xfffff5, 0x3ffffea, 0x7ffff4, 0x3ffffeb,
                0x7ffffe6, 0x3ffffec, 0x3ffffed, 0x7ffffe7, 0x7ffffe8, 0x7ffffe9, 0x7ffffea, 0x7ffffeb,
                0xffffffe, 0x7ffffec, 0x7ffffed, 0x7ffffee, 0x7ffffef, 0x7fffff0, 0x3ffffee
        };

        private static final byte[] LENGTHS = {
                13, 23, 28, 28, 28, 28, 28, 28, 28, 24, 30, 28, 28, 30, 28, 28, 28, 28, 28, 28, 28,
                28, 30, 28, 28, 28, 28, 28, 28, 28, 28, 28, 6, 10, 10, 12, 13, 6, 8, 11, 10, 10, 8,
                11, 8, 6, 6, 6, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 7, 8, 15, 6, 12, 10, 13, 6, 7, 7, 7,
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 7, 8, 13, 19, 13, 14,
                6, 15, 5, 6, 5, 6, 5, 6, 6, 6, 5, 7, 7, 6, 6, 6, 5, 6, 7, 6, 5, 5, 6, 7, 7, 7, 7,
                7, 15, 11, 14, 13, 28, 20, 22, 20, 20, 22, 22, 22, 23, 22, 23, 23, 23, 23, 23, 24,
                23, 24, 24, 22, 23, 24, 23, 23, 23, 23, 21, 22, 23, 22, 23, 23, 24, 22, 21, 20, 22,
                22, 23, 23, 21, 23, 22, 22, 24, 21, 22, 23, 23, 21, 21, 22, 21, 23, 22, 23, 23, 20,
                22, 22, 22, 23, 22, 22, 23, 26, 26, 20, 19, 22, 23, 22, 25, 26, 26, 26, 27, 27, 26,
                24, 25, 19, 21, 26, 27, 27, 26, 27, 24, 21, 21, 26, 26, 28, 27, 27, 27, 20, 24, 20,
                21, 22, 21, 21, 23, 22, 22, 25, 25, 24, 24, 26, 23, 26, 27, 26, 26, 27, 27, 27, 27,
                27, 28, 27, 27, 27, 27, 27, 26
        };

        public String decode(byte[] buf) throws HpackException {
            StringBuilder result = new StringBuilder();
            long current = 0;
            int bits = 0;

            for (byte b : buf) {
                current = (current << 8) | (b & 0xFF);
                bits += 8;
                while (bits >= 8) {
                    int found = findCodeIndex((int) (current >>> (bits - 8)));
                    if (found == -1) {
                        throw new HpackException("Invalid Huffman code");
                    }
                    if (found == 256) {
                        // EOS
                        return result.toString();
                    }
                    result.append((char) found);
                    bits -= LENGTHS[found];
                }
            }

            // Check for EOS padding
            if (bits > 0) {
                int found = findCodeIndex((int) (current << (8 - bits)));
                if (found != 256 && found != -1) {
                    throw new HpackException("Invalid Huffman code");
                }
            }

            return result.toString();
        }

        private int findCodeIndex(int code) {
            for (int i = 0; i < CODES.length; i++) {
                if (code >>> (8 - LENGTHS[i]) == CODES[i] >>> (32 - LENGTHS[i])) {
                    return i;
                }
            }
            return -1;
        }
    }
}