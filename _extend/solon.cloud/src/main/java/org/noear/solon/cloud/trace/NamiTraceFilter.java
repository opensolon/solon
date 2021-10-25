package org.noear.solon.cloud.trace;

import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.NamiManager;
import org.noear.nami.Result;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Instance;

/**
 * Nami 跟踪过滤器
 *
 * @author noear
 * @since 1.5
 */
public class NamiTraceFilter implements Filter {
    /**
     * 注册过滤器
     * */
    public static void register() {
        NamiManager.reg(new NamiTraceFilter());
    }

    @Override
    public Result doFilter(Invocation inv) throws Throwable {
        inv.headers.put(CloudClient.trace().HEADER_TRACE_ID_NAME(), CloudClient.trace().getTraceId());
        inv.headers.put(CloudClient.trace().HEADER_FROM_ID_NAME(), Instance.local().serviceAndAddress());
        return inv.invoke();
    }
}
