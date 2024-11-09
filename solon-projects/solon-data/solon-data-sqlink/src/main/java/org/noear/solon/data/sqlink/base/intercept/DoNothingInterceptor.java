package org.noear.solon.data.sqlink.base.intercept;

public class DoNothingInterceptor<T> extends Interceptor<T> {

    public static DoNothingInterceptor<?> Instance = new DoNothingInterceptor<>();

    @Override
    public T doIntercept(T value) {
        return value;
    }
}
