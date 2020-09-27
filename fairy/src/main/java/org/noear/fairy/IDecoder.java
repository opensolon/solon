package org.noear.fairy;

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
