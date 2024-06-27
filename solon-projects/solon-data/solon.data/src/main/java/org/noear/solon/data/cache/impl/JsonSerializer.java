package org.noear.solon.data.cache.impl;

import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.core.util.ClassUtil;

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
    public String serialize(Object fromObj) {
        return ONode.stringify(fromObj);
    }

    @Override
    public Object deserialize(String dta, Class<?> toClz) {
        Options options = Options.serialize();

        //分析类加载器
        options.setClassLoader(ClassUtil.resolveClassLoader(toClz));

        return ONode.load(dta, options).toObject(toClz);
    }
}