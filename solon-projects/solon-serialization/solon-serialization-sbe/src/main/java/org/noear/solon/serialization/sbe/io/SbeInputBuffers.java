/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.serialization.sbe.io;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Sbe 输入缓冲
 *
 * @author noear
 * @since 3.0
 * */
public class SbeInputBuffers {
    private DirectBuffer buffer;
    private int currentOffset = 0;

    public SbeInputBuffers wrap(final DirectBuffer buffer) {
        return this.wrap(buffer, 0);
    }

    public SbeInputBuffers wrap(final DirectBuffer buffer, final int offset) {
        this.buffer = buffer;
        this.currentOffset = offset;
        return this;
    }

    public int readInt() {
        final int intValue = buffer.getInt(currentOffset);
        currentOffset += BitUtil.SIZE_OF_INT;
        return intValue;
    }

    public long readLong() {
        final long longValue = buffer.getLong(currentOffset);
        currentOffset += BitUtil.SIZE_OF_LONG;
        return longValue;
    }

    public float readFloat() {
        final float floatValue = buffer.getFloat(currentOffset);
        currentOffset += BitUtil.SIZE_OF_FLOAT;
        return floatValue;
    }

    public double readDouble() {
        final double doubleValue = buffer.getDouble(currentOffset);
        currentOffset += BitUtil.SIZE_OF_DOUBLE;
        return doubleValue;
    }

    public byte readByte() {
        final byte byteValue = buffer.getByte(currentOffset);
        currentOffset += BitUtil.SIZE_OF_BYTE;
        return byteValue;
    }

    public char readChar() {
        final char charValue = buffer.getChar(currentOffset);
        currentOffset += BitUtil.SIZE_OF_CHAR;
        return charValue;
    }

    public boolean readBoolean() {
        final byte booleanByte = buffer.getByte(currentOffset);
        currentOffset += BitUtil.SIZE_OF_BYTE;
        return booleanByte == 1;
    }

    public short readShort() {
        final short shortValue = buffer.getShort(currentOffset);
        currentOffset += BitUtil.SIZE_OF_SHORT;
        return shortValue;
    }

    public String readString() {
        return new String(readByteArray());
    }

    public BigInteger readBigInteger() {
        return new BigInteger(readByteArray());
    }

    public BigDecimal readBigDecimal() {
        return new BigDecimal(readString());
    }


    public long[] readLongArray() {
        final int length = readInt();
        final long[] longs = new long[length];
        for (int i = 0; i < longs.length; i++) {
            longs[i] = readLong();
        }

        return longs;
    }

    public int[] readIntArray() {
        final int length = readInt();
        final int[] ints = new int[length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = readInt();
        }

        return ints;
    }

    public float[] readFloatArray() {
        final int length = readInt();
        final float[] floats = new float[length];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = readFloat();
        }

        return floats;
    }

    public double[] readDoubleArray() {
        final int length = readInt();
        final double[] doubles = new double[length];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = readDouble();
        }

        return doubles;
    }

    public short[] readShortArray() {
        final int length = readInt();
        final short[] shorts = new short[length];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = readShort();
        }

        return shorts;
    }

    public byte[] readByteArray() {
        final int length = readInt();
        final byte[] bytes = new byte[length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = readByte();
        }

        return bytes;
    }

    public char[] readCharArray() {
        final int length = readInt();
        final char[] chars = new char[length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = readChar();
        }

        return chars;
    }

    public <T extends SbeSerializable> T readObject(Function<SbeInputBuffers, T> creator) {
        if (readBoolean()) {// 检查对象是否存在
            T object = creator.apply(this);// 通过 Function 反序列化对象
            object.readBuffer(this);
            return object;
        }
        return null;
    }

    public <T> T[] readArray(final Function<SbeInputBuffers, T> creator, final IntFunction<T[]> arrayCreator) {
        final int length = readInt();
        final T[] array = arrayCreator.apply(length);
        for (int i = 0; i < length; i++) {
            array[i] = creator.apply(this);
        }
        return array;
    }

    public <T> List<T> readList(final Function<SbeInputBuffers, T> creator) {
        final int length = readInt();
        final List<T> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(creator.apply(this));
        }
        return list;
    }

    public <T> T readNullable(final Function<SbeInputBuffers, T> creator) {
        return readBoolean() ? creator.apply(this) : null;
    }

    public <K, V, M extends Map<K, V>> M readMap(final Supplier<M> mapSupplier,
                                                 final Function<SbeInputBuffers, K> keyCreator,
                                                 final Function<SbeInputBuffers, V> valCreator) {
        final int length = readInt();
        final M map = mapSupplier.get();
        for (int i = 0; i < length; i++) {
            map.put(keyCreator.apply(this), valCreator.apply(this));
        }
        return map;
    }
}