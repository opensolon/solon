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

import org.noear.solon.Utils;

import java.util.Properties;

/**
 * 属性模板处理工具
 *
 * @author noear
 * @since 2.5
 */
public class PropUtil {
    /**
     * 表达式拆分（拆成 name, def）
     *
     * @param expr 兼容 ${key} or key or ${key:def} or key:def
     * @return [name, def]
     */
    public static String[] expSplit(String expr) {
        //如果有表达式，去掉符号
        if (expr.startsWith("${") && expr.endsWith("}")) {
            expr = expr.substring(2, expr.length() - 1);
        }

        //如果有默认值，则获取
        String def = null;
        int defIdx = expr.indexOf(':');
        if (defIdx > 0) {
            if (expr.length() > defIdx + 1) {
                def = expr.substring(defIdx + 1).trim();
            } else {
                def = "";
            }
            expr = expr.substring(0, defIdx).trim();
        }

        return new String[]{expr, def};
    }

    /**
     * 根据表达式获取配置值
     *
     * @param expr 兼容 ${key} or key or ${key:def} or key:def
     */
    public static String getByExp(Properties main, Properties target, String expr, String refKey) {
        return getByExp(main, target, expr, refKey, true);
    }

    /**
     * 根据表达式获取配置值
     *
     * @param expr   兼容 ${key} or key or ${key:def} or key:def
     * @param useDef 是否使用默认值
     */
    public static String getByExp(Properties main, Properties target, String expr, String refKey, boolean useDef) {
        String[] nameAndDef = expSplit(expr);

        String name = nameAndDef[0];
        if (Utils.isEmpty(name)) {
            return nameAndDef[1];
        }

        if (name.indexOf('.') == 0 && refKey != null) {
            //本级引用
            int refIdx = refKey.lastIndexOf('.');
            if (refIdx > 0) {
                name = refKey.substring(0, refIdx) + name;
            }
        }

        String val = null;

        if (target != null) {
            //从"目标属性"获取
            val = target.getProperty(name);
        }

        if (val == null) {
            //从"主属性"获取
            val = main.getProperty(name);

            if (val == null) {
                //从"环镜变量"获取
                val = System.getenv(name);
            }
        }

        if (val == null) {
            if (useDef) {
                return nameAndDef[1];
            } else {
                return null;
            }
        } else {
            return val;
        }
    }


    /**
     * 根据模板获取配置值
     *
     * @param tml 模板： ${key} 或 aaa${key}bbb 或 ${key:def}/ccc
     */
    public static String getByTml(Properties main, Properties target, String tml, String refKey) {
        return getByTml(main, target, tml, refKey, true);
    }

    /**
     * 根据模板获取配置值
     *
     * @param tml    模板： ${key} 或 aaa${key}bbb 或 ${key:def}/ccc
     * @param useDef 是否使用默认值
     */
    public static String getByTml(Properties main, Properties target, String tml, String refKey, boolean useDef) {
        if (Utils.isEmpty(tml)) {
            return tml;
        }

        int start = 0, end = 0;
        while (true) {
            start = tml.indexOf("${");

            if (start < 0) {
                return tml;
            } else {
                end = tml.indexOf('}', start);

                if (end < 0) {
                    throw new IllegalStateException("Invalid template expression: " + tml);
                }

                String valueExp = tml.substring(start + 2, end); //key:def
                //支持默认值表达式 ${key:def}
                String value = getByExp(main, target, valueExp, refKey, useDef);

                if (value == null) {
                    return null;
                }

                tml = tml.substring(0, start) + value + tml.substring(end + 1);
            }
        }
    }
}