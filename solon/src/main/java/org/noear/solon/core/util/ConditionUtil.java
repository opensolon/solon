package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Condition;
import org.noear.solon.core.AopContext;

/**
 * @author noear
 * @since 2.0
 */
public class ConditionUtil {
    public static boolean test(AopContext context, Condition anno) {
        if (Utils.isNotEmpty(anno.hasClassName())) {
            return Utils.loadClass(context.getClassLoader(), anno.hasClassName()) != null;
        }

        if (Utils.isNotEmpty(anno.hasProperty())) {
            String[] kv = anno.hasProperty().split("=");
            if (kv.length != 2) {
                throw new IllegalArgumentException("Condition hasProperty resolution failed: " + anno.hasProperty());
            }

            String val = context.cfg().getByParse(kv[0].trim());
            return kv[1].trim().equals(val);
        }

        try {
            anno.hasClass();
            return true;
        } catch (TypeNotPresentException e) {
            return false;
        }
    }
}
