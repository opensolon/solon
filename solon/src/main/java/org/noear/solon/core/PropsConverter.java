package org.noear.solon.core;

import org.noear.solon.Utils;
import org.noear.solon.core.wrap.ClassWrap;

import java.util.Properties;

/**
 * @author noear
 * @since 1.6
 */
public class PropsConverter {
    private static PropsConverter global;

    public static PropsConverter global() {
        return global;
    }

    public static void globalSet(PropsConverter instance) {
        if (instance != null) {
            PropsConverter.global = instance;
        }
    }

    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        PropsConverter tmp = Utils.newInstance("org.noear.solon.extend.impl.PropsConverterExt");

        if (tmp == null) {
            global = new PropsConverter();
        } else {
            global = tmp;
        }
    }


    /**
     * 转换
     *
     * @param props     属性
     * @param target    目标
     * @param targetClz 目标类型
     */
    public <T> T convert(Properties props, T target, Class<T> targetClz) {
        if (target == null) {
            return ClassWrap.get(targetClz).newBy(props);
        } else {
            ClassWrap.get(target.getClass()).fill(target, props::getProperty);
            return target;
        }
    }
}
