package org.noear.fairy;

/**
 * 编码器
 * */
public interface IEncoder {
    /**
     * 编码
     * */
    Enctype enctype();

    /**
     * 序列化
     * */
    Object encode(Object obj);
}
