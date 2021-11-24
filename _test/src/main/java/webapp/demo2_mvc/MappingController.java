package webapp.demo2_mvc;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.core.handle.Context;

@Singleton(false)
@Mapping("/demo2/mapping")
@Controller
public class MappingController {
    //支持与父XMapping叠国路径
    @Mapping("a")
    public String test_a(Context context) {
        return context.path();
    }

    //支持*一段路径匹配
    @Mapping("b/*")
    public String test_b(Context context) {
        return context.path();
    }

    //支持**不限长度匹配
    @Mapping("c/**")
    public String test_c(Context context) {
        return context.path();
    }

    //支持特征路径匹配1
    @Mapping("d1/**/$*")
    public String test_d1(Context context) {
        return context.path();
    }

    //支持特征路径匹配2
    @Mapping("d1/**/@*")
    public String test_d2(Context context) {
        return context.path();
    }

    //支持path var匹配
    @Mapping("e/{p_q}/{obj}/{id}")
    public String test_e(Context context, String p_q, String obj, String id) {
        return context.path() + ":" + p_q + "-" + obj + "-" + id;
    }
}
