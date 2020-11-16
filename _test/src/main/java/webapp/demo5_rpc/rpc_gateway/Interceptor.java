package webapp.demo5_rpc.rpc_gateway;

import org.noear.solon.annotation.After;
import org.noear.solon.annotation.Before;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

@After({Interceptor.ApiIntercepter.class})
@Before({ Interceptor.AuthInterceptor.class})
@Controller
public class Interceptor {

    @Mapping(value = "/demo52/**",index = 1, before = true)
    public void call(Context context, String sev) {
        context.output("XInterceptor1，你被我拦截了(/{sev}/**)!!!\n");
    }

    @Mapping(value = "/demo52/**",index = 3, before = true)
    public void call2(Context context, String sev) {
        context.output("XInterceptor3，你被我拦截了(/{sev}/**)!!!\n");
    }

    @Mapping(value = "/demo52/**",index = 2, before = true)
    public void call3(Context context, String sev) {
        context.output("XInterceptor2，你被我拦截了(/{sev}/**)!!!\n");
    }

    //demo 省事儿，直接发写这儿
    public static class ApiIntercepter implements Handler {
        @Override
        public void handle(Context context) throws Throwable {
            context.output("XInterceptor of XBefore:API\n");
        }
    }

    //demo 省事儿，直接发写这儿
    public static class AuthInterceptor implements Handler {
        @Override
        public void handle(Context context) throws Throwable {
            context.output("XInterceptor of XAfter:Auth\n");
        }
    }
}
