package demo;

import org.noear.nami.annotation.Mapping;
import org.noear.solon.annotation.Controller;
import org.noear.solon.cloud.tracing.Spans;

/**
 * @author noear 2022/5/7 created
 */
@Controller
public class DemoService {
    @Mapping("hello")
    public String hello() {
        Spans.active(span -> span.setTag("订单", 12));
        //或
        Spans.active().setTag("订单", 12);

        return "hello world";
    }
}
