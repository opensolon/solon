package org.noear.solon.serialization.abc.chronicle.bytes;

import org.noear.solon.serialization.abc.io.AbcFactory;
import org.noear.solon.serialization.abc.io.AbcSerializable;

public interface ChrBytesSerializable extends AbcSerializable<ChrBytesInput, ChrBytesOutput> {
    @Override
    default AbcFactory<ChrBytesInput, ChrBytesOutput> serializeFactory(){
        return ChrBytesFactory.getInstance();
    }
}