package org.noear.solonclient;

public interface ISerializer {
    /**
     * 编码
     * */
    Enctype enctype();

    /**
     * 序列化
     * */
    Object serialize(Object obj);
}
