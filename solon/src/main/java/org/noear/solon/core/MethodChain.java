package org.noear.solon.core;


/**
 * 方法执行链
 *
 * @author noear
 * @since 1.0
 * */
public interface MethodChain {
    Object doInvoke(Object obj, Object[] args) throws Throwable;

    class Entity implements MethodChain {
        public final int index;
        public final MethodHandler handler;
        public MethodChain next;
        private MethodWrap mw;

        Entity(MethodWrap m, int i, MethodHandler h) {
            index = i;
            handler = h;
            mw = m;
        }

        @Override
        public Object doInvoke(Object obj, Object[] args) throws Throwable {
            return handler.doInvoke(obj, mw.getMethod(), mw.getParameters(), args, next);
        }
    }

}
