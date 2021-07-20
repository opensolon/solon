package org.noear.solon.cache.jedis;


import org.noear.solon.data.cache.Serializer;

import java.util.Base64;

/**
 * @author noear 2021/6/15 created
 */
public class JavabinSerializer implements Serializer<String> {
    public static final JavabinSerializer instance = new JavabinSerializer();

    @Override
    public String name() {
        return "java-bin";
    }

    @Override
    public String serialize(Object obj) throws Exception {
        byte[] tmp = SerializationUtils.serialize(obj);
        return Base64.getEncoder().encodeToString(tmp);
    }

    @Override
    public Object deserialize(String dta) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(dta);
        return SerializationUtils.deserialize(bytes);
    }
}
