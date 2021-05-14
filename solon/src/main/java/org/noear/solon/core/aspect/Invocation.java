package org.noear.solon.core.aspect;

import org.noear.solon.core.wrap.MethodHolder;

import java.util.List;

/**
 * @author noear
 * @since 1.3
 */
public class Invocation {
    private final Object target;
    private final Object[] args;
    private final MethodHolder method;
    private final List<InterceptorEntity> interceptors;
    private int interceptorIndex = 0;

    public Invocation(Object target, Object[] args, MethodHolder method, List<InterceptorEntity> interceptors) {
        this.target = target;
        this.args = args;
        this.method = method;
        this.interceptors = interceptors;
    }

    public Object target() {
        return target;
    }

    public Object[] args() {
        return args;
    }

    public MethodHolder method() {
        return method;
    }

    public Object invoke() throws Throwable {
        Object tmp = interceptors.get(interceptorIndex).interceptor.doIntercept(this);
        interceptorIndex++;
        return tmp;
    }
}
