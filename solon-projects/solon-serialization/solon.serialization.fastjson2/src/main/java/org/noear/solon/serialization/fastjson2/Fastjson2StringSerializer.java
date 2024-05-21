package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.serialization.Serializer;

import java.io.IOException;

/**
 * Fastjson2 字符串序列化
 *
 * @author 暮城留风
 * @since 1.10
 * @since 2.8
 */
public class Fastjson2StringSerializer implements Serializer<String> {
    private JSONWriter.Feature[] writeFeatures;
    private JSONWriter.Context writeContext;

    private JSONReader.Feature[] readerFeatures;
    private JSONReader.Context readerContext;

    /**
     * 设置序列化配置
     */
    public void setSerializeConfig(ObjectWriterProvider config, JSONWriter.Feature... features) {
        this.writeFeatures = features;

        if (config != null) {
            this.writeContext = new JSONWriter.Context(config, features);
        }
    }

    /**
     * 设置反序列化配置
     */
    public void setDeserializeCofnig(ObjectReaderProvider config, JSONReader.Feature... features) {
        this.readerFeatures = features;

        if (config != null) {
            this.readerContext = new JSONReader.Context(config, features);
        }
    }

    @Override
    public String serialize(Object obj) throws IOException {
        if (writeContext == null) {
            return JSON.toJSONString(obj, writeFeatures);
        } else {
            return JSON.toJSONString(obj, writeContext);
        }
    }

    @Override
    public Object deserialize(String data, Class<?> clz) throws IOException {
        if (clz == null) {
            if (readerContext == null) {
                return JSON.parse(data, readerFeatures);
            } else {
                return JSON.parse(data, readerContext);
            }
        } else {
            if (readerContext == null) {
                return JSON.parseObject(data, clz, readerFeatures);
            } else {
                return JSON.parseObject(data, clz, readerContext);
            }
        }
    }
}