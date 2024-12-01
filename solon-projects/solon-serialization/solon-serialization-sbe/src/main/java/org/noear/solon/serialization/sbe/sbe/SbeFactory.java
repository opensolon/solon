package org.noear.solon.serialization.sbe.sbe;

import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.noear.solon.serialization.sbe.io.BytesFactory;

/**
 * @author noear 2024/12/1 created
 */
public class SbeFactory implements BytesFactory<SbeInputBuffers,SbeOutputBuffers> {
    public static final SbeFactory instance = new SbeFactory();

    public static BytesFactory<SbeInputBuffers, SbeOutputBuffers> getInstance() {
        return instance;
    }

    @Override
    public SbeInputBuffers createReader(byte[] bytes) {
        MutableDirectBuffer buffer = new UnsafeBuffer(bytes);

        return new SbeInputBuffers().wrap(buffer);
    }

    @Override
    public SbeOutputBuffers createWriter() {
        ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(128);
        return new SbeOutputBuffers(buffer);
    }
}