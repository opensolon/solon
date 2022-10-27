package io.jsonwebtoken.snack.io;

import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;
import org.noear.snack.ONode;
import org.noear.snack.core.DEFAULTS;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;

import java.nio.charset.StandardCharsets;

public class SnackSerializer<T> implements Serializer<T> {
    Options options = Options.serialize().add(Feature.EnumUsingName);
    @Override
    public byte[] serialize(T t) throws SerializationException {
        ONode oNode = ONode.loadObj(t, options);
        oNode.remove(DEFAULTS.DEF_TYPE_PROPERTY_NAME);
        String json = oNode.toJson();
        return json.getBytes(StandardCharsets.UTF_8);
    }
}
