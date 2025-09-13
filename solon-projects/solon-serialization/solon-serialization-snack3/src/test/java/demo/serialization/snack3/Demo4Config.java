package demo.serialization.snack3;

import org.noear.snack.core.Feature;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.snack3.SnackStringSerializer;

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
    public void config(SnackStringSerializer serializer) {
        //示例1：通过转换器，做简单类型的定制
        serializer.addEncoder(Date.class, s -> s.getTime());

        serializer.addEncoder(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        serializer.addEncoder(Double.class, s -> String.valueOf(s));

        serializer.addEncoder(BigDecimal.class, s -> s.toPlainString());

        //示例2：通过编码器，做复杂类型的原生定制（基于框架原生接口）
        serializer.addEncoder(Date.class, (data, node) -> node.val().setNumber(data.getTime()));

        //示例3：调整特性（例，添加枚举序列化为名字的特性）
        serializer.getSerializeConfig().addFeatures(Feature.EnumUsingName);  //增加特性
        serializer.getSerializeConfig().addFeatures(Feature.UseOnlyGetter);  //增加特性 //只使用 getter 属性做序列化输出
        serializer.getSerializeConfig().removeFeatures(Feature.BrowserCompatible); //移除特性

        serializer.getSerializeConfig().setFeatures(Feature.BrowserCompatible); //重设特性


        //serializer.getDeserializeConfig();
    }
}
