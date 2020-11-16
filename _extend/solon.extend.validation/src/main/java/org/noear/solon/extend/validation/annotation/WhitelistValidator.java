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

    @Override
    public String message(Whitelist anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, Whitelist anno, String name, StringBuilder tmp) {
        if (WhitelistCheckerImp.global().check(anno, ctx)) {
            return Result.succeed();
        } else {
            return Result.failure();
        }
    }
}
