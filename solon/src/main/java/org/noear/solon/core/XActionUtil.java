package org.noear.solon.core;

import java.lang.reflect.*;
import java.util.*;

/**
 * XAction 辅助工具
 * */
public class XActionUtil {
    /**
     * 默认执行器
     * */
    protected static MethodExecutor exeDef = new XActionExecutor();

    /**
     * 执行库
     * */
    protected static Set<MethodExecutor> exeLib = new HashSet<>();

    /**
     * 注册执行器
     * */
    public static void register(MethodExecutor executor){
        exeLib.add(executor);
    }


    /**
     * 执行方法
     */
    public static Object exeMethod(XContext ctx, Object obj, MethodWrap mWrap) throws Throwable {
        String ct = ctx.contentType();

        for (MethodExecutor me : exeLib) {
            if (me.matched(ctx, ct)) {
                return me.execute(ctx, obj, mWrap);
            }
        }

        return exeDef.execute(ctx, obj, mWrap);
    }
}
