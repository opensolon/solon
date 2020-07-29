package org.noear.solon.core;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * XAction 辅助工具
 * */
public class XActionUtil {
    public static XActionConverter converter = new XActionConverter();

    /**
     * 将参数转为实体
     */
    public static Object params2Entity(XContext ctx, Class<?> pt) throws Exception {
        return converter.changeEntity(ctx, null, pt);
    }

    /**
     * 执行方法
     */
    public static Object exeMethod(Object obj, MethodWrap mWrap, XContext ctx) throws Exception {
        try {
            Parameter[] pSet = mWrap.parameters;

            List<Object> args = converter.change(ctx, pSet);


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
