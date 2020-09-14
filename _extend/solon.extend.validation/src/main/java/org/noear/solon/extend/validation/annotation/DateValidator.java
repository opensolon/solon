package org.noear.solon.extend.validation.annotation;

import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XResult;
import org.noear.solon.extend.validation.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public XResult validate(XContext ctx, Date anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);
        if (val == null || tryParse(anno, val) == false) {
            tmp.append(',').append(name);
        }

        if (tmp.length() > 1) {
            return XResult.failure(tmp.substring(1));
        } else {
            return XResult.succeed();
        }
    }

    private boolean tryParse(Date anno, String val) {
        try {
            if (XUtil.isEmpty(anno.value())) {
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
