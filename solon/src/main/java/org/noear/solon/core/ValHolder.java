package org.noear.solon.core;

import java.util.concurrent.CompletableFuture;

/**
 * 值容器
 * */
public class ValHolder<T> {
    /**
     * 值
     * */
    public T value;

    public CompletableFuture<T> future;
}
