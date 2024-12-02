package org.noear.solon.serialization.abc.chronicle.bytes;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import org.noear.solon.serialization.abc.io.AbcFactory;
import org.noear.solon.serialization.abc.io.AbcSerializable;

public interface ChrBytesSerializable extends AbcSerializable<BytesIn, BytesOut> {
    @Override
    default AbcFactory<BytesIn, BytesOut> serializeFactory(){
        return ChrBytesFactory.getInstance();
    }
}