package webapp.demo5_rpc;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Gateway;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Result;

/**
 * @author noear 2023/6/13 created
 */
@Mapping("demo53/api2/**")
@Component
public class ApiGateway extends Gateway {
    @Override
    protected void register() {
        add(ApiDemo.class);
    }

    @Override
    public void render(Object obj, Context c) throws Throwable {
        if (c.getRendered()) {
            return;
        } else {
            c.setRendered(true);
        }

        if (obj instanceof ModelAndView) {
            c.result = obj;
        } else {
            if (obj instanceof Result) {
                c.result = obj;
            } else {
                c.result = Result.succeed(obj);
            }
        }

        c.render(c.result);
    }
}
