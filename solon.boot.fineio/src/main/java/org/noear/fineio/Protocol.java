package org.noear.fineio;

import java.nio.ByteBuffer;

public interface Protocol<T> {
    T decode(final ByteBuffer buffer);
}
