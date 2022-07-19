package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

/**
 *
 * @author noear
 * @since 1.3
 * */
public class NotBlacklistValidator implements Validator<NotBlacklist> {
    public static final NotBlacklistValidator instance = new NotBlacklistValidator();
    private NotBlacklistChecker checker = (anno, ctx) -> false;

    public void setChecker(NotBlacklistChecker checker) {
        if (checker != null) {
            this.checker = checker;
        }
    }

    @Override
    public String message(NotBlacklist anno) {
        return anno.message();
    }

    @Override
    public Class<?>[] groups(NotBlacklist anno) {
        return anno.groups();
    }

    @Override
    public Result validateOfContext(Context ctx, NotBlacklist anno, String name, StringBuilder tmp) {
        if (checker.check(anno, ctx)) {
            return Result.succeed();
        } else {
            return Result.failure(403);
        }
    }
}
