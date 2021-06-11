package org.noear.solon.extend.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.validation.Validator;

import java.io.IOException;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class NoRepeatSubmitValidator implements Validator<NoRepeatSubmit> {
    public static final NoRepeatSubmitValidator instance = new NoRepeatSubmitValidator();

    @Override
    public String message(NoRepeatSubmit anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, NoRepeatSubmit anno, String name, StringBuilder tmp) {
        tmp.append(ctx.path()).append("::");

        for (HttpPart part : anno.value()) {
            switch (part) {
                case body: {
                    try {
                        tmp.append("body:");
                        tmp.append(ctx.body()).append(";");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
                case headers: {
                    tmp.append("headers:");
                    ctx.headerMap().forEach((k, v) -> {
                        tmp.append(k).append("=").append(v).append(";");
                    });
                    break;
                }
                default: {
                    tmp.append("params:");
                    ctx.paramMap().forEach((k, v) -> {
                        tmp.append(k).append("=").append(v).append(";");
                    });
                    break;
                }
            }
        }

        if (NoRepeatLockImp.global().tryLock(Utils.md5(tmp.toString()), anno.seconds())) {
            return Result.succeed();
        } else {
            return Result.failure();
        }
    }
}
