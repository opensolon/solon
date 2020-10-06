package org.noear.solon.core;

class InvokeHolder implements InvokeChain {
    public final int index;
    public final InvokeHandler handler;
    public InvokeChain next;
    private MethodWrap method;

    InvokeHolder(MethodWrap m, int i, InvokeHandler h) {
        index = i;
        handler = h;
        method = m;
    }

    @Override
    public Object doInvoke(Object obj, Object[] args) throws Throwable {
        return handler.doInvoke(obj, method.getMethod(), method.getParameters(), args, next);
    }
}
