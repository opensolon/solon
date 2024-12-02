package org.noear.solon.serialization.abc.chronicle.bytes;

import net.openhft.chronicle.bytes.BytesOut;
import org.noear.solon.serialization.abc.io.AbcOutput;

public class ChrBytesOutput implements AbcOutput {
    public BytesOut _ ;

    public ChrBytesOutput(BytesOut bytesOut){
        this._ = bytesOut;
    }

    @Override
    public byte[] toBytes() {
        return _.bytesForRead().toByteArray();
    }
}
