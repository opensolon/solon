package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.serialization.Serializer;

import java.io.IOException;

/**
 * Fastjson 字符串序列化
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class FastjsonStringSerializer implements Serializer<String> {
    private SerializeConfig serializeConfig;
    private SerializerFeature[] serializerFeatures;
    private ParserConfig parserConfig;
    private Feature[] parserFeatures;

    /**
     * 设置序列化配置
     */
    public void setSerializeConfig(SerializeConfig serializeConfig, SerializerFeature[] serializerFeatures) {
        this.serializeConfig = serializeConfig;
        this.serializerFeatures = serializerFeatures;
    }

    /**
     * 设置反序列化配置
     */
    public void setDeserializeConfig(ParserConfig parserConfig, Feature[] parserFeatures) {
        this.parserConfig = parserConfig;
        this.parserFeatures = parserFeatures;
    }

    @Override
    public String serialize(Object obj) throws IOException {
        if (serializerFeatures == null) {
            serializerFeatures = new SerializerFeature[0];
        }

        if (serializeConfig == null) {
            return JSON.toJSONString(obj, serializerFeatures);
        } else {
            return JSON.toJSONString(obj, serializeConfig, serializerFeatures);
        }
    }

    @Override
    public Object deserialize(String data, Class<?> clz) throws IOException {
        if (parserFeatures == null) {
            parserFeatures = new Feature[0];
        }

        if (clz == null) {
            if (parserConfig == null) {
                return JSON.parse(data, parserFeatures);
            } else {
                return JSON.parse(data, parserConfig, parserFeatures);
            }
        } else {
            if (parserConfig == null) {
                return JSON.parseObject(data, clz, parserFeatures);
            } else {
                return JSON.parseObject(data, clz, parserConfig, parserFeatures);
            }
        }
    }
}