package org.noear.solon.validation.annotation;

/**
 * 防止重复提交锁
 * 
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface NoRepeatLock {
    boolean tryLock(String key, int seconds);
}

