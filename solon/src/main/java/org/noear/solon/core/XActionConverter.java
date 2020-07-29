package org.noear.solon.core;

import org.noear.solon.core.utils.TypeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XActionConverter {
    public List<Object> change(XContext ctx, Parameter[] pSet) throws Exception{
        List<Object> args = new ArrayList<>(pSet.length);

        Object bodyObj = changeBody(ctx);

        //p 参数
        //pt 参数原类型
        for (Parameter p : pSet) {
            Class<?> pt = p.getType();

            if (XContext.class.isAssignableFrom(pt)) {
                //如果是 XContext 类型，直接加入参数
                //
                args.add(ctx);
            } else if (XFile.class == pt) {
                //如果是文件
                //
                args.add(ctx.file(p.getName()));
            } else {
                args.add(changeValue(ctx, p, pt, bodyObj));
            }
        }

        return args;
    }

    protected Object changeBody(XContext ctx) throws Exception{
        return  null;
    }

    protected Object changeValue(XContext ctx, Parameter p, Class<?> pt, Object bodyObj) throws Exception {
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
                    if (pt.getName().startsWith("java.") || pt.isArray() || pt.isPrimitive()) {
                        //如果是java基础类型，则为null（后面统一地 isPrimitive 做处理）
                        //
                        tv = null;
                    } else {
                        //尝试转为实体
                        tv = changeEntity(ctx, pn, pt, bodyObj);
                    }
                }
            }
        } else {
            //如果拿到了具体的参数值，则开始转换
            tv = TypeUtil.changeOfCtx(p, pt, pn, pv, ctx);
        }

        if (tv == null) {
            //
            // 如果是基类类型（int,long...），则抛出异常
            //
            if (pt.isPrimitive()) {
                //如果是基本类型，则为给个默认值
                //
                if (pt == short.class) {
                    tv = (short) 0;
                } else if (pt == int.class) {
                    tv = 0;
                } else if (pt == long.class) {
                    tv = 0L;
                } else if (pt == double.class) {
                    tv = 0d;
                } else if (pt == float.class) {
                    tv = 0f;
                } else if (pt == boolean.class) {
                    tv = false;
                } else {
                    //
                    //其它类型不支持
                    //
                    throw new IllegalArgumentException("Please enter a valid parameter @" + pn);
                }
            }
        }

        return tv;
    }

    /**
     * 获取实体值
     */
    protected Object changeEntity(XContext ctx, String name, Class<?> type, Object bodyObj) throws Exception {
        Field[] fields = type.getDeclaredFields();

        Map<String, String> map = ctx.paramMap();
        Object obj = type.newInstance();

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
