package webapp.demoa_interceptor;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;


@Controller
public class BeforeInterceptor {

    @Mapping(value = "/demoa/**",index = 1, before = true)
    public void call(Context context, String sev) {
        context.output("XInterceptor1::你被我拦截了(/demoa/**)!!!\n");
    }

    @Mapping(value = "/demoa/**",index = 3, before = true)
    public void call2(Context context, String sev) {
        context.output("XInterceptor3::你被我拦截了(/demoa/**)!!!\n");
    }

    @Mapping(value = "/demoa/**",index = 2, before = true)
    public void call3(Context context, String sev) {
        context.output("XInterceptor2::你被我拦截了(/demoa/**)!!!\n");
    }

}
