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

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public final class Huffman {

    public interface Reader {

        void read(ByteBuffer source,
                  Appendable destination,
                  boolean isLast) throws IOException;

        /**
         * Brings this reader to the state it had upon construction.
         */
        void reset();
    }

    public interface Writer {

        Writer from(CharSequence input, int start, int end);

        boolean write(ByteBuffer destination);

        /**
         * Brings this writer to the state it had upon construction.
         *
         * @return this writer
         */
        Writer reset();

        /**
         * Calculates the number of bytes required to represent a subsequence of
         * the given {@code CharSequence} using the Huffman coding.
         *
         * @param value characters
         * @param start the start index, inclusive
         * @param end   the end index, exclusive
         * @return number of bytes
         * @throws NullPointerException      if the value is null
         * @throws IndexOutOfBoundsException if any invocation of {@code value.charAt(i)}, where
         *                                   {@code start <= i < end}, throws an IndexOutOfBoundsException
         */
        int lengthOf(CharSequence value, int start, int end);

        default int lengthOf(CharSequence value) {
            return lengthOf(value, 0, value.length());
        }
    }
}
