package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.noear.solon.serialization.StringSerializer;

import java.io.IOException;

/**
 * @author noear 2021/10/11 created
 */
public class Fastjson2Serializer implements StringSerializer {
    JSONWriter.Feature[] features;

    protected Fastjson2Serializer(JSONWriter.Feature... features) {
        this.features = features;
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return JSON.toJSONString(obj, features);
    }
}
