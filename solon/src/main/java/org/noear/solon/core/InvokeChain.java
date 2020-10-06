package org.noear.solon.core;


public interface InvokeChain {
    Object doInvoke(Object obj, Object[] args) throws Throwable;
}
