package org.noear.solon.serialization.gson;

import com.google.gson.Gson;
import org.noear.solon.serialization.StringSerializer;

import java.io.IOException;

/**
 * @author noear 2021/10/11 created
 */
public class GsonSerializer implements StringSerializer {
    private Gson real;

    public GsonSerializer(Gson real) {
        this.real = real;
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return real.toJson(obj);
    }
}
