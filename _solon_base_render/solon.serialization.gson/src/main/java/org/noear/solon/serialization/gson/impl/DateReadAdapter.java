package org.noear.solon.serialization.gson.impl;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.noear.solon.core.util.DateAnalyzer;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * @author noear
 * @since 2.2
 */
public class DateReadAdapter extends TypeAdapter<Date> {
    @Override
    public void write(JsonWriter jsonWriter, Date date) throws IOException {

    }

    @Override
    public Date read(JsonReader in) throws IOException {
        JsonToken jsonToken = in.peek();
        switch (jsonToken) {
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                return new Date(in.nextLong());
            case STRING:
                try {
                    return DateAnalyzer.getGlobal().parse(in.nextString());
                } catch (ParseException e) {
                    throw new JsonSyntaxException("Expecting date, got: " + jsonToken + "; at path " + in.getPath(), e);
                }
            default:
                throw new JsonSyntaxException("Expecting date, got: " + jsonToken + "; at path " + in.getPath());
        }
    }
}
