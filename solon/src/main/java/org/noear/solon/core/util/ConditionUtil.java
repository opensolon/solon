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
            return test(context, anno);
        }
    }

    private static boolean test(AopContext context, Condition anno) {
        if (Utils.isNotEmpty(anno.hasClassName())) {
            return Utils.loadClass(context.getClassLoader(), anno.hasClassName()) != null;
        }

        if (Utils.isNotEmpty(anno.hasProperty())) {
            String[] kv = anno.hasProperty().split("=");

            if (kv.length > 1) {
                String key = kv[0].trim();
                if (key.endsWith("!")) { //支持 != 和 =
                    key = key.substring(0, key.length() - 1).trim();
                }

                //val 可能为 null
                String val = context.cfg().getByExpr(key);

                if (anno.hasProperty().contains("!=")) {
                    return kv[1].trim().equals(val) == false;
                } else {
                    return kv[1].trim().equals(val);
                }
            } else {
                String val = context.cfg().getByExpr(anno.hasProperty());
                //有值就行
                return Utils.isNotEmpty(val);
            }
        }

        try {
            anno.hasClass();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}
