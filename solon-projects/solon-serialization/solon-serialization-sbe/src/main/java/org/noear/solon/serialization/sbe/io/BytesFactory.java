package org.noear.solon.serialization.sbe.io;

/**
 * @author noear
 * @since 3.0
 */
public interface BytesFactory<R extends BytesReader,W extends BytesWriter> {
    /**
     * 创建读取器
     */
    R createReader(byte[] bytes);

    /**
     * 创建书写器
     */
    W createWriter();
}
