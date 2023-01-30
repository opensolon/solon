package webapp.demoa_interceptor;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;


//@Controller
public class BeforeInterceptor {
    public static class Before1 implements Handler{

        @Override
        public void handle(Context ctx) throws Throwable {
            ctx.output("XInterceptor1::你被我拦截了(/demoa/**)!!!\n");
        }
    }

    public static class Before2 implements Handler{

        @Override
        public void handle(Context ctx) throws Throwable {
            ctx.output("XInterceptor2::你被我拦截了(/demoa/**)!!!\n");
        }
    }

    public static class Before3 implements Handler{

        @Override
        public void handle(Context ctx) throws Throwable {
            ctx.output("XInterceptor3::你被我拦截了(/demoa/**)!!!\n");
        }
    }

    //@Mapping(value = "/demoa/**",index = 1, endpoint = Endpoint.before)
    public void call(Context context, String sev) {
        context.output("XInterceptor1::你被我拦截了(/demoa/**)!!!\n");
    }

    //@Mapping(value = "/demoa/**",index = 3, endpoint = Endpoint.before)
    public void call2(Context context, String sev) {
        context.output("XInterceptor3::你被我拦截了(/demoa/**)!!!\n");
    }

    //@Mapping(value = "/demoa/**",index = 2, endpoint = Endpoint.before)
    public void call3(Context context, String sev) {
        context.output("XInterceptor2::你被我拦截了(/demoa/**)!!!\n");
    }

}
