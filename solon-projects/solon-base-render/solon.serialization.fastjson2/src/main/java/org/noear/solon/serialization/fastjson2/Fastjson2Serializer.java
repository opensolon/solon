package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.serialization.StringSerializer;

import java.io.IOException;

/**
 * @author 暮城留风
 * @since 1.10
 */
public class Fastjson2Serializer implements StringSerializer {
    ObjectWriterProvider config;
    JSONWriter.Feature[] features;
    JSONWriter.Context writeContext;

    protected Fastjson2Serializer(ObjectWriterProvider config, JSONWriter.Feature... features) {
        this.features = features;

        if (config != null) {
            this.config = config;
            this.writeContext = new JSONWriter.Context(config, features);
        }
    }

    @Override
    public String serialize(Object obj) throws IOException {
        if (config == null) {
            return JSON.toJSONString(obj, features);
        } else {
            return JSON.toJSONString(obj, writeContext);
        }
    }
}
