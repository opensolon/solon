package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.noear.solon.serialization.StringSerializer;

import java.io.IOException;

/**
 * @author noear 2021/10/11 created
 */
public class JacksonSerializer implements StringSerializer {
    final ObjectMapper real;
    public JacksonSerializer(ObjectMapper real){
        this.real = real;
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return real.writeValueAsString(obj);
    }
}
