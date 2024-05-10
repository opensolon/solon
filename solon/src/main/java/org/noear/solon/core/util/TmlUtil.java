package org.noear.solon.core.util;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;

import java.util.HashMap;
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
        if (tml.indexOf('$') < 0) {
            return tml;
        }

        return parse(tml, inv.argsAsMap());
    }

    public static String parse(String tml, Invocation inv, Object rst) {
        if (tml.indexOf('$') < 0) {
            return tml;
        }

        Map<String, Object> model = new HashMap<>(inv.argsAsMap());
        model.put("", rst);

        return parse(tml, model);
    }

    /**
     * 解析模板
     *
     * <pre><code>name=${name},type={.type}</code></pre>
     *
     * @param view  模板
     * @param model 参数
     */
    public static String parse(String view, Map<String, Object> model) {
        if (view.indexOf('$') < 0) {
            return view;
        }

        StringBuilder str2 = new StringBuilder(view);

        //${name}
        //${.name}
        //${obj.name}
        Matcher m = tmpPattern.matcher(view);
        while (m.find()) {
            String mark = m.group(0);
            String name = m.group(1);

            if (model.containsKey(name)) {
                //说明从输入参数取值
                String val = String.valueOf(model.get(name));
                int idx = str2.indexOf(mark);
                str2 = str2.replace(idx, idx + mark.length(), val);
            } else if (name.indexOf('.') >= 0) {
                //说明要从返回结果取值
                Object obj;
                String fieldKey = null;
                String fieldVal = null;
                {
                    String[] cf = name.split("\\.");
                    obj = model.get(cf[0]);
                    fieldKey = cf[1];
                }

                if (obj != null) {
                    Object valTmp = null;

                    if (obj instanceof Map) {
                        valTmp = ((Map) obj).get(fieldKey);
                    } else {
                        FieldWrap fw = ClassWrap.get(obj.getClass()).getFieldWrap(fieldKey);
                        if (fw == null) {
                            throw new IllegalArgumentException("Missing tml parameter (result field): " + name);
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

                int idx = str2.indexOf(mark);
                str2 = str2.replace(idx, idx + mark.length(), fieldVal);
            } else {
                //如果缺少参数就出异常，容易发现问题
                throw new IllegalArgumentException("Missing tml parameter: " + name);
            }
        }


        return str2.toString();
    }

    private static final Pattern tmpPattern = Pattern.compile("\\$\\{(\\w*\\.?\\w+)\\}");
}
