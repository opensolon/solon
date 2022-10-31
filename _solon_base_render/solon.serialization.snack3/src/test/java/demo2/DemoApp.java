package demo2;

import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.Solon;
import org.noear.solon.serialization.JsonConverter;
import org.noear.solon.serialization.snack3.SnackRenderFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author noear 2021/10/12 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args, app -> {
            app.onEvent(SnackRenderFactory.class, e -> {
                initMvcJsonCustom(e);
            });
        });
    }

    //初始化json定制（需要在插件运行前定制）
    private static void initMvcJsonCustom(SnackRenderFactory factory) {
        //示例1：通过转换器，做简单类型的定制
        factory.addConvertor(Date.class, s -> s.getTime());

        factory.addConvertor(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        factory.addConvertor(Double.class, s -> String.valueOf(s));

        //示例2：通过编码器，做复杂类型的原生定制（基于框架原生接口）
        factory.addEncoder(Date.class, (data, node) -> node.val().setNumber(data.getTime()));

        //示例3：重置序列化特性（例，添加序列化null的特性）
        factory.setFeatures(
                Feature.OrderedField,
                Feature.WriteDateUseTicks,
                Feature.TransferCompatible,
                Feature.StringNullAsEmpty,
                Feature.QuoteFieldNames,
                Feature.SerializeNulls);

        //factory.config().add()
    }
}
