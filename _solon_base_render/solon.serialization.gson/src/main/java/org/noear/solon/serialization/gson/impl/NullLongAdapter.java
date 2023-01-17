package org.noear.solon.serialization.gson.impl;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.noear.solon.serialization.prop.JsonProps;

import java.io.IOException;

public class NullLongAdapter extends TypeAdapter<Long> {
    private JsonProps jsonProps;

    public NullLongAdapter(JsonProps jsonProps) {
        this.jsonProps = jsonProps;
    }

    @Override
    public void write(JsonWriter out, Long number) throws IOException {
        if (jsonProps.longAsString) {
            if (number == null) {
                out.value("0");
            } else {
                out.value(number.toString());
            }
        } else {
            if (number == null) {
                out.value(0);
            } else {
                out.value(number);
            }
        }
    }

    @Override
    public Long read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
