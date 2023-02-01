package webapp.demoa_interceptor;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;


//@Controller
public class AfterInterceptor implements Handler {
    //@Mapping(value = "/demoa/**", index = 1, endpoint = Endpoint.after)
//    public void call(Context context, String sev) {
//
//    }

    @Override
    public void handle(Context ctx) throws Throwable {
        ctx.output("XInterceptor_aft::你被我拦截了(/demoa/**)!!!\n");
    }
}
