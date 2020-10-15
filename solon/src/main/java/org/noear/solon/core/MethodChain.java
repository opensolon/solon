package org.noear.solon.core;


public interface MethodChain {
    Object doInvoke(Object obj, Object[] args) throws Throwable;

    static class Entity implements MethodChain {
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
