package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.validation.Validator;

/**
 *
 * @author noear
 * @since 1.3
 * */
public class NotBlacklistValidator implements Validator<NotBlacklist> {
    public static final NotBlacklistValidator instance = new NotBlacklistValidator();
    private static NotBlacklistChecker checker = (anno, ctx) -> false;

    public static void setChecker(NotBlacklistChecker checker) {
        if (checker != null) {
            NotBlacklistValidator.checker = checker;
        }
    }

    @Override
    public String message(NotBlacklist anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, NotBlacklist anno, String name, StringBuilder tmp) {
        if (checker.check(anno, ctx)) {
            return Result.succeed();
        } else {
            return Result.failure(403);
        }
    }
}
