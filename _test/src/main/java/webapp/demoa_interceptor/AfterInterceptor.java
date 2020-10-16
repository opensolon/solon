package webapp.demoa_interceptor;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;


@XController
public class AfterInterceptor {
    @XMapping(value = "/demoa/**", index = 1, after = true)
    public void call(XContext context, String sev) {
        context.output("XInterceptor_aft::你被我拦截了(/demoa/**)!!!\n");
    }
}
