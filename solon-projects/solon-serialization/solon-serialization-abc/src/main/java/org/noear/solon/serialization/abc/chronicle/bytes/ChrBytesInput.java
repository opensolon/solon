package org.noear.solon.serialization.abc.chronicle.bytes;

import org.noear.solon.serialization.abc.io.AbcInput;
import net.openhft.chronicle.bytes.BytesIn;

public class ChrBytesInput implements AbcInput {
    public BytesIn _;

    public ChrBytesInput(BytesIn bytesIn) {
        this._ = bytesIn;
    }
}
