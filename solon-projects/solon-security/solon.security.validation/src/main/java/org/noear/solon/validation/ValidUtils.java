package org.noear.solon.validation;

import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.util.FormatUtils;

import java.lang.annotation.Annotation;

/**
 * 验证工具
 *
 * @author noear
 * @since 2.9
 */
public class ValidUtils {
    /**
     * 验证请求上下文
     *
     * @param ctx 请求上下文
     */
    public static void validateContext(Context ctx) throws Throwable {
        if (ctx.getHandled()) {
            return;
        }

        Action a = ctx.action();

        if (a != null) {
            ValidatorManager.validateOfContext(ctx, a);
        }
    }

    /**
     * 验证调用链
     *
     * @param inv 调用连
     */
    public static void validateInvocation(Invocation inv) throws ValidatorException {
        //可能是 web 或 com
        try {
            ValidatorManager.validateOfInvocation(inv);
        } catch (ValidatorException e) {
            throw new ValidatorException(e.getCode(), e.getMessage(), e.getAnnotation(), e.getResult(), inv.method().getMethod());
        } catch (Throwable e) {
            throw new ValidatorException(400, e.getMessage(), null, Result.failure(), inv.method().getMethod());
        }
    }

    /**
     * 验证实体
     *
     * @param obj    实体
     * @param groups 验证组
     */
    public static void validateEntity(Object obj, Class<?>... groups) throws ValidatorException {
        Result rst = ValidatorManager.validateOfEntity(obj, groups);

        if (rst.getCode() == Result.FAILURE_CODE) {
            String message = null;
            Annotation anno = null;
            String label = obj.getClass().getName();

            if (Utils.isEmpty(rst.getDescription())) {
                rst.setDescription(label);
            }

            if (rst.getData() instanceof BeanValidateInfo) {
                BeanValidateInfo info = (BeanValidateInfo) rst.getData();
                anno = info.anno;
                message = info.message;
            }

            String msg = FormatUtils.format(anno, rst, message);
            throw new ValidatorException(rst.getCode(), msg, null, rst);
        }
    }
}
