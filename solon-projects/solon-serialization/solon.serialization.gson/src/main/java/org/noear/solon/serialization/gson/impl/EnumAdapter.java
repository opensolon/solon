package org.noear.solon.serialization.gson.impl;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * @author noear
 * @since 2.2
 */
public class EnumAdapter extends TypeAdapter<Enum> {
    @Override
    public void write(JsonWriter out, Enum anEnum) throws IOException {
        out.value(anEnum.name());
    }

    @Override
    public Enum read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
