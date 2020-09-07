package org.noear.solon.extend.validation.annotation;

@FunctionalInterface
public interface NoRepeatLock {
    boolean tryLock(String key, int seconds);
}

