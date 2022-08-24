package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.serialization.StringSerializer;

import java.io.IOException;

/**
 * @author noear
 * @since 1.9
 */
public class Fastjson2Serializer implements StringSerializer {
    ObjectWriterProvider config;
    JSONWriter.Feature[] features;

    protected Fastjson2Serializer(ObjectWriterProvider config, JSONWriter.Feature... features) {
        this.config = config;
        this.features = features;
    }

    @Override
    public String serialize(Object obj) throws IOException {
        if (config == null) {
            return JSON.toJSONString(obj, features);
        } else {
            return JSON.toJSONString(obj, features);
            //JSONWriter.Context c = new JSONWriter.Context(config,features);
            //return JSON.toJSONString(obj, config, features);
        }
    }
}
