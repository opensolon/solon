package demo.serialization.gson;

import com.google.gson.JsonPrimitive;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.gson.GsonStringSerializer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author noear 2025/9/13 created
 */
@Configuration
public class Demo4Config {
    @Bean
    public void config(GsonStringSerializer serializer) throws Exception {
        //方式1：通过转换器，做简单类型的定制
        serializer.addEncoder(Date.class, s -> s.getTime());

        serializer.addEncoder(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        serializer.addEncoder(Long.class, s -> String.valueOf(s));

        //方式2：通过编码器，做复杂类型的原生定制（基于框架原生接口）
        serializer.addEncoder(Date.class, (source, type, jsc) -> {
            return new JsonPrimitive(source.getTime());
        });
    }
}