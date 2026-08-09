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
import java.nio.ReadOnlyBufferException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class Encoder {

    private static final AtomicLong ENCODERS_IDS = new AtomicLong();

    /* Used to calculate the number of bytes required for Huffman encoding */
    private final QuickHuffman.Writer huffmanWriter = new QuickHuffman.Writer();

    private final long id;
    private final IndexedWriter indexedWriter = new IndexedWriter();
    private final LiteralWriter literalWriter = new LiteralWriter();
    private final LiteralNeverIndexedWriter literalNeverIndexedWriter
            = new LiteralNeverIndexedWriter();
    private final LiteralWithIndexingWriter literalWithIndexingWriter
            = new LiteralWithIndexingWriter();
    private final SizeUpdateWriter sizeUpdateWriter = new SizeUpdateWriter();
    private final BulkSizeUpdateWriter bulkSizeUpdateWriter
            = new BulkSizeUpdateWriter();

    private BinaryRepresentationWriter writer;
    // The default implementation of Encoder does not use dynamic region of the
    // HeaderTable. Thus the performance profile should be similar to that of
    // SimpleHeaderTable.
    private final HeaderTable headerTable;

    private boolean encoding;

    private int maxCapacity;
    private int currCapacity;
    private int lastCapacity;
    private long minCapacity;
    private boolean capacityUpdate;
    private boolean configuredCapacityUpdate;

    /**
     * Constructs an {@code Encoder} with the specified maximum capacity of the
     * header table.
     *
     * <p> The value has to be agreed between decoder and encoder out-of-band,
     * e.g. by a protocol that uses HPACK
     * (see <a href="https://tools.ietf.org/html/rfc7541#section-4.2">4.2. Maximum Table Size</a>).
     *
     * @param maxCapacity a non-negative integer
     * @throws IllegalArgumentException if maxCapacity is negative
     */
    public Encoder(int maxCapacity) {
        id = ENCODERS_IDS.incrementAndGet();

        if (maxCapacity < 0) {
            throw new IllegalArgumentException(
                    "maxCapacity >= 0: " + maxCapacity);
        }
        // Initial maximum capacity update mechanics
        minCapacity = Long.MAX_VALUE;
        currCapacity = -1;
        setMaxCapacity0(maxCapacity);
        headerTable = new HeaderTable(lastCapacity);
    }

    /**
     * Sets up the given header {@code (name, value)}.
     *
     * <p> Fixates {@code name} and {@code value} for the duration of encoding.
     *
     * @param name  the name
     * @param value the value
     * @throws NullPointerException  if any of the arguments are {@code null}
     * @throws IllegalStateException if the encoder hasn't fully encoded the previous header, or
     *                               hasn't yet started to encode it
     * @see #header(CharSequence, CharSequence, boolean)
     */
    public void header(CharSequence name, CharSequence value)
            throws IllegalStateException {
        header(name, value, false);
    }

    /**
     * Sets up the given header {@code (name, value)} with possibly sensitive
     * value.
     *
     * <p> If the {@code value} is sensitive (think security, secrecy, etc.)
     * this encoder will compress it using a special representation
     * (see <a href="https://tools.ietf.org/html/rfc7541#section-6.2.3">6.2.3.  Literal Header Field Never Indexed</a>).
     *
     * <p> Fixates {@code name} and {@code value} for the duration of encoding.
     *
     * @param name      the name
     * @param value     the value
     * @param sensitive whether or not the value is sensitive
     * @throws NullPointerException  if any of the arguments are {@code null}
     * @throws IllegalStateException if the encoder hasn't fully encoded the previous header, or
     *                               hasn't yet started to encode it
     * @see #header(CharSequence, CharSequence)
     * @see DecodingCallback#onDecoded(CharSequence, CharSequence, boolean)
     */
    public void header(CharSequence name,
                       CharSequence value,
                       boolean sensitive) throws IllegalStateException {
        // Arguably a good balance between complexity of implementation and
        // efficiency of encoding
        requireNonNull(name, "name");
        requireNonNull(value, "value");
        HeaderTable t = getHeaderTable();
        int index = t.indexOf(name, value);
        if (index > 0) {
            indexed(index);
        } else {
            boolean huffmanValue = isHuffmanBetterFor(value);
            if (index < 0) {
                if (sensitive) {
                    literalNeverIndexed(-index, value, huffmanValue);
                } else {
                    literal(-index, value, huffmanValue);
                }
            } else {
                boolean huffmanName = isHuffmanBetterFor(name);
                if (sensitive) {
                    literalNeverIndexed(name, huffmanName, value, huffmanValue);
                } else {
                    literal(name, huffmanName, value, huffmanValue);
                }
            }
        }
    }

    private boolean isHuffmanBetterFor(CharSequence value) {
        // prefer Huffman encoding only if it is strictly smaller than Latin-1
        return huffmanWriter.lengthOf(value) < value.length();
    }

    /**
     * Sets a maximum capacity of the header table.
     *
     * <p> The value has to be agreed between decoder and encoder out-of-band,
     * e.g. by a protocol that uses HPACK
     * (see <a href="https://tools.ietf.org/html/rfc7541#section-4.2">4.2. Maximum Table Size</a>).
     *
     * <p> May be called any number of times after or before a complete header
     * has been encoded.
     *
     * <p> If the encoder decides to change the actual capacity, an update will
     * be encoded before a new encoding operation starts.
     *
     * @param capacity a non-negative integer
     * @throws IllegalArgumentException if capacity is negative
     * @throws IllegalStateException    if the encoder hasn't fully encoded the previous header, or
     *                                  hasn't yet started to encode it
     */
    public void setMaxCapacity(int capacity) {
        setMaxCapacity0(capacity);
    }

    private void setMaxCapacity0(int capacity) {
        checkEncoding();
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity >= 0: " + capacity);
        }
        int calculated = calculateCapacity(capacity);
        if (calculated < 0 || calculated > capacity) {
            throw new IllegalArgumentException(
                    format("0 <= calculated <= capacity: calculated=%s, capacity=%s",
                            calculated, capacity));
        }
        capacityUpdate = true;
        // maxCapacity needs to be updated unconditionally, so the encoder
        // always has the newest one (in case it decides to update it later
        // unsolicitedly)
        // Suppose maxCapacity = 4096, and the encoder has decided to use only
        // 2048. It later can choose anything else from the region [0, 4096].
        maxCapacity = capacity;
        lastCapacity = calculated;
        minCapacity = Math.min(minCapacity, lastCapacity);
    }

    /**
     * Calculates actual capacity to be used by this encoder in response to
     * a request to update maximum table size.
     *
     * <p> Default implementation does not add anything to the headers table,
     * hence this method returns {@code 0}.
     *
     * <p> It is an error to return a value {@code c}, where {@code c < 0} or
     * {@code c > maxCapacity}.
     *
     * @param maxCapacity upper bound
     * @return actual capacity
     */
    protected int calculateCapacity(int maxCapacity) {
        return 0;
    }

    /**
     * Encodes the {@linkplain #header(CharSequence, CharSequence) set up}
     * header into the given buffer.
     *
     * <p> The encoder writes as much as possible of the header's binary
     * representation into the given buffer, starting at the buffer's position,
     * and increments its position to reflect the bytes written. The buffer's
     * mark and limit will not be modified.
     *
     * <p> Once the method has returned {@code true}, the current header is
     * deemed encoded. A new header may be set up.
     *
     * @param headerBlock the buffer to encode the header into, may be empty
     * @return {@code true} if the current header has been fully encoded,
     * {@code false} otherwise
     * @throws NullPointerException    if the buffer is {@code null}
     * @throws ReadOnlyBufferException if this buffer is read-only
     * @throws IllegalStateException   if there is no set up header
     */
    public final boolean encode(ByteBuffer headerBlock) {
        if (!encoding) {
            throw new IllegalStateException("A header hasn't been set up");
        }
        if (!prependWithCapacityUpdate(headerBlock)) { // TODO: log
            return false;
        }
        boolean done = writer.write(headerTable, headerBlock);
        if (done) {
            writer.reset(); // FIXME: WHY?
            encoding = false;
        }
        return done;
    }

    private boolean prependWithCapacityUpdate(ByteBuffer headerBlock) {
        if (capacityUpdate) {
            if (!configuredCapacityUpdate) {
                List<Integer> sizes = new LinkedList<>();
                if (minCapacity < currCapacity) {
                    sizes.add((int) minCapacity);
                    if (minCapacity != lastCapacity) {
                        sizes.add(lastCapacity);
                    }
                } else if (lastCapacity != currCapacity) {
                    sizes.add(lastCapacity);
                }
                bulkSizeUpdateWriter.maxHeaderTableSizes(sizes);
                configuredCapacityUpdate = true;
            }
            boolean done = bulkSizeUpdateWriter.write(headerTable, headerBlock);
            if (done) {
                minCapacity = lastCapacity;
                currCapacity = lastCapacity;
                bulkSizeUpdateWriter.reset();
                capacityUpdate = false;
                configuredCapacityUpdate = false;
            }
            return done;
        }
        return true;
    }

    protected final void indexed(int index) throws IndexOutOfBoundsException {
        checkEncoding();
        encoding = true;
        writer = indexedWriter.index(index);
    }

    protected final void literal(int index,
                                 CharSequence value,
                                 boolean useHuffman)
            throws IndexOutOfBoundsException {
        checkEncoding();
        encoding = true;
        writer = literalWriter
                .index(index).value(value, useHuffman);
    }

    protected final void literal(CharSequence name,
                                 boolean nameHuffman,
                                 CharSequence value,
                                 boolean valueHuffman) {
        checkEncoding();
        encoding = true;
        writer = literalWriter
                .name(name, nameHuffman).value(value, valueHuffman);
    }

    protected final void literalNeverIndexed(int index,
                                             CharSequence value,
                                             boolean valueHuffman)
            throws IndexOutOfBoundsException {
        checkEncoding();
        encoding = true;
        writer = literalNeverIndexedWriter
                .index(index).value(value, valueHuffman);
    }

    protected final void literalNeverIndexed(CharSequence name,
                                             boolean nameHuffman,
                                             CharSequence value,
                                             boolean valueHuffman) {
        checkEncoding();
        encoding = true;
        writer = literalNeverIndexedWriter
                .name(name, nameHuffman).value(value, valueHuffman);
    }

    protected final void literalWithIndexing(int index,
                                             CharSequence value,
                                             boolean valueHuffman)
            throws IndexOutOfBoundsException {
        checkEncoding();
        encoding = true;
        writer = literalWithIndexingWriter
                .index(index).value(value, valueHuffman);
    }

    protected final void literalWithIndexing(CharSequence name,
                                             boolean nameHuffman,
                                             CharSequence value,
                                             boolean valueHuffman) {
        checkEncoding();
        encoding = true;
        writer = literalWithIndexingWriter
                .name(name, nameHuffman).value(value, valueHuffman);
    }

    protected final void sizeUpdate(int capacity)
            throws IllegalArgumentException {
        checkEncoding();
        // Ensure subclass follows the contract
        if (capacity > this.maxCapacity) {
            throw new IllegalArgumentException(
                    format("capacity <= maxCapacity: capacity=%s, maxCapacity=%s",
                            capacity, maxCapacity));
        }
        writer = sizeUpdateWriter.maxHeaderTableSize(capacity);
    }

    protected final int getMaxCapacity() {
        return maxCapacity;
    }

    protected final HeaderTable getHeaderTable() {
        return headerTable;
    }

    protected final void checkEncoding() { // TODO: better name e.g. checkIfEncodingInProgress()
        if (encoding) {
            throw new IllegalStateException(
                    "Previous encoding operation hasn't finished yet");
        }
    }
}
