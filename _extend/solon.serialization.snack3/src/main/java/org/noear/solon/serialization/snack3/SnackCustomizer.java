package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Constants;
import org.noear.snack.core.NodeEncoder;
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
public abstract class SnackCustomizer implements JsonCustomizer {

    protected abstract Constants config();

    /**
     * 添加编码器
     * */
    public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
        config().addEncoder(clz, encoder);
    }

    @Override
    public void addDateConvertor(StringConverter<Date> converter) {
        addEncoder(Date.class, (source, target) -> {
            target.val().setString(converter.convert(source));
        });
    }

    @Override
    public void addDateConvertor(NumberConverter<Date> converter) {
        addEncoder(Date.class, (source, target) -> {
            target.val().setNumber(converter.convert(source));
        });
    }

    @Override
    public void addLocalDateConvertor(StringConverter<LocalDate> converter) {
        addEncoder(LocalDate.class, (source, target) -> {
            target.val().setString(converter.convert(source));
        });
    }

    @Override
    public void addLocalDateConvertor(NumberConverter<LocalDate> converter) {
        addEncoder(LocalDate.class, (source, target) -> {
            target.val().setNumber(converter.convert(source));
        });
    }

    @Override
    public void addLocalTimeConvertor(StringConverter<LocalTime> converter) {
        addEncoder(LocalTime.class, (source, target) -> {
            target.val().setString(converter.convert(source));
        });
    }

    @Override
    public void addLocalTimeConvertor(NumberConverter<LocalTime> converter) {
        addEncoder(LocalTime.class, (source, target) -> {
            target.val().setNumber(converter.convert(source));
        });
    }

    @Override
    public void addLocalDateTimeConvertor(StringConverter<LocalDateTime> converter) {
        addEncoder(LocalDateTime.class, (source, target) -> {
            target.val().setString(converter.convert(source));
        });
    }

    @Override
    public void addLocalDateTimeConvertor(NumberConverter<LocalDateTime> converter) {
        addEncoder(LocalDateTime.class, (source, target) -> {
            target.val().setNumber(converter.convert(source));
        });
    }
}
