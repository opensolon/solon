package org.noear.solon.ext;

@FunctionalInterface
public interface ExConsumer<T>  {
    void accept(T t) throws Throwable;
}
