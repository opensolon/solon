package org.noear.solon.extend.validation;

import org.noear.solon.annotation.Note;
import org.noear.solon.core.handler.Context;
import org.noear.solon.core.handler.Result;

import java.lang.annotation.Annotation;

/**
 * 失败处理者
 *
 * @author noear
 * @since 1.0
 * */
public interface ValidatorFailureHandler {
    /**
     * @return 是否停止后续检查器
     */
    @Note("@return 是否停止后续检查器")
    boolean onFailure(Context ctx, Annotation ano, Result result, String message);
}
