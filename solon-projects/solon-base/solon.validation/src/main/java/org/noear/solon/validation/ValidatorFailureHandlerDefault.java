package org.noear.solon.validation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.util.FormatUtils;

import java.lang.annotation.Annotation;

/**
 * 验证失败处理默认实现
 *
 * @author noear
 * @since 1.3
 * */
class ValidatorFailureHandlerDefault implements ValidatorFailureHandler {

    @Override
    public boolean onFailure(Context ctx, Annotation anno, Result rst, String msg) throws Throwable {
        msg = FormatUtils.format(anno, rst, msg);

        throw new ValidatorException(rst.getCode(), msg, anno, rst);
    }
}
