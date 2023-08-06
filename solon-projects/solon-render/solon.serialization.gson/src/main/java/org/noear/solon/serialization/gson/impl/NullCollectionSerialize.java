package org.noear.solon.serialization.gson.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author noear
 * @since 1.12
 */
public class NullCollectionSerialize implements JsonSerializer<Collection> {
    @Override
    public JsonElement serialize(Collection collection, Type type, JsonSerializationContext jsonSerializationContext) {
        if (collection == null) {
            return new JsonArray();
        }

        return jsonSerializationContext.serialize(collection, type);
    }
}
