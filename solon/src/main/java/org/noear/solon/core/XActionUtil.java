package org.noear.solon.core;

import org.noear.solon.core.utils.TypeUtil;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * XAction 辅助工具
 * */
public class XActionUtil {

    /**
     * 将参数转为实体
     */
    public static Object params2Entity(XContext ctx, Class<?> clz) throws Exception {
        Field[] fields = clz.getDeclaredFields();

        Map<String, String> map = ctx.paramMap();
        Object obj = clz.newInstance();

        for (Field f : fields) {
            String key = f.getName();
            if (map.containsKey(key)) {
                //将 string 转为目标 type，并为字段赋值
                Object val = TypeUtil.changeOfCtx(f, f.getType(), key, map.get(key), ctx);
                f.set(obj, val);
            }
        }

        return obj;
    }

    /**
     * 执行方法
     */
    public static Object exeMethod(Object obj, MethodWrap mWrap, XContext ctx) throws Exception {
        try {
            Parameter[] pSet = mWrap.parameters;

            List<Object> args = new ArrayList<>();

            //p 参数
            //pt 参数原类型
            for (Parameter p : pSet) {
                Class<?> pt = p.getType();

                if (XContext.class.isAssignableFrom(pt)) {
                    //如果是 XContext 类型，直接加入参数
                    //
                    args.add(ctx);
                } else {
                    String pn = p.getName();    //参数名
                    String pv = ctx.param(pn);  //参数值
                    Object tv = null;

                    if (pv == null) {
                        //
                        // 没有从ctx.param 直接找到值
                        //
                        if (XFile.class == pt) {
                            //1.如果是 XFile 类型
                            tv = ctx.file(pn);
                        } else {
                            //$name 的变量，从attr里找
                            if (pn.startsWith("$")) {
                                tv = ctx.attr(pn);
                            } else {
                                if (pt.getName().startsWith("java.") || pt.isArray()) {
                                    //如果是java基础对象，则为null
                                    //
                                    tv = null;
                                } else if (pt.isPrimitive()) {
                                    //如果是基本类型，则为null
                                    //
                                    if(pt == short.class){
                                        tv = (short)0;
                                    } else if(pt == int.class){
                                        tv = 0;
                                    } else if(pt == long.class){
                                        tv = 0L;
                                    } else if(pt == double.class){
                                        tv = 0d;
                                    } else if(pt == float.class){
                                        tv = 0f;
                                    } else if(pt == boolean.class){
                                        tv = false;
                                    }
                                    //别的类型，暂时不管
                                } else {
                                    //尝试转为模型
                                    tv = params2Entity(ctx, pt);
                                }
                            }
                        }
                    } else {
                        //如果拿到了具体的参数值，则开始转换
                        tv = TypeUtil.changeOfCtx(p, pt, pn, pv, ctx);
                    }

                    if(tv == null){
                        //
                        // 如果是基类类型（int,long...），则抛出异常
                        //
                        if (pt.isPrimitive()) {
                            throw new IllegalArgumentException("Please enter a valid parameter @" + pn);
                        }
                    }

                    args.add(tv);
                }
            }

            if (args.size() == 0) {
                return mWrap.method.invoke(obj);
            } else {
                return mWrap.method.invoke(obj, args.toArray());
            }
        }
        catch (InvocationTargetException ex) {
            Throwable ex2 = ex.getCause();
            if (ex2 instanceof Error) {
                throw new RuntimeException(ex2);
            } else {
                throw (Exception) ex2;
            }
        }
    }
}
