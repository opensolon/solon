package org.noear.solon.serialization.abc.chronicle.bytes;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import org.noear.solon.serialization.abc.io.AbcFactory;

public class ChrBytesFactory implements AbcFactory<BytesIn, BytesOut> {
    public static final ChrBytesFactory instance = new ChrBytesFactory();

    public static AbcFactory<BytesIn, BytesOut> getInstance() {
        return instance;
    }

    @Override
    public BytesIn createInput(byte[] bytes) {
        return Bytes.wrapForRead(bytes);
    }

    @Override
    public BytesOut createOutput() {
        return Bytes.allocateElasticDirect(128);
    }

    @Override
    public byte[] extractBytes(BytesOut out) {
        return out.bytesForRead().toByteArray();
    }
}