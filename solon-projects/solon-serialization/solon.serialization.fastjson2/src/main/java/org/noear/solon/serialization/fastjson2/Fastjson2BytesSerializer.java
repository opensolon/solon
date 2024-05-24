package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ActionSerializer;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Fastjson2 字符串序列化
 *
 * @author 暮城留风
 * @since 1.10
 * @since 2.8
 */
public class Fastjson2BytesSerializer implements ActionSerializer<byte[]> {
    private JSONWriter.Context serializeConfig;
    private JSONReader.Context deserializeConfig;
    private Charset charset;

    public Fastjson2BytesSerializer() {
        charset = Charset.forName(Solon.encoding());
    }

    public JSONWriter.Context getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new JSONWriter.Context(new ObjectWriterProvider());
        }

        return serializeConfig;
    }

    public void cfgSerializeFeatures(boolean isReset, boolean isAdd, JSONWriter.Feature... features) {
        if (isReset) {
            getSerializeConfig().setFeatures(0);//JSONFactory.getDefaultWriterFeatures());
        }

        for (JSONWriter.Feature feature : features) {
            getSerializeConfig().config(feature, isAdd);
        }
    }

    public JSONReader.Context getDeserializeConfig() {
        if (deserializeConfig == null) {
            deserializeConfig = new JSONReader.Context(new ObjectReaderProvider());
        }
        return deserializeConfig;
    }

    public void cfgDeserializeFeatures(boolean isReset, boolean isAdd, JSONReader.Feature... features) {
        if (isReset) {
            getDeserializeConfig().setFeatures(0);//JSONFactory.getDefaultReaderFeatures());
        }

        for (JSONReader.Feature feature : features) {
            getDeserializeConfig().config(feature, isAdd);
        }
    }

    @Override
    public String name() {
        return "fastjson2-jsonb";
    }

    @Override
    public byte[] serialize(Object obj) throws IOException {
        return JSON.toJSONBytes(obj, charset, getSerializeConfig());
    }

    @Override
    public Object deserialize(byte[] data, Class<?> clz) throws IOException {
        if (clz == null) {
            return JSON.parse(data, getDeserializeConfig());
        } else {
            return JSON.parseObject(data, clz, getDeserializeConfig());
        }
    }

    @Override
    public Object deserializeBody(Context ctx) throws IOException {
        byte[] data = ctx.bodyAsBytes();

        return JSON.parse(data, getDeserializeConfig());
    }
}