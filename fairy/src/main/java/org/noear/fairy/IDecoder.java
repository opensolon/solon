package org.noear.fairy;

/**
 * 解码器
 * */
public interface IDecoder {
    /**
     * 编码
     * */
    Enctype enctype();

    /**
     * 反序列化
     * */
    <T> T decode(Result rst, Class<T> clz);
}
