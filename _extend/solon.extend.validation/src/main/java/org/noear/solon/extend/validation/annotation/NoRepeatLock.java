package org.noear.solon.extend.validation.annotation;

/**
 *
 * @author noear
 * @since 1.0.24
 * */
@FunctionalInterface
public interface NoRepeatLock {
    boolean tryLock(String key, int seconds);
}

