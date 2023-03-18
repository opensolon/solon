package org.noear.solon.validation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;

/**
 * 验证器失败处理者
 *
 * @author noear
 * @since 1.0
 * */
public interface ValidatorFailureHandler {
    /**
     * @return 是否停止后续检查器
     * @see ValidatorManager::validateOfContext
     */
    boolean onFailure(Context ctx, Annotation ano, Result result, String message) throws Throwable;
}
