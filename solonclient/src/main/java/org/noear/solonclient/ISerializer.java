package org.noear.solonclient;

public interface ISerializer {
    String serialize(Object obj);
    <T> T deserialize(String str, Class<T> clz);
}
