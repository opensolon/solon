package org.noear.solon.serialization.gson.impl;

import com.google.gson.*;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.noear.solon.serialization.prop.JsonProps;

import java.io.IOException;

public class NullBooleanAdapter extends TypeAdapter<Boolean> {
    private JsonProps jsonProps;

    public NullBooleanAdapter(JsonProps jsonProps) {
        this.jsonProps = jsonProps;
    }

    @Override
    public void write(JsonWriter out, Boolean aBoolean) throws IOException {
        if (jsonProps.boolAsInt) {
            if (aBoolean == null) {
                out.value(0);
            } else {
                out.value(aBoolean ? 1 : 0);
            }
        } else {
            if (aBoolean == null) {
                out.value(false);
            } else {
                out.value(aBoolean);
            }
        }

    }

    @Override
    public Boolean read(JsonReader jsonReader) throws IOException {
        return TypeAdapters.BOOLEAN.read(jsonReader);
    }
}
