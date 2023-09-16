package org.noear.solon.core.util;

import org.noear.solon.Utils;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        return getByExp( main,  target,  expr, null);
    }
    public static String getByExp(Properties main, Properties target, String expr, BiConsumer<String, String> c) {
        AtomicReference<String> nameAtomic = new AtomicReference<>();
        AtomicReference<String> defAtomic = new AtomicReference<>();
        exprStrHandle(expr, (name, def)->{
            nameAtomic.set(name);
            defAtomic.set(def);
        });

        String name = nameAtomic.get(), def = defAtomic.get();

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
            if(c != null){
                c.accept(name, def);
            }
            return def;
        } else {
            return val;
        }
    }
    public static  void exprStrHandle(String exprStr, BiConsumer<String, String> c){
        //如果有表达式，去掉符号
        if (exprStr.startsWith("${") && exprStr.endsWith("}")) {
            exprStr = exprStr.substring(2, exprStr.length() - 1);
        }
        //如果有默认值，则获取
        String def = null;
        int defIdx = exprStr.indexOf(":");
        if (defIdx > 0) {
            if (exprStr.length() > defIdx + 1) {
                def = exprStr.substring(defIdx + 1).trim();
            } else {
                def = "";
            }
            exprStr = exprStr.substring(0, defIdx).trim();
        }
        c.accept(exprStr, def);
    }

    public static String getByTml(Properties main, Properties target, String tml) {
        return getByTml(main, target, tml, null);
    }

    /**
     * 根据模板获取配置值
     *
     * @param tml @param tml 模板： ${key} 或 aaa${key}bbb 或 ${key:def}/ccc
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
                String name = tml.substring(start + 2, end);
                String valueExpStr = tml.substring(start, end + 1);
                //支持默认值表达式 ${key:def}
                String value = getByExp(main, target, name, (propName, propDefVal) ->{
                    if(c != null){
                        c.accept(propName, valueExpStr);
                    }
                });
                if (value == null) {
                    value = "";
                }
                //if(c != null && "".equals(value)){
                //    c.accept(name.replaceAll(":", "").trim(), tml.substring(start, end + 1));
                //}
                tml = tml.substring(0, start) + value + tml.substring(end + 1);
                //起始位增量
                start = start + value.length();
            }
        }
    }
}
