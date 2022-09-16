package demo2;

import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.Solon;
import org.noear.solon.serialization.fastjson.FastjsonRenderFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author noear 2021/10/12 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args, app -> {
            app.onEvent(FastjsonRenderFactory.class, e -> {
                initMvcJsonCustom(e);
            });
        });
    }

    //初始化json定制（需要在插件运行前定制）
    private static void initMvcJsonCustom(FastjsonRenderFactory factory) {
        //示例1：通过转换器，做简单类型的定制
        factory.addConvertor(Date.class, s -> s.getTime());

        factory.addConvertor(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        factory.addConvertor(Long.class, s -> String.valueOf(s));

        //示例2：通过编码器，做复杂类型的原生定制（基于框架原生接口）
        factory.addEncoder(Date.class, (ser, obj, o1, type, i) -> {
            SerializeWriter out = ser.getWriter();
            out.writeLong(((Date) obj).getTime());
        });

        //示例3：重置序列化特性（例，添加序列化null的特性）
        factory.setFeatures(SerializerFeature.BrowserCompatible,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteMapNullValue);

        //factory.config().addFilter();
    }
}
