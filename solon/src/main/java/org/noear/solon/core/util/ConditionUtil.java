package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Condition;
import org.noear.solon.core.AopContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

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
            return test(context, element, anno);
        }
    }

    private static boolean test(AopContext context, AnnotatedElement element, Condition anno) {
        if (Utils.isNotEmpty(anno.hasClassName())) {
            return Utils.loadClass(context.getClassLoader(), anno.hasClassName()) != null;
        }

        if (Utils.isNotEmpty(anno.hasProperty())) {
            String[] kv = anno.hasProperty().split("=");

            if (kv.length != 2) {
                String fullname;
                if (element instanceof Method) {
                    Method method = (Method) element;
                    fullname = method.getDeclaringClass().getName() + "::" + method.getName();
                } else {
                    Class<?> clz = (Class<?>) element;
                    fullname = clz.getName();
                }
                throw new IllegalArgumentException("@Condition hasProperty resolution failed: " + fullname);
            }

            String val = context.cfg().getByParse(kv[0].trim());

            //val 可能为 null
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
