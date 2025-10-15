package demo.serialization.snack4;

import org.noear.snack4.Feature;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.snack4.Snack4StringSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author noear 2025/9/13 created
 *
 */
@Configuration
public class Demo4Config {
    @Bean
    public void config(Snack4StringSerializer serializer) {
        //::序列化（用于渲染输出）
        //示例1：通过转换器，做简单类型的定制（addConvertor 新统一为 addEncoder）
        serializer.addEncoder(Date.class, s -> s.getTime());

        serializer.addEncoder(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        serializer.addEncoder(Double.class, s -> String.valueOf(s));

        serializer.addEncoder(BigDecimal.class, s -> s.toPlainString());

        //示例2：通过编码器，做复杂类型的原生定制（基于框架原生接口）
        serializer.addEncoder(Date.class, (c,v, node) -> node.setValue(v.getTime()));

        //示例3：调整特性（例，添加枚举序列化为名字的特性）
        serializer.getSerializeConfig().addFeatures(Feature.Write_EnumUsingName);  //增加特性
        serializer.getSerializeConfig().addFeatures(Feature.Write_OnlyUseSetter);  //增加特性 //只使用 getter 属性做序列化输出
        serializer.getSerializeConfig().removeFeatures(Feature.Write_BrowserCompatible); //移除特性

        serializer.getSerializeConfig().setFeatures(Feature.Write_BrowserCompatible); //重设特性

        //::反序列化（用于接收参数）
        //示例4：“反序列化”添加特性
        serializer.getDeserializeConfig().addFeatures(Feature.Write_EnumUsingName);
    }
}
