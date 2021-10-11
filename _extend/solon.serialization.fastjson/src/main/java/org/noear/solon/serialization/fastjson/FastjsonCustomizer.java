package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import org.noear.solon.serialization.JsonCustomizer;
import org.noear.solon.serialization.NumberConverter;
import org.noear.solon.serialization.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;


/**
 * @author noear 2021/10/11 created
 */
public abstract class FastjsonCustomizer implements JsonCustomizer {
    protected abstract SerializeConfig config();

    public <T> void addEncoder(Class<T> clz, ObjectSerializer encoder) {
        config().put(clz, encoder);
    }

    @Override
    public void addDateConvertor(StringConverter<Date> converter) {
        addEncoder(Date.class, (ser, obj, fieldName, fieldType, features) -> {
            SerializeWriter out = ser.getWriter();
            out.writeString(converter.convert((Date) obj));
        });
    }

    @Override
    public void addDateConvertor(NumberConverter<Date> converter) {
        addEncoder(Date.class, (ser, obj, fieldName, fieldType, features) -> {
            SerializeWriter out = ser.getWriter();
            out.writeLong(converter.convert((Date) obj).longValue());
        });
    }

    @Override
    public void addLocalDateConvertor(StringConverter<LocalDate> converter) {
        addEncoder(LocalDate.class, (ser, obj, fieldName, fieldType, features) -> {
            SerializeWriter out = ser.getWriter();
            out.writeString(converter.convert((LocalDate) obj));
        });
    }

    @Override
    public void addLocalDateConvertor(NumberConverter<LocalDate> converter) {
        addEncoder(LocalDate.class, (ser, obj, fieldName, fieldType, features) -> {
            SerializeWriter out = ser.getWriter();
            out.writeLong(converter.convert((LocalDate) obj).longValue());
        });
    }

    @Override
    public void addLocalTimeConvertor(StringConverter<LocalTime> converter) {
        addEncoder(LocalTime.class, (ser, obj, fieldName, fieldType, features) -> {
            SerializeWriter out = ser.getWriter();
            out.writeString(converter.convert((LocalTime) obj));
        });
    }

    @Override
    public void addLocalTimeConvertor(NumberConverter<LocalTime> converter) {
        addEncoder(LocalTime.class, (ser, obj, fieldName, fieldType, features) -> {
            SerializeWriter out = ser.getWriter();
            out.writeLong(converter.convert((LocalTime) obj).longValue());
        });
    }

    @Override
    public void addLocalDateTimeConvertor(StringConverter<LocalDateTime> converter) {
        addEncoder(LocalDateTime.class, (ser, obj, fieldName, fieldType, features) -> {
            SerializeWriter out = ser.getWriter();
            out.writeString(converter.convert((LocalDateTime) obj));
        });
    }

    @Override
    public void addLocalDateTimeConvertor(NumberConverter<LocalDateTime> converter) {
        addEncoder(LocalDateTime.class, (ser, obj, fieldName, fieldType, features) -> {
            SerializeWriter out = ser.getWriter();
            out.writeLong(converter.convert((LocalDateTime) obj).longValue());
        });
    }
}
