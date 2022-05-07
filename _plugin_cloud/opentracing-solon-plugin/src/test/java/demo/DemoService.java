package demo;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.noear.nami.annotation.Mapping;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2022/5/7 created
 */
@Controller
public class DemoService {
    @Inject
    Tracer tracer;

    @Mapping("hello")
    public String hello() {
        Span span = tracer.activeSpan();
        span.setTag("订单", "12");


        return "hello world";
    }
}
