package demo;

import org.noear.nami.annotation.Mapping;
import org.noear.solon.annotation.Controller;
import org.noear.solon.cloud.opentracing.TracingUtil;

/**
 * @author noear 2022/5/7 created
 */
@Controller
public class DemoService {
    @Mapping("hello")
    public String hello() {
        TracingUtil.activeSpan(span -> {
            span.setTag("订单", 12);
        });

        return "hello world";
    }
}
