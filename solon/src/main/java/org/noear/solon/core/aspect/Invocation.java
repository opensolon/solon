package org.noear.solon.core.aspect;

import org.noear.solon.core.wrap.MethodHolder;

import java.util.List;

/**
 * 调用者
 *
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

    /**
     * 目标对象
     * */
    public Object target() {
        return target;
    }

    /**
     * 参数
     * */
    public Object[] args() {
        return args;
    }

    /**
     * 函数
     * */
    public MethodHolder method() {
        return method;
    }

    /**
     * 调用
     * */
    public Object invoke() throws Throwable {
        return interceptors.get(interceptorIndex++).doIntercept(this);
    }
}
