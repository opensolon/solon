package org.noear.solon.serialization.gson.impl;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class NullNumberSerialize<T extends Number> extends TypeAdapter<T> {
    @Override
    public void write(JsonWriter out, T number) throws IOException {
        if (number == null) {
            out.value(0);
        } else {
            out.value(number);
        }
    }

    @Override
    public T read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
