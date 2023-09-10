package demo;

import org.noear.dami.bus.Interceptor;
import org.noear.dami.bus.InterceptorChain;
import org.noear.dami.bus.Payload;
import org.noear.solon.annotation.Component;

/**
 * @author noear 2023/9/10 created
 */
@Component
public class ApiInterceptor implements Interceptor {
    @Override
    public void doIntercept(Payload payload, InterceptorChain chain) {
        System.out.println("拦截：" + payload.toString());
        chain.doIntercept(payload);
    }
}
