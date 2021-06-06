package org.noear.nami;

import org.noear.nami.common.Result;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 1.4
 */
public class Invocation extends NamiContext {
    Interceptor actuator;
    List<Interceptor> interceptors = new ArrayList<>();
    int index;

    public Invocation(NamiConfig config, Method method, String action, Interceptor actuator) {
        super(config, method, action);
        this.actuator = actuator;
        this.interceptors.addAll(config.getInterceptors());
        this.interceptors.add(actuator);
        this.index = 0;
    }

    public Result invoke() throws Throwable {
        return interceptors.get(index++).doIntercept(this);
    }
}
