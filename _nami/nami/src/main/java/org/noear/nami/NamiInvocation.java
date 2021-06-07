package org.noear.nami;

import org.noear.nami.common.Result;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Nami - 调用者
 *
 * @author noear
 * @since 1.4
 */
public class NamiInvocation extends NamiContext {
    private List<NamiFilter> filters = new ArrayList<>();
    private int index;

    public NamiInvocation(NamiConfig config, Method method, String action, String url, NamiFilter actuator) {
        super(config, method, action, url);
        this.filters.addAll(config.getFilters());
        this.filters.add(actuator);
        this.index = 0;
    }

    /**
     * 调用
     * */
    public Result invoke() throws Throwable {
        return filters.get(index++).doFilter(this);
    }
}
