package org.noear.solon.core;

import java.lang.reflect.*;
import java.util.*;

/**
 * XAction 辅助工具
 * */
public class XActionUtil {
    public static XActionConverter def = new XActionConverter();
    public static Set<XActionConverter> converterSet = new HashSet<>();

    private static List<Object> paramsChange(XContext ctx, Parameter[] pSet) throws Exception{
        String tmp = ctx.contentType();
        for (XActionConverter c : converterSet) {
            if (c.matched(ctx, tmp)) {
                return c.change(ctx, pSet);
            }
        }

        return def.change(ctx, pSet);
    }

    /**
     * 执行方法
     */
    public static Object exeMethod(Object obj, MethodWrap mWrap, XContext ctx) throws Exception {
        try {
            Parameter[] pSet = mWrap.parameters;

            List<Object> args = paramsChange(ctx, pSet);


            if (args.size() == 0) {
                return mWrap.method.invoke(obj);
            } else {
                return mWrap.method.invoke(obj, args.toArray());
            }
        } catch (InvocationTargetException ex) {
            Throwable ex2 = ex.getCause();
            if (ex2 instanceof Error) {
                throw new RuntimeException(ex2);
            } else {
                throw (Exception) ex2;
            }
        }
    }
}
