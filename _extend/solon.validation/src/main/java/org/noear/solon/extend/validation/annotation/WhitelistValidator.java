package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class WhitelistValidator implements Validator<Whitelist> {
    public static final WhitelistValidator instance = new WhitelistValidator();

    private static WhitelistChecker checker = (anno, ctx) -> false;

    public static void setChecker(WhitelistChecker checker) {
        if (checker != null) {
            WhitelistValidator.checker = checker;
        }
    }

    @Override
    public String message(Whitelist anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, Whitelist anno, String name, StringBuilder tmp) {
        if (checker.check(anno, ctx)) {
            return Result.succeed();
        } else {
            return Result.failure(403);
        }
    }
}
