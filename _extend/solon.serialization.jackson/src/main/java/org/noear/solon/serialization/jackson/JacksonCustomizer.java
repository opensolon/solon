package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.noear.solon.serialization.JsonCustomizer;
import org.noear.solon.serialization.NumberConverter;
import org.noear.solon.serialization.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author noear 2021/10/11 created
 */
public abstract class JacksonCustomizer implements JsonCustomizer {

    protected abstract ObjectMapper config();

    protected  SimpleModule module;


    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        if (module == null) {
            module = new SimpleModule();
            config().registerModule(module);
        }

        module.addSerializer(clz, encoder);
    }

    @Override
    public void addDateConvertor(StringConverter<Date> converter) {
        addEncoder(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date source, JsonGenerator gen, SerializerProvider sp) throws IOException {
                gen.writeString(converter.convert(source));
            }
        });
    }

    @Override
    public void addDateConvertor(NumberConverter<Date> converter) {
        addEncoder(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date source, JsonGenerator gen, SerializerProvider sp) throws IOException {
                gen.writeNumber(converter.convert(source).longValue());
            }
        });
    }

    @Override
    public void addLocalDateConvertor(StringConverter<LocalDate> converter) {
        addEncoder(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate source, JsonGenerator gen, SerializerProvider sp) throws IOException {
                gen.writeString(converter.convert(source));
            }
        });
    }

    @Override
    public void addLocalDateConvertor(NumberConverter<LocalDate> converter) {
        addEncoder(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate source, JsonGenerator gen, SerializerProvider sp) throws IOException {
                gen.writeNumber(converter.convert(source).longValue());
            }
        });
    }

    @Override
    public void addLocalTimeConvertor(StringConverter<LocalTime> converter) {
        addEncoder(LocalTime.class, new JsonSerializer<LocalTime>() {
            @Override
            public void serialize(LocalTime source, JsonGenerator gen, SerializerProvider sp) throws IOException {
                gen.writeString(converter.convert(source));
            }
        });
    }

    @Override
    public void addLocalTimeConvertor(NumberConverter<LocalTime> converter) {
        addEncoder(LocalTime.class, new JsonSerializer<LocalTime>() {
            @Override
            public void serialize(LocalTime source, JsonGenerator gen, SerializerProvider sp) throws IOException {
                gen.writeNumber(converter.convert(source).longValue());
            }
        });
    }

    @Override
    public void addLocalDateTimeConvertor(StringConverter<LocalDateTime> converter) {
        addEncoder(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime source, JsonGenerator gen, SerializerProvider sp) throws IOException {
                gen.writeString(converter.convert(source));
            }
        });
    }

    @Override
    public void addLocalDateTimeConvertor(NumberConverter<LocalDateTime> converter) {
        addEncoder(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime source, JsonGenerator gen, SerializerProvider sp) throws IOException {
                gen.writeNumber(converter.convert(source).longValue());
            }
        });
    }
}
