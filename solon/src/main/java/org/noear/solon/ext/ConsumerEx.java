package org.noear.solon.ext;

@FunctionalInterface
public interface ConsumerEx<T>  {
    void accept(T t) throws Throwable;
}
