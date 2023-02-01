package webapp.demoa_interceptor;

import org.noear.solon.annotation.After;
import org.noear.solon.annotation.Before;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

@After(AfterInterceptor.class)
@Before({BeforeInterceptor.Before1.class, BeforeInterceptor.Before2.class, BeforeInterceptor.Before3.class})
@Mapping("/demoa/trigger")
@Controller
public class demoa_handler  {
    @Mapping
    public void handle(Context context) throws Throwable {
        context.output(context.path());
    }
}
