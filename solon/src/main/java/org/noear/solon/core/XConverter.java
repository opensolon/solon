package org.noear.solon.core;

import org.noear.solon.core.utils.TypeUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 实体转换器
 * */
public class XConverter {
    public static XConverter global = new XConverter();

    /**
     * 获取
     * */
    public Object convert(XContext ctx, Class<?> clz) throws Exception {
        Field[] fields = clz.getDeclaredFields();

        Map<String, String> map = ctx.paramMap();
        Object obj = clz.newInstance();

        if (map.size() > 0) {
            for (Field f : fields) {
                String key = f.getName();
                if (map.containsKey(key)) {
                    //将 string 转为目标 type，并为字段赋值
                    Object val = TypeUtil.changeOfCtx(f, f.getType(), key, map.get(key), ctx);
                    f.set(obj, val);
                }
            }
        }

        return obj;
    }
}
