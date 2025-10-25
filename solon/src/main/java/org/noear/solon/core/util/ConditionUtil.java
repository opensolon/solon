/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.util;

import org.noear.solon.annotation.Condition;
import org.noear.solon.core.AppContext;

import java.lang.reflect.AnnotatedElement;

/**
 * 条件检测工具
 *
 * @author noear
 * @since 2.0
 */
public class ConditionUtil {
    /**
     * 是否有 onMissingBean 条件
     *
     * @since 2.9
     */
    public static boolean ifMissingBean(Condition anno) {
        if (anno == null) {
            return false;
        } else {
            try {
                return (anno.onMissingBean() != Void.class) || Assert.isNotEmpty(anno.onMissingBeanName());
            } catch (Throwable e) {
                //如果 onMissingBean 的类是不存在的，会出错
                return true;
            }
        }
    }

    /**
     * 是否有 OnBean 条件
     *
     * @since 2.9
     */
    public static boolean ifBean(Condition anno) {
        if (anno == null) {
            return false;
        } else {
            try {
                return (anno.onBean() != Void.class) || Assert.isNotEmpty(anno.onBeanName());
            } catch (Throwable e) {
                //如果 onBean 的类是不存在的，会出错
                return true;
            }
        }
    }

    public static void onBeanRun(Condition anno, AppContext context, RunnableEx runnable) {
        if (anno.onBean() != Void.class) {
            //onBean
            context.getBeanAsync(anno.onBean(), bean -> {
                if (Assert.isNotEmpty(anno.onBeanName())) {
                    //onBeanName
                    context.getBeanAsync(anno.onBeanName(), bean2 -> {
                        RunUtil.runOrThrow(() -> runnable.run());
                    });
                } else {
                    RunUtil.runOrThrow(() -> runnable.run());
                }
            });
        } else {
            //onBeanName
            context.getBeanAsync(anno.onBeanName(), bean -> {
                RunUtil.runOrThrow(() -> runnable.run());
            });
        }
    }

    /**
     * 检测条件
     */
    public static boolean test(AppContext context, AnnotatedElement element) {
        Condition anno = element.getAnnotation(Condition.class);

        return test(context, anno);
    }

    /**
     * 检测条件
     */
    public static boolean test(AppContext context, Condition anno) {
        if (anno == null) {
            return true;
        } else {
            return testNo(context, anno) == false;
        }
    }

    private static boolean testNo(AppContext context, Condition anno) {
        try {
            anno.onClass();
        } catch (Throwable e) {
            //异常，表示不存在类
            return true;
        }

        if (Assert.isNotEmpty(anno.onClassName())) {
            if (ClassUtil.loadClass(context.getClassLoader(), anno.onClassName()) == null) {
                //如果null，表示不存在类
                return true;
            }
        }

        //@deprecated 3.6
        if (Assert.isNotEmpty(anno.onProperty())) {
            String[] exprs = anno.onProperty().split("&&");
            for (String expr : exprs) {
                if (testPropertyNo(context, expr.trim())) {
                    return true;
                }
            }
        }

        if (Assert.isNotEmpty(anno.onExpression())) {
            if (testExpression(context, anno.onExpression().trim()) == false) {
                return true;
            }
        }

        try {
            if (anno.onMissingBean() != Void.class) {
                if (context.hasWrap(anno.onMissingBean())) {
                    return true;
                }
            }
        } catch (Throwable e) {
            //如果 onMissingBean 的类是不存在的，会异常 //异常跳过，不用管
        }

        if (Assert.isNotEmpty(anno.onMissingBeanName())) {
            if (context.hasWrap(anno.onMissingBeanName())) {
                return true;
            }
        }

        return false;
    }

    private static boolean testExpression(AppContext context, String expr) {
        Object val = SnelUtil.eval(expr, context.cfg());

        if (val instanceof Boolean) {
            return (Boolean) val;
        }

        if (val instanceof String) {
            //如果是字符串，有值就行
            return Assert.isNotEmpty((String) val);
        }

        //其它，非 null 就行
        return val != null;
    }

    /**
     * @deprecated 3.6
     * */
    @Deprecated
    private static boolean testPropertyNo(AppContext context, String expr) {
        int kIdx = expr.indexOf('=');

        if (kIdx < 0) {
            String val = context.cfg().getByExpr(expr);
            //有值就行
            if (Assert.isNotEmpty(val) == false) {
                return true;
            }
        } else {
            String left, right;
            boolean isNeq = false;

            if (expr.charAt(kIdx + 1) == '=') {
                //说明是 '=='
                left = expr.substring(0, kIdx).trim();
                kIdx++;
                right = expr.substring(kIdx + 1, expr.length()).trim();
            } else if (expr.charAt(kIdx - 1) == '!') {
                //说明是 '!='
                isNeq = true;
                right = expr.substring(kIdx + 1, expr.length()).trim();
                kIdx--;
                left = expr.substring(0, kIdx).trim();
            } else {
                //说明是 '='
                left = expr.substring(0, kIdx).trim();
                right = expr.substring(kIdx + 1, expr.length()).trim();
            }

            String leftV = context.cfg().getByExpr(left);

            if (isNeq) {
                //!=
                if (right.equals(leftV)) {
                    return true;
                }
            } else {
                //==
                if (right.equals(leftV) == false) {
                    return true;
                }
            }
        }

        return false;
    }
}