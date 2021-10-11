package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import org.noear.solon.serialization.JsonRenderFactory;
import org.noear.solon.serialization.JsonLongConverter;
import org.noear.solon.serialization.JsonStringConverter;



/**
 * @author noear 2021/10/11 created
 */
public abstract class FastjsonRenderFactoryBase implements JsonRenderFactory {
    protected abstract SerializeConfig config();

    public <T> void addEncoder(Class<T> clz, ObjectSerializer encoder) {
        config().put(clz, encoder);
    }

    @Override
    public <T> void addConvertor(Class<T> clz, JsonLongConverter<T> converter) {
        addEncoder(clz, (ser, obj, fieldName, fieldType, features) -> {
            SerializeWriter out = ser.getWriter();
            out.writeLong(converter.convert((T) obj));
        });
    }

    @Override
    public <T> void addConvertor(Class<T> clz, JsonStringConverter<T> converter) {
        addEncoder(clz, (ser, obj, fieldName, fieldType, features) -> {
            SerializeWriter out = ser.getWriter();
            out.writeString(converter.convert((T) obj));
        });
    }
}
