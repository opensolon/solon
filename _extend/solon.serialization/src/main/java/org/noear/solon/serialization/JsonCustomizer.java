package org.noear.solon.serialization;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * Json 定制器
 *
 * @author noear
 * @since 1.5
 */
public interface JsonCustomizer {
    void addDateConvertor(StringConverter<Date> converter);

    void addDateConvertor(NumberConverter<Date> converter);

    void addLocalDateConvertor(StringConverter<LocalDate> converter);

    void addLocalDateConvertor(NumberConverter<LocalDate> converter);

    void addLocalTimeConvertor(StringConverter<LocalTime> converter);

    void addLocalTimeConvertor(NumberConverter<LocalTime> converter);

    void addLocalDateTimeConvertor(StringConverter<LocalDateTime> converter);

    void addLocalDateTimeConvertor(NumberConverter<LocalDateTime> converter);
}
