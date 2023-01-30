package webapp.demoa_interceptor;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Endpoint;


@Controller
public class BeforeInterceptor {

    @Mapping(value = "/demoa/**",index = 1, endpoint = Endpoint.before)
    public void call(Context context, String sev) {
        context.output("XInterceptor1::你被我拦截了(/demoa/**)!!!\n");
    }

    @Mapping(value = "/demoa/**",index = 3, endpoint = Endpoint.before)
    public void call2(Context context, String sev) {
        context.output("XInterceptor3::你被我拦截了(/demoa/**)!!!\n");
    }

    @Mapping(value = "/demoa/**",index = 2, endpoint = Endpoint.before)
    public void call3(Context context, String sev) {
        context.output("XInterceptor2::你被我拦截了(/demoa/**)!!!\n");
    }

}
