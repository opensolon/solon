package webapp.demoa_interceptor;

import org.noear.solon.annotation.XAfter;
import org.noear.solon.annotation.XBefore;
import org.noear.solon.annotation.XInterceptor;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;


@XInterceptor(before = true)
public class BeforeInterceptor {

    @XMapping(value = "/demoa/**",index = 1)
    public void call(XContext context, String sev) {
        context.output("XInterceptor1::你被我拦截了(/demoa/**)!!!\n");
    }

    @XMapping(value = "/demoa/**",index = 3)
    public void call2(XContext context, String sev) {
        context.output("XInterceptor3::你被我拦截了(/demoa/**)!!!\n");
    }

    @XMapping(value = "/demoa/**",index = 2)
    public void call3(XContext context, String sev) {
        context.output("XInterceptor2::你被我拦截了(/demoa/**)!!!\n");
    }

}
