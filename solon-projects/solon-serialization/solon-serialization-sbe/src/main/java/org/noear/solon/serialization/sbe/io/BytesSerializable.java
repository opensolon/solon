package org.noear.solon.serialization.sbe.io;

/**
 * @author noear
 * @since 3.0
 */
public interface BytesSerializable<R extends BytesReader,W extends BytesWriter> {
    /**
     * 序列化工厂
     */
    BytesFactory<R, W> serializeFactory();

    /**
     * 序列化读取
     */
    void serializeRead(R in);

    /**
     * 序列化写入
     */
    void serializeWrite(W out);
}