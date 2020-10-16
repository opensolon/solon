package webapp.demo5_rpc.rpc_gateway;

import org.noear.solon.annotation.XAfter;
import org.noear.solon.annotation.XBefore;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

@XAfter({Interceptor.ApiIntercepter.class})
@XBefore({ Interceptor.AuthInterceptor.class})
@XController
public class Interceptor {

    @XMapping(value = "/demo52/**",index = 1, before = true)
    public void call(XContext context, String sev) {
        context.output("XInterceptor1，你被我拦截了(/{sev}/**)!!!\n");
    }

    @XMapping(value = "/demo52/**",index = 3, before = true)
    public void call2(XContext context, String sev) {
        context.output("XInterceptor3，你被我拦截了(/{sev}/**)!!!\n");
    }

    @XMapping(value = "/demo52/**",index = 2, before = true)
    public void call3(XContext context, String sev) {
        context.output("XInterceptor2，你被我拦截了(/{sev}/**)!!!\n");
    }

    //demo 省事儿，直接发写这儿
    public static class ApiIntercepter implements XHandler {
        @Override
        public void handle(XContext context) throws Throwable {
            context.output("XInterceptor of XBefore:API\n");
        }
    }

    //demo 省事儿，直接发写这儿
    public static class AuthInterceptor implements XHandler {
        @Override
        public void handle(XContext context) throws Throwable {
            context.output("XInterceptor of XAfter:Auth\n");
        }
    }
}
