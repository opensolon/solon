package org.noear.solon.serialization.gson.impl;

import com.google.gson.*;

import java.lang.reflect.Type;

public class NullBooleanSerialize implements JsonSerializer<Boolean> {

    @Override
    public JsonElement serialize(Boolean aBoolean, Type type, JsonSerializationContext jsonSerializationContext) {
        if (aBoolean == null) {
            return new JsonPrimitive(false);
        }
        return new JsonPrimitive(aBoolean);
    }
}
