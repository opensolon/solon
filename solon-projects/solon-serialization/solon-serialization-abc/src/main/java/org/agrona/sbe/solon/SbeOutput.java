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
package org.agrona.sbe.solon;

import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Sbe 输出冲缓
 *
 * @author noear
 * @since 3.0
 * */
public class SbeOutput {
    private final MutableDirectBuffer buffer;
    private int currentOffset = 0;

    public SbeOutput(final MutableDirectBuffer buffer) {
        this.buffer = buffer;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[buffer.capacity()];
        buffer.getBytes(0, bytes);
        return bytes;
    }

    public void writeInt(final int intValue) {
        buffer.putInt(currentOffset, intValue);
        currentOffset += BitUtil.SIZE_OF_INT;
    }

    public void writeLong(final long longValue) {
        buffer.putLong(currentOffset, longValue);
        currentOffset += BitUtil.SIZE_OF_LONG;
    }

    public void writeFloat(final float floatValue) {
        buffer.putFloat(currentOffset, floatValue);
        currentOffset += BitUtil.SIZE_OF_FLOAT;
    }

    public void writeDouble(final double doubleValue) {
        buffer.putDouble(currentOffset, doubleValue);
        currentOffset += BitUtil.SIZE_OF_DOUBLE;
    }

    public void writeByte(final byte byteValue) {
        buffer.putByte(currentOffset, byteValue);
        currentOffset += BitUtil.SIZE_OF_BYTE;
    }

    public void writeChar(final char charValue) {
        buffer.putChar(currentOffset, charValue);
        currentOffset += BitUtil.SIZE_OF_CHAR;
    }

    public void writeBoolean(final boolean booleanValue) {
        buffer.putByte(currentOffset, (byte) (booleanValue ? 1 : 0));
        currentOffset += BitUtil.SIZE_OF_BYTE;
    }

    public void writeShort(final short shortValue) {
        buffer.putShort(currentOffset, shortValue);
        currentOffset += BitUtil.SIZE_OF_SHORT;
    }

    public void writeString(final String stringValue) {
        writeByteArray(stringValue.getBytes());
    }

    public void writeBigInteger(final BigInteger bigInteger) {
        writeByteArray(bigInteger.toByteArray());
    }

    public void writeBigDecimal(final BigDecimal bigDecimal) {
        writeString(bigDecimal.toString());
    }

    public void writeIntArray(final int[] ints) {
        writeInt(ints.length);
        for (int i = 0; i < ints.length; i++) {
            writeInt(ints[i]);
        }
    }

    public void writeLongArray(final long[] longs) {
        writeInt(longs.length);
        for (int i = 0; i < longs.length; i++) {
            writeLong(longs[i]);
        }
    }

    public void writeFloatArray(final float[] floats) {
        writeFloat(floats.length);
        for (int i = 0; i < floats.length; i++) {
            writeFloat(floats[i]);
        }
    }

    public void writeDoubleArray(final double[] doubles) {
        writeInt(doubles.length);
        for (int i = 0; i < doubles.length; i++) {
            writeDouble(doubles[i]);
        }
    }

    public void writeShortArray(final short[] shorts) {
        writeInt(shorts.length);
        for (int i = 0; i < shorts.length; i++) {
            writeShort(shorts[i]);
        }
    }

    public void writeByteArray(final byte[] bytes) {
        writeInt(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            writeByte(bytes[i]);
        }
    }

    public void writeCharArray(final char[] chars) {
        writeInt(chars.length);
        for (int i = 0; i < chars.length; i++) {
            writeChar(chars[i]);
        }
    }

    public <T extends SbeSerializable> void writeObject(final T object) {
        writeBoolean(object != null);
        if (object != null) {
            object.serializeWrite(this);
        }
    }

    public <T extends SbeSerializable> void writeArray(final T[] array) {
        writeInt(array.length);
        for (int i = 0; i < array.length; i++) {
            array[i].serializeWrite(this);
        }
    }

    public <T extends SbeSerializable> void writeList(final List<SbeSerializable> collection) {
        final int size = collection.size();
        writeInt(size);
        collection.forEach(e -> e.serializeWrite(this));
    }

    public <T> void writeNullable(final T object, final BiConsumer<T, SbeOutput> marshaller) {
        writeBoolean(object != null);
        if (object != null) {
            marshaller.accept(object, this);
        }
    }

    public <K, V> void writeMap(final Map<K, V> map,
                                final BiConsumer<SbeOutput, K> keyMarshaller,
                                final BiConsumer<SbeOutput, V> valMarshaller) {
        final int size = map.size();
        writeInt(size);

        map.forEach((k, v) -> {
            keyMarshaller.accept(this, k);
            valMarshaller.accept(this, v);
        });
    }

    public void reset() {
        currentOffset = 0;
    }
}