package org.noear.solon.core;


/**
 * 方法拦截调用链（用于支持 @XAround ）
 *
 * @author noear
 * @since 1.1
 * */
public interface XInterceptorChain {
    MethodHolder method();
    Object doIntercept(Object target, Object[] args) throws Throwable;

    class Entity implements XInterceptorChain {
        private final XInterceptor handler;
        private final MethodWrap methodWrap;

        public final int index;
        public XInterceptorChain next;

        Entity(MethodWrap m, int i, XInterceptor h) {
            index = i;
            handler = h;
            methodWrap = m;
        }

        @Override
        public MethodHolder method(){
            return methodWrap;
        }

        @Override
        public Object doIntercept(Object target, Object[] args) throws Throwable {
            return handler.doIntercept(target, args, next);
        }
    }
}
