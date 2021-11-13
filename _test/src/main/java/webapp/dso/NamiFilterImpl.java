package webapp.dso;

import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.Result;
import org.noear.solon.annotation.Component;

/**
 * @author noear 2021/6/6 created
 */
@Component
public class NamiFilterImpl implements Filter {
    @Override
    public Result doFilter(Invocation inv) throws Throwable {
        System.out.println("====NamiInterceptorImpl");
        return inv.invoke();
    }
}
