package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

public class WhitelistValidator implements Validator<Whitelist> {
    public static final WhitelistValidator instance = new WhitelistValidator();

    @Override
    public String message(Whitelist anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, Whitelist anno, StringBuilder tmp) {
        if (WhitelistCheckerImp.global().check(anno, ctx)) {
            return XResult.succeed();
        } else {
            return XResult.failure();
        }
    }
}
