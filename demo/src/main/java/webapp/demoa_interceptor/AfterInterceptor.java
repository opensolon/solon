package webapp.demoa_interceptor;

import org.noear.solon.annotation.XInterceptor;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;


@XInterceptor(after = true)
public class AfterInterceptor {

    @XMapping(value = "/demoa/**",index = 1)
    public void call(XContext context, String sev) {
        context.output("XInterceptor_aft::你被我拦截了(/demoa/**)!!!\n");
    }

}
