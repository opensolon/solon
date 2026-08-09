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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public final class Decoder {

    private static final AtomicLong DECODERS_IDS = new AtomicLong();

    /* An immutable list of states */
    private static final List<State> states;

    static {
        // To be able to do a quick lookup, each of 256 possibilities are mapped
        // to corresponding states.
        //
        // We can safely do this since patterns 1, 01, 001, 0001, 0000 are
        // Huffman prefixes and therefore are inherently not ambiguous.
        //
        // I do it mainly for better debugging (to not go each time step by step
        // through if...else tree). As for performance win for the decoding, I
        // believe is negligible.
        State[] s = new State[256];
        for (int i = 0; i < s.length; i++) {
            if ((i & 0b1000_0000) == 0b1000_0000) {
                s[i] = State.INDEXED;
            } else if ((i & 0b1100_0000) == 0b0100_0000) {
                s[i] = State.LITERAL_WITH_INDEXING;
            } else if ((i & 0b1110_0000) == 0b0010_0000) {
                s[i] = State.SIZE_UPDATE;
            } else if ((i & 0b1111_0000) == 0b0001_0000) {
                s[i] = State.LITERAL_NEVER_INDEXED;
            } else if ((i & 0b1111_0000) == 0b0000_0000) {
                s[i] = State.LITERAL;
            } else {
                throw new InternalError(String.valueOf(i));
            }
        }
        states = Arrays.asList(s);
    }

    private final long id;
    private final SimpleHeaderTable table;

    private State state = State.READY;
    private final IntegerReader integerReader;
    private final StringReader stringReader;
    private final StringBuilder name;
    private final StringBuilder value;
    private int intValue;
    private boolean firstValueRead;
    private boolean firstValueIndex;
    private boolean nameHuffmanEncoded;
    private boolean valueHuffmanEncoded;
    private int capacity;

    /**
     * Constructs a {@code Decoder} with the specified initial capacity of the
     * header table.
     *
     * <p> The value has to be agreed between decoder and encoder out-of-band,
     * e.g. by a protocol that uses HPACK
     * (see <a href="https://tools.ietf.org/html/rfc7541#section-4.2">4.2. Maximum Table Size</a>).
     *
     * @param capacity a non-negative integer
     * @throws IllegalArgumentException if capacity is negative
     */
    public Decoder(int capacity) {
        id = DECODERS_IDS.incrementAndGet();

        setMaxCapacity0(capacity);
        table = new SimpleHeaderTable(capacity);
        integerReader = new IntegerReader();
        stringReader = new StringReader();
        name = new StringBuilder(512);
        value = new StringBuilder(1024);
    }

    /**
     * Sets a maximum capacity of the header table.
     *
     * <p> The value has to be agreed between decoder and encoder out-of-band,
     * e.g. by a protocol that uses HPACK
     * (see <a href="https://tools.ietf.org/html/rfc7541#section-4.2">4.2. Maximum Table Size</a>).
     *
     * @param capacity a non-negative integer
     * @throws IllegalArgumentException if capacity is negative
     */
    public void setMaxCapacity(int capacity) {
        setMaxCapacity0(capacity);
    }

    private void setMaxCapacity0(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity >= 0: " + capacity);
        }
        // FIXME: await capacity update if less than what was prior to it
        this.capacity = capacity;
    }

    /**
     * Decodes a header block from the given buffer to the given callback.
     *
     * <p> Suppose a header block is represented by a sequence of
     * {@code ByteBuffer}s in the form of {@code Iterator<ByteBuffer>}. And the
     * consumer of decoded headers is represented by the callback. Then to
     * decode the header block, the following approach might be used:
     *
     * <pre>{@code
     * while (buffers.hasNext()) {
     *     ByteBuffer input = buffers.next();
     *     decoder.decode(input, callback, !buffers.hasNext());
     * }
     * }</pre>
     *
     * <p> The decoder reads as much as possible of the header block from the
     * given buffer, starting at the buffer's position, and increments its
     * position to reflect the bytes read. The buffer's mark and limit will not
     * be modified.
     *
     * <p> Once the method is invoked with {@code endOfHeaderBlock == true}, the
     * current header block is deemed ended, and inconsistencies, if any, are
     * reported immediately by throwing an {@code IOException}.
     *
     * <p> Each callback method is called only after the implementation has
     * processed the corresponding bytes. If the bytes revealed a decoding
     * error, the callback method is not called.
     *
     * <p> In addition to exceptions thrown directly by the method, any
     * exceptions thrown from the {@code callback} will bubble up.
     *
     * @param headerBlock      the chunk of the header block, may be empty
     * @param endOfHeaderBlock true if the chunk is the final (or the only one) in the sequence
     * @param consumer         the callback
     * @throws IOException          in case of a decoding error
     * @throws NullPointerException if either headerBlock or consumer are null
     * @apiNote The method asks for {@code endOfHeaderBlock} flag instead of
     * returning it for two reasons. The first one is that the user of the
     * decoder always knows which chunk is the last. The second one is to throw
     * the most detailed exception possible, which might be useful for
     * diagnosing issues.
     * @implNote This implementation is not atomic in respect to decoding
     * errors. In other words, if the decoding operation has thrown a decoding
     * error, the decoder is no longer usable.
     */
    public void decode(ByteBuffer headerBlock,
                       boolean endOfHeaderBlock,
                       DecodingCallback consumer) throws IOException {
        requireNonNull(headerBlock, "headerBlock");
        requireNonNull(consumer, "consumer");
        while (headerBlock.hasRemaining()) {
            proceed(headerBlock, consumer);
        }
        if (endOfHeaderBlock && state != State.READY) {
            throw new IOException("Unexpected end of header block");
        }
    }

    private void proceed(ByteBuffer input, DecodingCallback action)
            throws IOException {
        switch (state) {
            case READY:
                resumeReady(input);
                break;
            case INDEXED:
                resumeIndexed(input, action);
                break;
            case LITERAL:
                resumeLiteral(input, action);
                break;
            case LITERAL_WITH_INDEXING:
                resumeLiteralWithIndexing(input, action);
                break;
            case LITERAL_NEVER_INDEXED:
                resumeLiteralNeverIndexed(input, action);
                break;
            case SIZE_UPDATE:
                resumeSizeUpdate(input, action);
                break;

            default:
                throw new InternalError("Unexpected decoder state: " + state);
        }
    }

    private void resumeReady(ByteBuffer input) {
        int b = input.get(input.position()) & 0xff; // absolute read
        State s = states.get(b);
        switch (s) {
            case INDEXED:
                integerReader.configure(7);
                state = State.INDEXED;
                firstValueIndex = true;
                break;
            case LITERAL:
                state = State.LITERAL;
                firstValueIndex = (b & 0b0000_1111) != 0;
                if (firstValueIndex) {
                    integerReader.configure(4);
                }
                break;
            case LITERAL_WITH_INDEXING:
                state = State.LITERAL_WITH_INDEXING;
                firstValueIndex = (b & 0b0011_1111) != 0;
                if (firstValueIndex) {
                    integerReader.configure(6);
                }
                break;
            case LITERAL_NEVER_INDEXED:
                state = State.LITERAL_NEVER_INDEXED;
                firstValueIndex = (b & 0b0000_1111) != 0;
                if (firstValueIndex) {
                    integerReader.configure(4);
                }
                break;
            case SIZE_UPDATE:
                integerReader.configure(5);
                state = State.SIZE_UPDATE;
                firstValueIndex = true;
                break;
            default:
                throw new InternalError(String.valueOf(s));
        }
        if (!firstValueIndex) {
            input.get(); // advance, next stop: "String Literal"
        }
    }

    //              0   1   2   3   4   5   6   7
    //            +---+---+---+---+---+---+---+---+
    //            | 1 |        Index (7+)         |
    //            +---+---------------------------+
    //
    private void resumeIndexed(ByteBuffer input, DecodingCallback action)
            throws IOException {
        if (!integerReader.read(input)) {
            return;
        }
        intValue = integerReader.get();
        integerReader.reset();
        try {
            SimpleHeaderTable.HeaderField f = getHeaderFieldAt(intValue);
            action.onIndexed(intValue, f.name, f.value);
        } finally {
            state = State.READY;
        }
    }

    private SimpleHeaderTable.HeaderField getHeaderFieldAt(int index)
            throws IOException {
        SimpleHeaderTable.HeaderField f;
        try {
            f = table.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("header fields table index", e);
        }
        return f;
    }

    //              0   1   2   3   4   5   6   7
    //            +---+---+---+---+---+---+---+---+
    //            | 0 | 0 | 0 | 0 |  Index (4+)   |
    //            +---+---+-----------------------+
    //            | H |     Value Length (7+)     |
    //            +---+---------------------------+
    //            | Value String (Length octets)  |
    //            +-------------------------------+
    //
    //              0   1   2   3   4   5   6   7
    //            +---+---+---+---+---+---+---+---+
    //            | 0 | 0 | 0 | 0 |       0       |
    //            +---+---+-----------------------+
    //            | H |     Name Length (7+)      |
    //            +---+---------------------------+
    //            |  Name String (Length octets)  |
    //            +---+---------------------------+
    //            | H |     Value Length (7+)     |
    //            +---+---------------------------+
    //            | Value String (Length octets)  |
    //            +-------------------------------+
    //
    private void resumeLiteral(ByteBuffer input, DecodingCallback action)
            throws IOException {
        if (!completeReading(input)) {
            return;
        }
        try {
            if (firstValueIndex) {
                SimpleHeaderTable.HeaderField f = getHeaderFieldAt(intValue);
                action.onLiteral(intValue, f.name, value, valueHuffmanEncoded);
            } else {
                action.onLiteral(name, nameHuffmanEncoded, value, valueHuffmanEncoded);
            }
        } finally {
            cleanUpAfterReading();
        }
    }

    //
    //              0   1   2   3   4   5   6   7
    //            +---+---+---+---+---+---+---+---+
    //            | 0 | 1 |      Index (6+)       |
    //            +---+---+-----------------------+
    //            | H |     Value Length (7+)     |
    //            +---+---------------------------+
    //            | Value String (Length octets)  |
    //            +-------------------------------+
    //
    //              0   1   2   3   4   5   6   7
    //            +---+---+---+---+---+---+---+---+
    //            | 0 | 1 |           0           |
    //            +---+---+-----------------------+
    //            | H |     Name Length (7+)      |
    //            +---+---------------------------+
    //            |  Name String (Length octets)  |
    //            +---+---------------------------+
    //            | H |     Value Length (7+)     |
    //            +---+---------------------------+
    //            | Value String (Length octets)  |
    //            +-------------------------------+
    //
    private void resumeLiteralWithIndexing(ByteBuffer input,
                                           DecodingCallback action)
            throws IOException {
        if (!completeReading(input)) {
            return;
        }
        try {
            //
            // 1. (name, value) will be stored in the table as strings
            // 2. Most likely the callback will also create strings from them
            // ------------------------------------------------------------------------
            //    Let's create those string beforehand (and only once!) to benefit everyone
            //
            String n;
            String v = value.toString();
            if (firstValueIndex) {
                SimpleHeaderTable.HeaderField f = getHeaderFieldAt(intValue);
                n = f.name;
                action.onLiteralWithIndexing(intValue, n, v, valueHuffmanEncoded);
            } else {
                n = name.toString();
                action.onLiteralWithIndexing(n, nameHuffmanEncoded, v, valueHuffmanEncoded);
            }
            table.put(n, v);
        } finally {
            cleanUpAfterReading();
        }
    }

    //              0   1   2   3   4   5   6   7
    //            +---+---+---+---+---+---+---+---+
    //            | 0 | 0 | 0 | 1 |  Index (4+)   |
    //            +---+---+-----------------------+
    //            | H |     Value Length (7+)     |
    //            +---+---------------------------+
    //            | Value String (Length octets)  |
    //            +-------------------------------+
    //
    //              0   1   2   3   4   5   6   7
    //            +---+---+---+---+---+---+---+---+
    //            | 0 | 0 | 0 | 1 |       0       |
    //            +---+---+-----------------------+
    //            | H |     Name Length (7+)      |
    //            +---+---------------------------+
    //            |  Name String (Length octets)  |
    //            +---+---------------------------+
    //            | H |     Value Length (7+)     |
    //            +---+---------------------------+
    //            | Value String (Length octets)  |
    //            +-------------------------------+
    //
    private void resumeLiteralNeverIndexed(ByteBuffer input,
                                           DecodingCallback action)
            throws IOException {
        if (!completeReading(input)) {
            return;
        }
        try {
            if (firstValueIndex) {
                SimpleHeaderTable.HeaderField f = getHeaderFieldAt(intValue);
                action.onLiteralNeverIndexed(intValue, f.name, value, valueHuffmanEncoded);
            } else {
                action.onLiteralNeverIndexed(name, nameHuffmanEncoded, value, valueHuffmanEncoded);
            }
        } finally {
            cleanUpAfterReading();
        }
    }

    //              0   1   2   3   4   5   6   7
    //            +---+---+---+---+---+---+---+---+
    //            | 0 | 0 | 1 |   Max size (5+)   |
    //            +---+---------------------------+
    //
    private void resumeSizeUpdate(ByteBuffer input,
                                  DecodingCallback action) throws IOException {
        if (!integerReader.read(input)) {
            return;
        }
        intValue = integerReader.get();
        assert intValue >= 0;
        if (intValue > capacity) {
            throw new IOException(
                    format("Received capacity exceeds expected: capacity=%s, expected=%s",
                            intValue, capacity));
        }
        integerReader.reset();
        try {
            action.onSizeUpdate(intValue);
            table.setMaxSize(intValue);
        } finally {
            state = State.READY;
        }
    }

    private boolean completeReading(ByteBuffer input) throws IOException {
        if (!firstValueRead) {
            if (firstValueIndex) {
                if (!integerReader.read(input)) {
                    return false;
                }
                intValue = integerReader.get();
                integerReader.reset();
            } else {
                if (!stringReader.read(input, name)) {
                    return false;
                }
                nameHuffmanEncoded = stringReader.isHuffmanEncoded();
                stringReader.reset();
            }
            firstValueRead = true;
            return false;
        } else {
            if (!stringReader.read(input, value)) {
                return false;
            }
        }
        valueHuffmanEncoded = stringReader.isHuffmanEncoded();
        stringReader.reset();
        return true;
    }

    private void cleanUpAfterReading() {
        name.setLength(0);
        value.setLength(0);
        firstValueRead = false;
        state = State.READY;
    }

    private enum State {
        READY,
        INDEXED,
        LITERAL_NEVER_INDEXED,
        LITERAL,
        LITERAL_WITH_INDEXING,
        SIZE_UPDATE
    }

    SimpleHeaderTable getTable() {
        return table;
    }
}
