/*
 * Copyright 2017-2024 noear.org and authors
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

import org.noear.solon.Utils;
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
     * @deprecated 2.9
     */
    @Deprecated
    public static boolean ifMissing(Condition anno) {
        return ifMissingBean(anno);
    }

    /**
     * 是否有 onMissingBean 条件
     */
    public static boolean ifMissingBean(Condition anno) {
        if (anno == null) {
            return false;
        } else {
            try {
                return (anno.onMissingBean() != Void.class) || Utils.isNotEmpty(anno.onMissingBeanName());
            } catch (Throwable e) {
                //如果 onMissingBean 的类是不存在的，会出错
                return true;
            }
        }
    }

    /**
     * 是否有 OnBean 条件
     */
    public static boolean ifBean(Condition anno) {
        if (anno == null) {
            return false;
        } else {
            try {
                return (anno.onBean() != Void.class) || Utils.isNotEmpty(anno.onBeanName());
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
                if (Utils.isNotEmpty(anno.onBeanName())) {
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

        if (Utils.isNotEmpty(anno.onClassName())) {
            if (ClassUtil.loadClass(context.getClassLoader(), anno.onClassName()) == null) {
                //如果null，表示不存在类
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

        try {
            if (anno.onMissingBean() != Void.class) {
                if (context.hasWrap(anno.onMissingBean())) {
                    return true;
                }
            }
        } catch (Throwable e) {
            //如果 onMissingBean 的类是不存在的，会异常 //异常跳过，不用管
        }

        if (Utils.isNotEmpty(anno.onMissingBeanName())) {
            if (context.hasWrap(anno.onMissingBeanName())) {
                return true;
            }
        }

        return false;
    }
}