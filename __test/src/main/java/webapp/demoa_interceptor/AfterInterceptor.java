package webapp.demoa_interceptor;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Endpoint;


@Controller
public class AfterInterceptor {
    @Mapping(value = "/demoa/**", index = 1, endpoint = Endpoint.after)
    public void call(Context context, String sev) {
        context.output("XInterceptor_aft::你被我拦截了(/demoa/**)!!!\n");
    }
}
