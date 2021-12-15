package features;

import com.google.gson.JsonPrimitive;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.serialization.gson.GsonRenderFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2021/10/12 created
 */
@Controller
public class TestApp {
    public static void main(String[] args) {
        Solon.start(TestApp.class, args, app -> {
            initMvcJsonCustom();
        });
    }

    /**
     * 初始化json定制（需要在插件运行前定制）
     */
    private static void initMvcJsonCustom() {
        //通过转换器，做简单类型的定制
        GsonRenderFactory.global
                .addConvertor(Date.class, s -> s.getTime());

        GsonRenderFactory.global
                .addConvertor(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        GsonRenderFactory.global
                .addConvertor(LocalDateTime.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));


        GsonRenderFactory.global
                .addEncoder(Date.class, (source, type, jsc) -> {
                    return new JsonPrimitive(source.getTime());
                });

    }

    @Mapping("/")
    public Object home() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("time1", LocalDateTime.now());
        data.put("time2", LocalDate.now());
        data.put("time3", new Date());

        return data;
    }
}
