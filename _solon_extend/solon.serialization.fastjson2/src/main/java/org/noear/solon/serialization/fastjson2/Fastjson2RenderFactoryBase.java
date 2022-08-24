package org.noear.solon.serialization.fastjson2;


import com.alibaba.fastjson2.writer.ObjectWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.serialization.JsonConverter;
import org.noear.solon.serialization.JsonRenderFactory;

/**
 * Json 渲染器工厂基类
 *
 * @author noear
 * @since 1.9
 */
public abstract class Fastjson2RenderFactoryBase implements JsonRenderFactory {

    public abstract ObjectWriterProvider config();

    public <T> void addEncoder(Class<T> clz, ObjectWriter encoder) {
        config().register(clz, encoder);
    }


    @Override
    public <T> void addConvertor(Class<T> clz, JsonConverter<T> converter) {
        addEncoder(clz, (out, obj, fieldName, fieldType, features) -> {
            Object val = converter.convert((T) obj);
            if (val == null) {
                out.writeNull();
            } else if (val instanceof String) {
                out.writeString((String) val);
            } else if (val instanceof Number) {
                if (val instanceof Integer || val instanceof Long) {
                    out.writeInt64(((Number) val).longValue());
                } else {
                    out.writeDouble(((Number) val).doubleValue());
                }
            } else {
                throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
            }
        });
    }
}
