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
     * 根据表达式获取配置值
     *
     * @param expr 兼容 ${key} or key or ${key:def} or key:def
     */
    public static String getByExp(Properties main, Properties target, String expr) {
        String name = expr;

        //如果有表达式，去掉符号
        if (name.startsWith("${") && name.endsWith("}")) {
            name = expr.substring(2, name.length() - 1);
        }

        //如果有默认值，则获取
        String def = null;
        int defIdx = name.indexOf(":");
        if (defIdx > 0) {
            if (name.length() > defIdx + 1) {
                def = name.substring(defIdx + 1).trim();
            } else {
                def = "";
            }
            name = name.substring(0, defIdx).trim();
        }


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
            return def;
        } else {
            return val;
        }
    }

    /**
     * 根据模板获取配置值
     *
     * @param tml @param tml 模板： ${key} 或 aaa${key}bbb 或 ${key:def}/ccc
     */
    public static String getByTml(Properties main, Properties target, String tml) {
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

                String name = tml.substring(start + 2, end);
                String value = getByExp(main, target, name);//支持默认值表达式 ${key:def}
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
