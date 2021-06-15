package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class WhitelistValidator implements Validator<Whitelist> {
    public static final WhitelistValidator instance = new WhitelistValidator();

    private WhitelistChecker checker = (anno, ctx) -> false;

    public void setChecker(WhitelistChecker checker) {
        if (checker != null) {
            this.checker = checker;
        }
    }

    @Override
    public String message(Whitelist anno) {
        return anno.message();
    }

    @Override
    public Result validateOfContext(Context ctx, Whitelist anno, String name, StringBuilder tmp) {
        if (checker.check(anno, ctx)) {
            return Result.succeed();
        } else {
            return Result.failure(403);
        }
    }
}
