package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ActionSerializer;

import java.io.IOException;

/**
 * Fastjson 字符串序列化
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class FastjsonStringSerializer implements ActionSerializer<String> {
    private SerializeConfig serializeConfig;
    private int serializerFeatures = JSON.DEFAULT_GENERATE_FEATURE;
    private ParserConfig parserConfig;
    private int parserFeatures = JSON.DEFAULT_PARSER_FEATURE;

    public SerializeConfig getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new SerializeConfig();
        }

        return serializeConfig;
    }

    public void cfgSerializerFeatures(boolean isReset, boolean isAdd, SerializerFeature... features) {
        if (isReset) {
            serializerFeatures = JSON.DEFAULT_GENERATE_FEATURE;
        }

        for (SerializerFeature feature : features) {
            if (isAdd) {
                serializerFeatures |= feature.getMask();
            } else {
                serializerFeatures &= ~feature.getMask();
            }
        }
    }

    public ParserConfig getParserConfig() {
        if (parserConfig == null) {
            parserConfig = new ParserConfig();
        }

        return parserConfig;
    }

    public void cfgParserFeatures(boolean isReset, boolean isAdd, Feature... features) {
        if (isReset) {
            parserFeatures = JSON.DEFAULT_GENERATE_FEATURE;
        }

        for (Feature feature : features) {
            if (isAdd) {
                parserFeatures |= feature.getMask();
            } else {
                parserFeatures &= ~feature.getMask();
            }
        }

    }

    @Override
    public String name() {
        return "fastjson-json";
    }

    @Override
    public String serialize(Object obj) throws IOException {
        if (serializeConfig == null) {
            return JSON.toJSONString(obj, serializerFeatures);
        } else {
            return JSON.toJSONString(obj, serializeConfig, new SerializeFilter[0], null, serializerFeatures);
        }
    }

    @Override
    public Object deserialize(String data, Class<?> clz) throws IOException {
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

    @Override
    public Object deserializeBody(Context ctx) throws IOException {
        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            if (parserConfig == null) {
                return JSON.parse(data, parserFeatures);
            } else {
                return JSON.parse(data, parserConfig, parserFeatures);
            }
        } else {
            return null;
        }
    }
}