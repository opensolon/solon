package org.noear.solon.data.sqlink.base.intercept;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Interceptor<T> {
    public abstract T doIntercept(T value);

    private static final Map<Class<? extends Interceptor<?>>, Interceptor<?>> cache = new ConcurrentHashMap<>();

    public static <T> Interceptor<T> get(Class<? extends Interceptor<T>> c) {
        Interceptor<?> generator = cache.get(c);
        if (generator == null) {
            try {
                generator = c.newInstance();
                cache.put(c, generator);
            }
            catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return (Interceptor<T>) generator;
    }
}
