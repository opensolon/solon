package org.noear.solonclient;

public interface ISerializer {
    String stringify(Object obj);
    String serialize(Object obj);
    <T> T deserialize(String str, Class<T> clz);
}
