package org.noear.solon.data.util;

import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.util.TmplUtil;

import java.lang.reflect.Method;

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
        return TmplUtil.parse(tml, inv, null);
    }

    /**
     * 基于模板与调用构建Key
     *
     * @param tml 模板
     * @param inv 拦截动作
     * @param rst 返回值
     */
    public static String buildByTmlAndInv(String tml, Invocation inv, Object rst) {
        return TmplUtil.parse(tml, inv, rst);
    }
}
