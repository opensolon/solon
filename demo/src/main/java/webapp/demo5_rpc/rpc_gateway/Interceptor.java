package webapp.demo5_rpc.rpc_gateway;

import org.noear.solon.annotation.XBefore;
import org.noear.solon.annotation.XInterceptor;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

@XBefore({ Interceptor.ApiIntercepter.class, Interceptor.AuthInterceptor.class})
@XInterceptor(before = true)
public class Interceptor {
    @XMapping(value = "/{sev}/**")
    public void call(XContext context, String sev){

    }

    //demo 省事儿，直接发写这儿
    public static class ApiIntercepter implements XHandler {
        @Override
        public void handle(XContext context) throws Exception {

        }
    }

    //demo 省事儿，直接发写这儿
    public static class AuthInterceptor implements XHandler {
        @Override
        public void handle(XContext context) throws Exception {

        }
    }
}
