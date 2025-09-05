package demo.nami;

import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.Result;
import org.noear.solon.annotation.Component;

/**
 *
 * @author noear 2025/9/5 created
 *
 */
@Component
public class NamiFilterImpl implements org.noear.nami.Filter {
    @Override
    public Result doFilter(Invocation inv) throws Throwable {
        inv.headers.put("xxx","xxx");

        return inv.invoke();
    }
}
