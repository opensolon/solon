package org.noear.solon.core;


/**
 * 方法拦截调用链（用于支持 @XAround ）
 *
 * @author noear
 * @since 1.0
 * */
public interface XInterceptorChain {
    Object doInvoke(Object obj, Object[] args) throws Throwable;

    class Entity implements XInterceptorChain {
        public final int index;
        public final XInterceptor handler;
        public XInterceptorChain next;
        private MethodWrap mw;

        Entity(MethodWrap m, int i, XInterceptor h) {
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
