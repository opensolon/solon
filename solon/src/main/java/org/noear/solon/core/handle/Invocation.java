package org.noear.solon.core.handle;

import org.noear.solon.core.wrap.MethodHolder;

/**
 * @author noear
 * @since 1.3
 */
public class Invocation {
    private final Object target;
    private final Object[] args;
    private final MethodHolder method;
    protected InterceptorChain next;

    public Invocation(Object target, Object[] args, MethodHolder method){
        this.target = target;
        this.args = args;
        this.method = method;
    }

    public Object target(){
        return target;
    }
    public Object[] args(){
        return args;
    }
    public MethodHolder method(){
        return method;
    }

    public Object invoke() throws Throwable{
        return next.doIntercept(this);
    }
}
