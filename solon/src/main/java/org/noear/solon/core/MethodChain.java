package org.noear.solon.core;


/**
 * 方法拦截调用链（用于支持 @XAround ）
 *
 * @author noear
 * @since 1.0
 * */
public interface MethodChain {
    Object doInvoke(Object obj, Object[] args) throws Throwable;

    class Entity implements MethodChain {
        private final MethodInterceptor handler;
        private final MethodWrap mw;

        public final int index;
        public MethodChain next;

        Entity(MethodWrap m, int i, MethodInterceptor h) {
            index = i;
            handler = h;
            mw = m;
        }

        @Override
        public Object doInvoke(Object obj, Object[] args) throws Throwable {
            return handler.doIntercept(obj, mw, args, next);
        }
    }
}
