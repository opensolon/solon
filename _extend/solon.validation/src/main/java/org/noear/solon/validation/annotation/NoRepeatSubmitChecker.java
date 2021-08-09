package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;

/**
 * 防止重复提交锁
 * 
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface NoRepeatSubmitChecker {
    /**
     * @param anno 注解
     * @param ctx 上下文
     * @param submitHash 提交内容的哈希
     * @param limitSeconds 限制秒数
     * */
    boolean check(NoRepeatSubmit anno, Context ctx,  String submitHash, int limitSeconds);
}

