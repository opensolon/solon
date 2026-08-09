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
import java.util.*;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class HpackEncoder {
    private static final int STATIC_TABLE_LENGTH = 61;
    private static final Map<String, Integer> STATIC_TABLE_MAP = createStaticTableMap();
    private static final HuffmanEncoder HUFFMAN_ENCODER = new HuffmanEncoder();

    private final List<HeaderValue> dynamicTable;
    private final Map<String, Integer> dynamicTableMap;
    private int dynamicTableSize;
    private int maxDynamicTableSize;

    public HpackEncoder(int maxDynamicTableSize) {
        this.dynamicTable = new ArrayList<>();
        this.dynamicTableMap = new HashMap<>();
        this.dynamicTableSize = 0;
        this.maxDynamicTableSize = maxDynamicTableSize;
    }

    public ByteBuffer encode(Collection<HeaderValue> headers) throws HpackException {
        ByteBuffer buffer = ByteBuffer.allocate(4096); // Initial size, may grow

        for (HeaderValue header : headers) {
            encodeHeader(buffer, header);
        }

        buffer.flip();
        return buffer;
    }

    private void encodeHeader(ByteBuffer buffer, HeaderValue header) throws HpackException {
        Integer staticIndex = STATIC_TABLE_MAP.get(header.getName() + ": " + header.getValue());
        if (staticIndex != null) {
            encodeInteger(buffer, 7, staticIndex);
            buffer.put((byte) (0x80 | buffer.get(buffer.position() - 1)));
            return;
        }

        Integer dynamicIndex = dynamicTableMap.get(header.getName() + ": " + header.getValue());
        if (dynamicIndex != null) {
            encodeInteger(buffer, 7, dynamicIndex + STATIC_TABLE_LENGTH);
            buffer.put((byte) (0x80 | buffer.get(buffer.position() - 1)));
            return;
        }

        staticIndex = STATIC_TABLE_MAP.get(header.getName() + ": ");
        if (staticIndex != null) {
            encodeInteger(buffer, 6, staticIndex);
            buffer.put((byte) (0x40 | buffer.get(buffer.position() - 1)));
            encodeString(buffer, header.getValue());
        } else {
            buffer.put((byte) 0x40);
            encodeString(buffer, header.getName());
            encodeString(buffer, header.getValue());
        }

        addToDynamicTable(header);
    }

    private void addToDynamicTable(HeaderValue header) {
        int headerSize = header.getName().length() + header.getValue().length() + 32;
        while (dynamicTableSize + headerSize > maxDynamicTableSize && !dynamicTable.isEmpty()) {
            HeaderValue removed = dynamicTable.remove(dynamicTable.size() - 1);
            dynamicTableSize -= removed.getName().length() + removed.getValue().length() + 32;
            dynamicTableMap.remove(removed.getName() + ": " + removed.getValue());
        }
        if (headerSize <= maxDynamicTableSize) {
            dynamicTable.add(0, header);
            dynamicTableSize += headerSize;
            dynamicTableMap.put(header.getName() + ": " + header.getValue(), 1);
            for (Map.Entry<String, Integer> entry : dynamicTableMap.entrySet()) {
                entry.setValue(entry.getValue() + 1);
            }
        }
    }

    private void encodeInteger(ByteBuffer buffer, int n, int i) throws HpackException {
        int limit = (1 << n) - 1;
        if (i < limit) {
            buffer.put((byte) i);
        } else {
            buffer.put((byte) limit);
            i -= limit;
            while (i >= 128) {
                buffer.put((byte) (0x80 | (i & 0x7F)));
                i >>>= 7;
            }
            buffer.put((byte) i);
        }
    }

    private void encodeString(ByteBuffer buffer, String s) throws HpackException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        byte[] huffmanEncoded = HUFFMAN_ENCODER.encode(bytes);

        if (huffmanEncoded.length < bytes.length) {
            encodeInteger(buffer, 7, huffmanEncoded.length);
            buffer.put((byte) (0x80 | buffer.get(buffer.position() - 1)));
            buffer.put(huffmanEncoded);
        } else {
            encodeInteger(buffer, 7, bytes.length);
            buffer.put(bytes);
        }
    }

    public void setMaxDynamicTableSize(int newMaxDynamicTableSize) throws HpackException {
        if (newMaxDynamicTableSize < 0) {
            throw new HpackException("Invalid dynamic table size: " + newMaxDynamicTableSize);
        }
        maxDynamicTableSize = newMaxDynamicTableSize;
        while (dynamicTableSize > maxDynamicTableSize && !dynamicTable.isEmpty()) {
            HeaderValue removed = dynamicTable.remove(dynamicTable.size() - 1);
            dynamicTableSize -= removed.getName().length() + removed.getValue().length() + 32;
            dynamicTableMap.remove(removed.getName() + ": " + removed.getValue());
        }
    }

    private static Map<String, Integer> createStaticTableMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put(":authority: ", 1);
        map.put(":method: GET", 2);
        map.put(":method: POST", 3);
        map.put(":path: /", 4);
        map.put(":path: /index.html", 5);
        map.put(":scheme: http", 6);
        map.put(":scheme: https", 7);
        map.put(":status: 200", 8);
        map.put(":status: 204", 9);
        map.put(":status: 206", 10);
        map.put(":status: 304", 11);
        map.put(":status: 400", 12);
        map.put(":status: 404", 13);
        map.put(":status: 500", 14);
        map.put("accept-charset: ", 15);
        map.put("accept-encoding: gzip, deflate", 16);
        map.put("accept-language: ", 17);
        map.put("accept-ranges: ", 18);
        map.put("accept: ", 19);
        map.put("access-control-allow-origin: ", 20);
        map.put("age: ", 21);
        map.put("allow: ", 22);
        map.put("authorization: ", 23);
        map.put("cache-control: ", 24);
        map.put("content-disposition: ", 25);
        map.put("content-encoding: ", 26);
        map.put("content-language: ", 27);
        map.put("content-length: ", 28);
        map.put("content-location: ", 29);
        map.put("content-range: ", 30);
        map.put("content-type: ", 31);
        map.put("cookie: ", 32);
        map.put("date: ", 33);
        map.put("etag: ", 34);
        map.put("expect: ", 35);
        map.put("expires: ", 36);
        map.put("from: ", 37);
        map.put("host: ", 38);
        map.put("if-match: ", 39);
        map.put("if-modified-since: ", 40);
        map.put("if-none-match: ", 41);
        map.put("if-range: ", 42);
        map.put("if-unmodified-since: ", 43);
        map.put("last-modified: ", 44);
        map.put("link: ", 45);
        map.put("location: ", 46);
        map.put("max-forwards: ", 47);
        map.put("proxy-authenticate: ", 48);
        map.put("proxy-authorization: ", 49);
        map.put("range: ", 50);
        map.put("referer: ", 51);
        map.put("refresh: ", 52);
        map.put("retry-after: ", 53);
        map.put("server: ", 54);
        map.put("set-cookie: ", 55);
        map.put("strict-transport-security: ", 56);
        map.put("transfer-encoding: ", 57);
        map.put("user-agent: ", 58);
        map.put("vary: ", 59);
        map.put("via: ", 60);
        map.put("www-authenticate: ", 61);
        return map;
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

    private static class HuffmanEncoder {
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

        public byte[] encode(byte[] data) {
            int bitLength = 0;
            for (byte b : data) {
                bitLength += LENGTHS[b & 0xFF];
            }

            byte[] result = new byte[(bitLength + 7) / 8];
            int bitPosition = 0;

            for (byte b : data) {
                int index = b & 0xFF;
                int code = CODES[index];
                int codeLength = LENGTHS[index];
                bitPosition = writeCode(result, bitPosition, code, codeLength);
            }

            // Padding
            if (bitPosition % 8 != 0) {
                int paddingLength = 8 - (bitPosition % 8);
                int paddingCode = (1 << paddingLength) - 1;
                writeCode(result, bitPosition, paddingCode, paddingLength);
            }

            return result;
        }

        private int writeCode(byte[] result, int bitPosition, int code, int codeLength) {
            int bytePosition = bitPosition / 8;
            int bitOffset = bitPosition % 8;

            code <<= 32 - codeLength;
            while (codeLength > 0) {
                int bitsToWrite = Math.min(8 - bitOffset, codeLength);
                int mask = 0xFF >>> bitOffset;
                result[bytePosition] &= ~mask;
                result[bytePosition] |= ((code >>> 24) & mask);

                code <<= bitsToWrite;
                codeLength -= bitsToWrite;
                bitOffset = (bitOffset + bitsToWrite) % 8;
                if (bitOffset == 0) {
                    bytePosition++;
                }
            }

            return bytePosition * 8 + bitOffset;
        }
    }
}