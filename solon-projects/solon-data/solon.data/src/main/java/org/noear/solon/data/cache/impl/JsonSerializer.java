package org.noear.solon.data.cache.impl;

import org.noear.snack.ONode;
import org.noear.solon.data.cache.Serializer;

/**
 * Json 序列化实现
 *
 * @author noear
 * @since 2.5
 */
public class JsonSerializer implements Serializer<String> {
    public static final JsonSerializer instance = new JsonSerializer();

    @Override
    public String name() {
        return "json-snack3";
    }

    @Override
    public String serialize(Object fromObj) throws Exception {
        return ONode.stringify(fromObj);
    }

    @Override
    public Object deserialize(String dta, Class<?> toClz) throws Exception {
        return ONode.deserialize(dta, toClz);
    }
}
