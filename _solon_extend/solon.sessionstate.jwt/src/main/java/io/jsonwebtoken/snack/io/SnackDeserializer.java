package io.jsonwebtoken.snack.io;

import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import org.noear.snack.ONode;
import org.noear.snack.core.DEFAULTS;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;

/**
 * @author noear 2022/10/27 created
 */
public class SnackDeserializer<T> implements Deserializer<T> {
    Options options = Options.serialize().add(Feature.EnumUsingName);

    @Override
    public T deserialize(byte[] bytes) throws DeserializationException {
        String json = new String(bytes);
        ONode oNode = ONode.loadStr(json, options);
        oNode.remove(DEFAULTS.DEF_TYPE_PROPERTY_NAME);
        return oNode.toObject(Object.class);
    }
}