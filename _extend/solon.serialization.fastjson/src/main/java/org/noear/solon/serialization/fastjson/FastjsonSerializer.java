package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.serialization.StringSerializer;

import java.io.IOException;

/**
 * @author noear 2021/10/11 created
 */
public class FastjsonSerializer implements StringSerializer {
    SerializeConfig config;
    SerializerFeature[] features;

    protected FastjsonSerializer(SerializeConfig config, SerializerFeature... features) {
        this.config = config;
        this.features = features;
    }

    @Override
    public String serialize(Object obj) throws IOException {
        if (config == null) {
            return JSON.toJSONString(obj, features);
        } else {
            return JSON.toJSONString(obj, config, features);
        }
    }
}
