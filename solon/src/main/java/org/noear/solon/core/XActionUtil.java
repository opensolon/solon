package org.noear.solon.core;

import org.noear.solon.core.utils.TypeUtil;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * XAction 辅助工具
 * */
class XActionUtil {

    /**
     * 将参数转为实体
     */
    protected static Object params2Entity(XContext ctx, Class<?> clz) throws Exception {
        Field[] fields = clz.getDeclaredFields();

        Map<String, String> map = ctx.paramMap();
        Object obj = clz.newInstance();

        for (Field f : fields) {
            String key = f.getName();
            if (map.containsKey(key)) {
                Object val = TypeUtil.changeOfCtx(f, f.getType(), key, map.get(key), ctx);
                f.set(obj, val);
            }
        }

        return obj;
    }

    /**
     * 执行方法
     */
    protected static Object exeMethod(Object obj, MethodWrap mWrap, XContext ctx) throws Exception {
        try {
            Parameter[] pSet = mWrap.parameters;

            List<Object> args = new ArrayList<>();

            //p 参数
            //pt 参数原类型
            for (Parameter p : pSet) {
                Class<?> pt = p.getType();

                if (XContext.class.equals(pt)) {
                    //如果是 XContext 类型
                    args.add(ctx);
                } else {
                    String pn = p.getName();
                    String pv = ctx.param(pn);

                    if (pv == null) {
                        //
                        // 没有从ctx.param 直接找到值
                        //
                        if (XFile.class == pt) {
                            //1.如果是 XFile 类型
                            XFile file = ctx.file(pn);
                            args.add(file);
                        } else {
                            if (pt.getName().startsWith("java.") || pt.isArray()) {
                                //如果是基本类型，则为null
                                args.add(null);
                            } else {
                                //尝试转为模型
                                args.add(params2Entity(ctx, pt));
                            }
                        }
                    } else {
                        //如果拿到了肯体的参数值，则开始转换
                        args.add(TypeUtil.changeOfCtx(p, pt, pn, pv, ctx));
                    }
                }
            }

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
