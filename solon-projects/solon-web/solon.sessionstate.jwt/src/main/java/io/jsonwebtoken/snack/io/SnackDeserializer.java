package io.jsonwebtoken.snack.io;

import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;

/**
 * Snack3 的 Deserializer 实现
 *
 * @author noear
 * @since 1.10
 */
public class SnackDeserializer<T> implements Deserializer<T> {
    Options options = Options.serialize().add(Feature.EnumUsingName);

    @Override
    public T deserialize(byte[] bytes) throws DeserializationException {
        String json = new String(bytes);
        ONode oNode = ONode.loadStr(json, options);
        oNode.remove(options.getTypePropertyName());

        return oNode.toObject(Object.class);
    }
}