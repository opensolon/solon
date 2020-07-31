package org.noear.solon.serialization.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

public class GsonDateSerialize implements JsonSerializer<Date> {
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getTime());
    }
}
