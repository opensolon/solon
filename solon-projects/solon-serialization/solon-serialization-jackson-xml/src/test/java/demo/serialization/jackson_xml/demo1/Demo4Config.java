package demo.serialization.jackson_xml.demo1;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.jackson.xml.JacksonXmlStringSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author noear 2025/9/13 created
 */
@Configuration
public class Demo4Config {
    @Bean
    public void config(JacksonXmlStringSerializer serializer) {
        //方式1：通过转换器，做简单类型的定制
        serializer.addEncoder(Date.class, s -> s.getTime());

        serializer.addEncoder(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        serializer.addEncoder(Long.class, s -> String.valueOf(s));

        serializer.addEncoder(BigDecimal.class, s -> s.toPlainString());

        //方式2：通过编码器，做复杂类型的原生定制（基于框架原生接口）
        serializer.addEncoder(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date date, JsonGenerator out, SerializerProvider sp) throws IOException {
                out.writeNumber(date.getTime());
            }
        });

        //
        serializer.getDeserializeConfig().setMapper(new XmlMapper());
    }
}
