package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import org.noear.solon.serialization.JsonRenderFactory;
import org.noear.solon.serialization.JsonConverter;

import java.math.BigDecimal;


/**
 * @author noear 2021/10/11 created
 */
public abstract class FastjsonRenderFactoryBase implements JsonRenderFactory {
    protected abstract SerializeConfig config();

    public <T> void addEncoder(Class<T> clz, ObjectSerializer encoder) {
        config().put(clz, encoder);
    }


    @Override
    public <T> void addConvertor(Class<T> clz, JsonConverter<T> converter) {
        addEncoder(clz, (ser, obj, fieldName, fieldType, features) -> {
            Object val = converter.convert((T) obj);

            SerializeWriter out = ser.getWriter();

            if (val == null) {
                out.writeNull();
            } else if (val instanceof String) {
                out.writeString((String) val);
            } else if (val instanceof Number) {
                if (val instanceof Integer || val instanceof Long) {
                    out.writeLong(((Number) val).longValue());
                } else {
                    out.writeDouble(((Number) val).doubleValue(), false);
                }
            } else {
                throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
            }
        });
    }
}
