package org.noear.solon.core.util;

import org.noear.solon.Utils;

import java.util.Properties;
import java.util.function.BiConsumer;

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
        int defIdx = expr.indexOf(":");
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
    public static String getByExp(Properties main, Properties target, String expr) {
        return getByExp(main, target, expr, null);
    }

    /**
     * 根据表达式获取配置值
     *
     * @param expr 兼容 ${key} or key or ${key:def} or key:def
     */
    public static String getByExp(Properties main, Properties target, String expr, BiConsumer<String, String> c) {
        String[] nameAndDef = expSplit(expr);

        String name = nameAndDef[0], def = nameAndDef[1];

        String val = null;

        if (target != null) {
            //从"目标属性"获取
            val = target.getProperty(name);
        }

        if (val == null) {
            //从"主属性"获取
            val = main.getProperty(name);

            if (val == null && Character.isUpperCase(name.charAt(0))) {
                //从"环镜变量"获取
                val = System.getenv(name);
            }
        }

        if (val == null) {
            if (c != null) {
                c.accept(name, def);
            }
            return def;
        } else {
            return val;
        }
    }



    /**
     * 根据模板获取配置值
     *
     * @param tml 模板： ${key} 或 aaa${key}bbb 或 ${key:def}/ccc
     */
    public static String getByTml(Properties main, Properties target, String tml) {
        return getByTml(main, target, tml, null);
    }

    /**
     * 根据模板获取配置值
     *
     * @param tml 模板： ${key} 或 aaa${key}bbb 或 ${key:def}/ccc
     */
    public static String getByTml(Properties main, Properties target, String tml, BiConsumer<String, String> c) {
        if (Utils.isEmpty(tml)) {
            return tml;
        }

        int start = 0, end = 0;
        while (true) {
            start = tml.indexOf("${", start);

            if (start < 0) {
                return tml;
            } else {
                end = tml.indexOf("}", start);

                if (end < 0) {
                    throw new IllegalStateException("Invalid template expression: " + tml);
                }

                String valueExp = tml.substring(start + 2, end); //key:def
                String valueExpFull = tml.substring(start, end + 1); //${key:def}
                //支持默认值表达式 ${key:def}
                String value = getByExp(main, target, valueExp, (propName, propDefVal) -> {
                    if (c != null) {
                        c.accept(propName, valueExpFull);
                    }
                });

                if (value == null) {
                    value = "";
                }

                tml = tml.substring(0, start) + value + tml.substring(end + 1);
                //起始位增量
                start = start + value.length();
            }
        }
    }
}