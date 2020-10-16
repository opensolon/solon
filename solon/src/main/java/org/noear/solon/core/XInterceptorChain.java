package org.noear.solon.core;


import org.noear.solon.core.wrap.MethodWrap;

/**
 * 方法拦截调用链（用于支持 @XAround ）
 *
 * @author noear
 * @since 1.0
 * */
public interface XInterceptorChain {
    /**
     * 方法容器
     * */
    MethodHolder method();

    /**
     * 拦截传递
     *
     * @param target 目标对象
     * @param args 参数
     * */
    Object doIntercept(Object target, Object[] args) throws Throwable;


    class Entity implements XInterceptorChain {
        private final XInterceptor handler;
        private final MethodWrap methodWrap;

        public final int index;
        public XInterceptorChain next;

        public Entity(MethodWrap m, int i, XInterceptor h) {
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
