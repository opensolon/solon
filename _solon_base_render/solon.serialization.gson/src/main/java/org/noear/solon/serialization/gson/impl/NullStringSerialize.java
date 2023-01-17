package org.noear.solon.serialization.gson.impl;

import com.google.gson.*;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class NullStringSerialize extends TypeAdapter<String> {

    @Override
    public void write(JsonWriter out, String s) throws IOException {
        if (s == null) {
            out.value("");
        } else {
            out.value(s);
        }
    }

    @Override
    public String read(JsonReader jsonReader) throws IOException {
        return TypeAdapters.STRING.read(jsonReader);
    }
}
