package org.noear.solon.cache.jedis;

import org.noear.snack.ONode;
import org.noear.solon.data.cache.Serializer;

/**
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
