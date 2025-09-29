package demo.serialization.fastjson2;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.fastjson2.Fastjson2StringSerializer;

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
    public void config(Fastjson2StringSerializer serializer) {
        //::序列化（用于渲染输出）
        //示例1：通过转换器，做简单类型的定制（addConvertor 新统一为 addEncoder）
        serializer.addEncoder(Date.class, s -> s.getTime());

        serializer.addEncoder(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        serializer.addEncoder(Double.class, s -> String.valueOf(s));

        serializer.addEncoder(BigDecimal.class, s -> s.toPlainString());

        //示例2：通过编码器，做复杂类型的原生定制（基于框架原生接口）
        serializer.addEncoder(Date.class, (out, obj, o1, type, i) -> {
            out.writeInt64(((Date) obj).getTime());
        });

        //示例3：重置序列化特性（例，添加序列化null的特性）
        serializer.getSerializeConfig().addFeatures(JSONWriter.Feature.WriteMapNullValue); //添加特性
        serializer.getSerializeConfig().removeFeatures(JSONWriter.Feature.BrowserCompatible); //移除特性
        serializer.getSerializeConfig().setFeatures(JSONWriter.Feature.BrowserCompatible); //重设特性

        //::反序列化（用于接收参数）
        //示例4：“反序列化”添加特性
        serializer.getDeserializeConfig().addFeatures(JSONReader.Feature.Base64StringAsByteArray);
    }
}