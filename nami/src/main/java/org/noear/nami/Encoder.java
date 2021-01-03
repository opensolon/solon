package org.noear.nami;

/**
 * 编码器
 * */
public interface Encoder {
    /**
     * 编码
     * */
    String enctype();

    /**
     * 序列化
     * */
    byte[] encode(Object obj);
}
