package org.noear.solon.serialization.gson.impl;

import com.google.gson.*;

import java.lang.reflect.Type;

public class NullStringSerialize implements JsonSerializer<String> {

    @Override
    public JsonElement serialize(String s, Type type, JsonSerializationContext jsonSerializationContext) {
        if (s == null) {
            return new JsonPrimitive("");
        }
        return new JsonPrimitive(s);
    }
}
