package org.noear.solon.extend.validation.annotation;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0.23
 * */
public class NoRepeatSubmitValidator implements Validator<NoRepeatSubmit> {
    public static final NoRepeatSubmitValidator instance = new NoRepeatSubmitValidator();

    @Override
    public String message(NoRepeatSubmit anno) {
        return anno.message();
    }

    @Override
    public XResult validate(XContext ctx, NoRepeatSubmit anno, String name, StringBuilder tmp) {
        tmp.append(ctx.path()).append("::");

        for (HttpPart part : anno.value()) {
            switch (part) {
                case body: {
                    try {
                        tmp.append("body:");
                        tmp.append(ctx.body()).append(";");
                    } catch (Exception ex) {
                        XUtil.throwTr(ex);
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

        if (NoRepeatLockImp.global().tryLock(XUtil.md5(tmp.toString()), anno.seconds())) {
            return XResult.succeed();
        } else {
            return XResult.failure();
        }
    }
}
