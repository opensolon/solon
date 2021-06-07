package org.noear.nami;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Nami - 调用者
 *
 * @author noear
 * @since 1.4
 */
public class Invocation extends NamiContext {
    private List<Filter> filters = new ArrayList<>();
    private int index;

    public Invocation(Config config, Method method, String action, String url, Filter actuator) {
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
