package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ActionSerializer;

import java.io.IOException;

/**
 * Fastjson2 字符串序列化
 *
 * @author 暮城留风
 * @since 1.10
 * @since 2.8
 */
public class Fastjson2StringSerializer implements ActionSerializer<String> {
    private JSONWriter.Context writeContext;

    private JSONReader.Context readerContext;

    public JSONReader.Context getReaderContext() {
        if (readerContext == null) {
            readerContext = new JSONReader.Context(new ObjectReaderProvider());
        }
        return readerContext;
    }

    public void cfgReaderFeatures(boolean isReset, boolean isAdd, JSONReader.Feature... features) {
        for (JSONReader.Feature feature : features) {
            getReaderContext().config(feature, isAdd);
        }
    }

    public JSONWriter.Context getWriteContext() {
        if (writeContext == null) {
            writeContext = new JSONWriter.Context(new ObjectWriterProvider());
        }

        return writeContext;
    }

    public void cfgWriteFeatures(boolean isReset, boolean isAdd, JSONWriter.Feature... features) {
        for (JSONWriter.Feature feature : features) {
            getWriteContext().config(feature, isAdd);
        }
    }

    @Override
    public String name() {
        return "fastjson2-json";
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return JSON.toJSONString(obj, getWriteContext());
    }

    @Override
    public Object deserialize(String data, Class<?> clz) throws IOException {
        if (clz == null) {
            return JSON.parse(data, getReaderContext());
        } else {
            return JSON.parseObject(data, clz, getReaderContext());
        }
    }

    @Override
    public Object deserializeBody(Context ctx) throws IOException {
        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return JSON.parse(data, getReaderContext());
        } else {
            return null;
        }
    }
}