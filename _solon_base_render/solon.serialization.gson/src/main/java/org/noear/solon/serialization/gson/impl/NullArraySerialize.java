package org.noear.solon.serialization.gson.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author noear
 * @since 1.12
 */
public class NullArraySerialize implements JsonSerializer<Arrays> {
    @Override
    public JsonElement serialize(Arrays collection, Type type, JsonSerializationContext jsonSerializationContext) {
        if (collection == null) {
            return new JsonArray();
        }

        return jsonSerializationContext.serialize(collection, type);
    }
}
