package webapp;

import org.noear.nami.NamiFilter;
import org.noear.nami.NamiInvocation;
import org.noear.nami.common.Result;
import org.noear.solon.annotation.Component;

/**
 * @author noear 2021/6/6 created
 */
@Component
public class NamiFilterImpl implements NamiFilter {
    @Override
    public Result doFilter(NamiInvocation inv) throws Throwable {
        System.out.println("====NamiInterceptorImpl");
        return inv.invoke();
    }
}
