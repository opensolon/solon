package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Condition;
import org.noear.solon.core.AopContext;

import java.lang.reflect.AnnotatedElement;

/**
 * @author noear
 * @since 2.0
 */
public class ConditionUtil {
    public static boolean test(AopContext context, AnnotatedElement element) {
        Condition anno = element.getAnnotation(Condition.class);

        if (anno == null) {
            return true;
        } else {
            return testNo(context, anno) == false;
        }
    }

    private static boolean testNo(AopContext context, Condition anno) {
        if (Utils.isNotEmpty(anno.hasClassName())) {
            if (Utils.loadClass(context.getClassLoader(), anno.hasClassName()) == null) {
                return true;
            }
        }

        if (Utils.isNotEmpty(anno.hasProperty())) {
            String[] kv = anno.hasProperty().split("=");

            if (kv.length > 1) {
                String val = context.cfg().getByExpr(kv[0].trim());
                //值要等于kv[1] （val 可能为 null）
                if (kv[1].trim().equals(val) == false) {
                    return true;
                }
            } else {
                String val = context.cfg().getByExpr(anno.hasProperty());
                //有值就行
                if (Utils.isNotEmpty(val) == false) {
                    return true;
                }
            }
        }

        try {
            anno.hasClass();
        } catch (Throwable e) {
            return true;
        }

        if (anno.missingBean() != Void.class) {
            return context.hasWrap(anno.missingBean());
        }

        return false;
    }
}
