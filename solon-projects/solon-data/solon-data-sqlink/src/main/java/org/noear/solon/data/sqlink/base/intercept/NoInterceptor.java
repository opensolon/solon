package org.noear.solon.data.sqlink.base.intercept;

public class NoInterceptor extends Interceptor<Void> {
    @Override
    public Void doIntercept(Void value) {
        return value;
    }
}
