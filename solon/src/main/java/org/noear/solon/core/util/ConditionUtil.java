package org.noear.solon.core.util;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Condition;
import org.noear.solon.core.AopContext;

import java.lang.reflect.AnnotatedElement;

/**
 * 条件检测工具
 *
 * @author noear
 * @since 2.0
 */
public class ConditionUtil {
    /**
     * 检测条件
     * */
    public static boolean test(AopContext context, AnnotatedElement element) {
        Condition anno = element.getAnnotation(Condition.class);

        return test(context, anno);
    }

    public static boolean test(AopContext context, Condition anno){
        if (anno == null) {
            return true;
        } else {
            return testNo(context, anno) == false;
        }
    }

    public static boolean ifMissing(Condition anno) {
        if (anno == null) {
            return false;
        } else {
            return (anno.onMissingBean() != Void.class) || Utils.isNotEmpty(anno.onMissingBeanName());
        }
    }

    private static boolean testNo(AopContext context, Condition anno) {
        try {
            anno.onClass();
        } catch (Throwable e) {
            return true;
        }

        if (Utils.isNotEmpty(anno.onClassName())) {
            if (ClassUtil.loadClass(context.getClassLoader(), anno.onClassName()) == null) {
                return true;
            }
        }

        if (Utils.isNotEmpty(anno.onProperty())) {
            String[] kv = anno.onProperty().split("=");

            if (kv.length > 1) {
                String val = context.cfg().getByExpr(kv[0].trim());
                //值要等于kv[1] （val 可能为 null）
                if (kv[1].trim().equals(val) == false) {
                    return true;
                }
            } else {
                String val = context.cfg().getByExpr(anno.onProperty());
                //有值就行
                if (Utils.isNotEmpty(val) == false) {
                    return true;
                }
            }
        }

        if (anno.onMissingBean() != Void.class) {
            if (context.hasWrap(anno.onMissingBean())) {
                return true;
            }
        }

        if (Utils.isNotEmpty(anno.onMissingBeanName())) {
            if (context.hasWrap(anno.onMissingBeanName())) {
                return true;
            }
        }

        return false;
    }
}
