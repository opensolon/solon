package org.noear.solon.core.handle;

import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.core.wrap.ParamWrap;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 默认的ActionExecutor实现
 *
 * @see Action#callDo(Context, Object, MethodWrap)
 * @author noear
 * @since 1.0
 * */
public class ActionExecutorDefault implements ActionExecutor {
    /**
     * 是否匹配
     *
     * @param ctx 上下文
     * @param ct  内容类型
     */
    @Override
    public boolean matched(Context ctx, String ct) {
        return true;
    }

    /**
     * 执行
     *
     * @param ctx   上下文
     * @param obj   控制器
     * @param mWrap 函数包装器
     */
    @Override
    public Object execute(Context ctx, Object obj, MethodWrap mWrap) throws Throwable {
        List<Object> args = buildArgs(ctx, mWrap.getParamWraps());
        return mWrap.invokeByAspect(obj, args.toArray());
    }


    /**
     * 构建执行参数
     */
    protected List<Object> buildArgs(Context ctx, ParamWrap[] pSet) throws Exception {
        List<Object> args = new ArrayList<>(pSet.length);

        Object bodyObj = changeBody(ctx);

        //p 参数
        //pt 参数原类型
        for (int i = 0, len = pSet.length; i < len; i++) {
            ParamWrap p = pSet[i];
            Class<?> pt = p.getType();

            if (Context.class.isAssignableFrom(pt)) {
                //如果是 Context 类型，直接加入参数
                //
                args.add(ctx);
            } else if (ModelAndView.class.isAssignableFrom(pt)) {
                //如果是 ModelAndView 类型，直接加入参数
                //
                args.add(new ModelAndView());
            } else if (Locale.class.isAssignableFrom(pt)) {
                //如果是 Locale 类型，直接加入参数
                //
                args.add(ctx.getLocale());
            } else if (UploadedFile.class == pt) {
                //如果是 UploadedFile
                //
                args.add(ctx.file(p.getName()));
            } else if (pt.getTypeName().equals("javax.servlet.http.HttpServletRequest")) {
                args.add(ctx.request());
            } else if (pt.getTypeName().equals("javax.servlet.http.HttpServletResponse")) {
                args.add(ctx.response());
            } else {
                Object tv = null;


                if (p.requireBody()) {
                    //需要 body 数据
                    if (String.class.equals(pt)) {
                        tv = ctx.bodyNew();
                    } else if (InputStream.class.equals(pt)) {
                        tv = ctx.bodyAsStream();
                    }
                }

                if (tv == null) {
                    //尝试数据转换
                    tv = changeValue(ctx, p, i, pt, bodyObj);
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
                            throw new IllegalArgumentException("Please enter a valid parameter @" + p.getName());
                        }
                    }
                }

                if (tv == null) {
                    if (p.required()) {
                        ctx.status(400);
                        throw new IllegalArgumentException("Required parameter @" + p.getName());
                    }
                }

                args.add(tv);
            }
        }

        return args;
    }

    /**
     * 尝试将body转换为特定对象
     */
    protected Object changeBody(Context ctx) throws Exception {
        return null;
    }

    /**
     * 尝试将值按类型转换
     */
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        String pn = p.getName();  //参数名
        String pv = null;         //参数值
        Object tv = null;         //目标值

        if (p.requireHeader()) {
            pv = ctx.header(pn);
        } else {
            pv = ctx.param(pn);
        }

        if (pv == null) {
            pv = p.defaultValue();
        }

        if (pv == null) {
            //
            // 没有从 ctx.param 直接找到值
            //
            if (UploadedFile.class == pt) {
                //1.如果是 UploadedFile 类型
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
                        tv = changeEntityDo(ctx, pn, pt);
                    }
                }
            }
        } else {
            //如果拿到了具体的参数值，则开始转换
            tv = ConvertUtil.to(p.getParameter(), pt, pn, pv, ctx);
        }

        return tv;
    }

    /**
     * 尝试将值转换为实体
     */
    private Object changeEntityDo(Context ctx, String name, Class<?> type) throws Exception {
        ClassWrap clzW = ClassWrap.get(type);
        Map<String, String> map = ctx.paramMap();

        return clzW.newBy(map::get, ctx);
    }
}
