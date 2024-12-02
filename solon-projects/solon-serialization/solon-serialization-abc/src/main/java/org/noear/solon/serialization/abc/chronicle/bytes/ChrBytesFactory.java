package org.noear.solon.serialization.abc.chronicle.bytes;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import org.noear.solon.serialization.abc.io.AbcFactory;

public class ChrBytesFactory implements AbcFactory<ChrBytesInput, ChrBytesOutput> {
    public static final ChrBytesFactory instance = new ChrBytesFactory();

    public static AbcFactory<ChrBytesInput, ChrBytesOutput> getInstance() {
        return instance;
    }

    @Override
    public ChrBytesInput createInput(byte[] bytes) {
        BytesIn<byte[]> bytesIn = Bytes.wrapForRead(bytes);
        return new ChrBytesInput(bytesIn);
    }

    @Override
    public ChrBytesOutput createOutput() {
        BytesOut<Void> bytesOut =  Bytes.allocateElasticDirect(128);
        return new ChrBytesOutput(bytesOut);
    }
}