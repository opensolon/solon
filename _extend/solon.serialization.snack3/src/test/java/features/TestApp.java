package features;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.serialization.snack3.SnackRenderFactory;

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
        SnackRenderFactory.global
                .addConvertor(Date.class, s -> s.getTime());

        SnackRenderFactory.global
                .addConvertor(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        SnackRenderFactory.global
                .addConvertor(LocalDateTime.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

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
