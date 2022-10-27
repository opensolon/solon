package io.jsonwebtoken.snack.io;

import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;

import java.nio.charset.StandardCharsets;

/**
 * Snack3 的 Serializer 实现，支持 bean 序列化
 *
 * @author noear
 * @since 1.10
 */
public class SnackSerializer<T> implements Serializer<T> {
    Options options = Options.serialize().add(Feature.EnumUsingName);
    @Override
    public byte[] serialize(T t) throws SerializationException {
        ONode oNode = ONode.loadObj(t, options);
        oNode.remove(options.getTypePropertyName());

        String json = oNode.toJson();
        return json.getBytes(StandardCharsets.UTF_8);
    }
}
