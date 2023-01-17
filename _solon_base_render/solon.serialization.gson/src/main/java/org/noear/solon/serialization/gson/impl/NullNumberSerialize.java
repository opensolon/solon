package org.noear.solon.serialization.gson.impl;

import com.google.gson.*;

import java.lang.reflect.Type;

public class NullNumberSerialize implements JsonSerializer<Number> {

    @Override
    public JsonElement serialize(Number number, Type type, JsonSerializationContext jsonSerializationContext) {
        if (number == null) {
            if (type == Long.class) {
                return new JsonPrimitive(0L);
            } else if (type == Double.class) {
                return new JsonPrimitive(0.0D);
            } else if (type == Float.class) {
                return new JsonPrimitive(0.0F);
            } else {
                return new JsonPrimitive(0);
            }
        }

        return new JsonPrimitive(number);
    }
}
