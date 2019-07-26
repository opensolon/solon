package org.noear.solon.core;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XParam;

import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * XAction 辅助工具
 * */
class XActionUtil {

    /** 将参数转为实体 */
    protected static Object params2Entity(XContext ctx, Class<?> clz) throws Exception{
        Field[] fields = clz.getDeclaredFields();

        Map<String,String> map = ctx.paramMap();
        Object obj = clz.newInstance();

        for (Field f : fields) {
            String key = f.getName();
            if (map.containsKey(key)) {
                Object val = change(f,f.getType(),key, map.get(key), ctx);
                f.set(obj,val);
            }
        }

        return obj;
    }

    /** 执行方法 */
    protected static Object exeMethod(Object obj, Method method, XContext ctx) throws Exception{
        try {
            Parameter[] pSet = method.getParameters();
            List<Object> args = new ArrayList<>();

            for (Parameter p : pSet) {
                Class<?> pt = p.getType();

                if (XContext.class.equals(pt)) {
                    args.add(ctx);
                }else {
                    String pn = p.getName();
                    String pv = ctx.param(pn);

                    if (pv == null) {
                        if(XFile.class == pt){
                            XFile file = ctx.file(pn);
                            args.add(file);
                        }else {
                            XParam xd = p.getAnnotation(XParam.class);
                            if (xd != null || pt.getAnnotation(XParam.class) != null) {
                                if (xd != null && XUtil.isEmpty(xd.value()) == false) {
                                    args.add(null);
                                } else {
                                    args.add(params2Entity(ctx, pt));
                                }
                            } else {
                                args.add(null);
                            }
                        }
                    } else {
                        args.add(change(p, pt, pn, pv, ctx));
                    }
                }
            }

            if(args.size()==0){
                return method.invoke(obj);
            }else {
                return method.invoke(obj, args.toArray());
            }
        }catch (InvocationTargetException ex) {
            Throwable ex2 = ex.getCause();
            if(ex2 instanceof Error){
                throw  new RuntimeException(ex2);
            }else{
                throw (Exception)ex2;
            }
        }
    }

    /** 类型转换 */
    protected static Object change(AnnotatedElement p , Class<?> type, String key, String val, XContext ctx) throws Exception {
        if (String.class == (type)) {
            return val;
        }

        if(val.length()==0){
            return null;
        }

        if (Integer.class == (type) || type == Integer.TYPE) {
            return Integer.parseInt(val);
        }

        if (Long.class == (type) || type == Long.TYPE) {
            return Long.parseLong(val);
        }

        if (Double.class == (type) || type == Double.TYPE) {
            return Double.parseDouble(val);
        }

        if (Float.class == (type) || type == Float.TYPE) {
            return Float.parseFloat(val);
        }

        if (Boolean.class == (type) || type == Boolean.TYPE) {
            return Boolean.parseBoolean(val);
        }

        if(Date.class == (type)) {
            XParam xd = p.getAnnotation(XParam.class);
            if (xd != null && XUtil.isEmpty(xd.value()) == false) {
                SimpleDateFormat fm = new SimpleDateFormat(xd.value());
                return fm.parse(val);
            }
        }

        if(String[].class == (type)){
            if(ctx == null){
                return null;
            }else {
                return ctx.paramValues(key);
            }
        }

        throw new RuntimeException("不支持类型:" + type.getName());
    }
}
