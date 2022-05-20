package org.noear.solon.data.util;

import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 拦截动作模板处理
 *
 * @author noear
 * @since 1.6
 */
public class InvKeys {

    /**
     * 基于调用构建Key
     *
     * @param inv 拦截动作
     */
    public static String buildByInv(Invocation inv) {
        Method method = inv.method().getMethod();

        StringBuilder keyB = new StringBuilder();

        keyB.append(method.getDeclaringClass().getName()).append(":");
        keyB.append(method.getName()).append(":");

        inv.argsAsMap().forEach((k, v) -> {
            keyB.append(k).append("_").append(v);
        });

        //必须md5，不然会出现特殊符号
        return Utils.md5(keyB.toString());
    }

    /**
     * 基于模板与调用构建Key
     *
     * @param tml 模板
     * @param inv 拦截动作
     */
    public static String buildByTmlAndInv(String tml, Invocation inv) {
        return buildByTmlAndInv(tml, inv, null);
    }

    /**
     * 基于模板与调用构建Key
     *
     * @param tml 模板
     * @param inv 拦截动作
     * @param rst 返回值
     */
    public static String buildByTmlAndInv(String tml, Invocation inv, Object rst) {
        if (tml.indexOf("$") < 0) {
            return tml;
        }

        Map map = inv.argsAsMap();
        String str2 = tml;

        //${name}
        //${.name}
        //${obj.name}
        Pattern pattern = Pattern.compile("\\$\\{(\\w*\\.?\\w+)\\}");
        Matcher m = pattern.matcher(tml);
        while (m.find()) {
            String mark = m.group(0);
            String name = m.group(1);

            if (map.containsKey(name)) {
                //说明从输入参数取值
                String val = String.valueOf(map.get(name));

                str2 = str2.replace(mark, val);
            } else if (name.contains(".")) {
                //说明要从返回结果取值
                Object obj;
                String fieldKey = null;
                String fieldVal = null;
                if (name.startsWith(".")) {
                    obj = rst;
                    fieldKey = name.substring(1);
                } else {
                    String[] cf = name.split("\\.");
                    obj = map.get(cf[0]);
                    fieldKey = cf[1];
                }

                if (obj != null) {
                    Object valTmp = null;

                    if (obj instanceof Map) {
                        valTmp = ((Map) obj).get(fieldKey);
                    } else {
                        FieldWrap fw = ClassWrap.get(obj.getClass()).getFieldWrap(fieldKey);
                        if (fw == null) {
                            throw new IllegalArgumentException("Missing cache tag parameter (result field): " + name);
                        }

                        try {
                            valTmp = fw.getValue(obj);
                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (valTmp != null) {
                        fieldVal = valTmp.toString();
                    }
                }

                if (fieldVal == null) {
                    fieldVal = "null";
                }

                str2 = str2.replace(mark, fieldVal);
            } else {
                //如果缺少参数就出异常，容易发现问题
                throw new IllegalArgumentException("Missing cache tag parameter: " + name);
            }
        }

        return str2;
    }
}
