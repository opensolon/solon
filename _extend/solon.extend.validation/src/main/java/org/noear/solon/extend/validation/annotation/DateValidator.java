package org.noear.solon.extend.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.validation.Validator;

import java.time.format.DateTimeFormatter;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class DateValidator implements Validator<Date> {
    public static final DateValidator instance = new DateValidator();


    @Override
    public String message(Date anno) {
        return anno.message();
    }

    @Override
    public Result validate(Context ctx, Date anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);
        if (val == null || tryParse(anno, val) == false) {
            tmp.append(',').append(name);
        }

        if (tmp.length() > 1) {
            return Result.failure(tmp.substring(1));
        } else {
            return Result.succeed();
        }
    }

    private boolean tryParse(Date anno, String val) {
        try {
            if (Utils.isEmpty(anno.value())) {
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(val);
            } else {
                DateTimeFormatter.ofPattern(anno.value()).parse(val);
            }

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
