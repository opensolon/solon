package demo2;

import com.alibaba.fastjson.serializer.SerializeWriter;
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
            initMvcJsonCustom();
        });
    }

    /**
     * 初始化json定制（需要在插件运行前定制）
     */
    private static void initMvcJsonCustom() {
        //demo: 通过转换器，做简单类型的定制
        FastjsonRenderFactory.global
                .addConvertor(Date.class, s -> s.getTime());

        FastjsonRenderFactory.global
                .addConvertor(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        //demo: 通过编码器，做复杂类型的原生定制（基于框架原生接口）
        FastjsonRenderFactory.global
                .addEncoder(Date.class, (ser, obj, o1, type, i) -> {
                    SerializeWriter out = ser.getWriter();
                    out.writeLong(((Date) obj).getTime());
                });
    }
}
