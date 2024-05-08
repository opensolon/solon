package org.noear.solon.core.util;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板工具
 *
 * @author noear
 * @since 2.8
 */
public class TmlUtil {
    public static String parse(String tml, Invocation inv) {
        if (tml.indexOf("$") < 0) {
            return tml;
        }

        return parse(tml, inv.argsAsMap(), null);
    }

    public static String parse(String tml, Invocation inv, Object rst) {
        if (tml.indexOf("$") < 0) {
            return tml;
        }

        return parse(tml, inv.argsAsMap(), rst);
    }

    /**
     * 解析模板
     *
     * <code><pre>name=${name}</pre></code>
     *
     * @param tml    模板
     * @param params 参数
     */
    public static String parse(String tml, Map<String, Object> params) {
        return parse(tml, params, null);
    }


    /**
     * 解析模板
     *
     * <code><pre>name=${name}</pre></code>
     *
     * @param tml    模板
     * @param params 参数
     * @param result 结果
     */
    public static String parse(String tml, Map<String, Object> params, Object result) {
        if (tml.indexOf("$") < 0) {
            return tml;
        }

        String str2 = tml;

        //${name}
        //${.name}
        //${obj.name}
        Pattern pattern = Pattern.compile("\\$\\{(\\w*\\.?\\w+)\\}");
        Matcher m = pattern.matcher(tml);
        while (m.find()) {
            String mark = m.group(0);
            String name = m.group(1);

            if (params.containsKey(name)) {
                //说明从输入参数取值
                String val = String.valueOf(params.get(name));

                str2 = str2.replace(mark, val);
            } else if (name.contains(".")) {
                //说明要从返回结果取值
                Object obj;
                String fieldKey = null;
                String fieldVal = null;
                if (name.startsWith(".")) {
                    obj = result;
                    fieldKey = name.substring(1);
                } else {
                    String[] cf = name.split("\\.");
                    obj = params.get(cf[0]);
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
                    fieldVal = "";
                }

                str2 = str2.replace(mark, fieldVal);
            } else {
                //如果缺少参数就出异常，容易发现问题
                throw new IllegalArgumentException("Missing tml parameter: " + name);
            }
        }

        return str2;
    }
}
