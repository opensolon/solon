/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.mvc;

import org.noear.solon.core.exception.ConstructionException;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.core.util.LazyReference;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ActionExecutor 默认实现
 *
 * @see ActionDefault#executeDo(Context, Object)
 * @author noear
 * @since 1.0
 * */
public class ActionExecuteHandlerDefault implements ActionExecuteHandler {
    /**
     * 是否匹配
     *
     * @param ctx  请求上下文
     * @param mime 内容类型
     */
    @Override
    public boolean matched(Context ctx, String mime) {
        return true;
    }

    /**
     * 执行
     *
     * @param ctx    请求上下文
     * @param target 控制器
     * @param mWrap  函数包装器
     */
    @Override
    public Object executeHandle(Context ctx, Object target, MethodWrap mWrap) throws Throwable {
        Object[] args = resolveArguments(ctx, target, mWrap);
        return mWrap.invokeByAspect(target, args);
    }

    /**
     * 执行
     *
     * @param ctx    请求上下文
     * @param target 控制器
     * @param mWrap  函数包装器
     */
    @Override
    public Object[] resolveArguments(Context ctx, Object target, MethodWrap mWrap) throws Throwable {
        return buildArgs(ctx, target, mWrap).toArray();
    }

    /**
     * 构建执行参数
     *
     * @param ctx    请求上下文
     * @param target 控制器
     * @param mWrap  函数包装器
     */
    protected List<Object> buildArgs(Context ctx, Object target, MethodWrap mWrap) throws Throwable {
        ParamWrap[] pSet = mWrap.getParamWraps();
        List<Object> args = new ArrayList<>(pSet.length);

        //懒引用
        LazyReference bodyRef = new LazyReference(() -> changeBody(ctx, mWrap));

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
                args.add(ctx.file(p.spec().getName()));
            } else if (UploadedFile[].class == pt) {
                //如果是 UploadedFile
                //
                args.add(ctx.fileValues(p.spec().getName()));
            } else {
                Object tv = null;

                if (Object.class != pt) { //object 是所在基类，不能用它拉取
                    tv = ctx.pull(pt);
                }

                if (tv == null) {
                    if (p.spec().isRequiredBody()) {
                        //需要 body 数据
                        if (String.class.equals(pt)) {
                            tv = ctx.bodyNew();
                        } else if (InputStream.class.equals(pt)) {
                            tv = ctx.bodyAsStream();
                        } else if (Map.class.equals(pt) && bodyRef.get() instanceof MultiMap) {
                            tv = ((MultiMap) bodyRef.get()).toValueMap();
                        }
                    }
                }

                if (tv == null) {
                    //尝试数据转换
                    try {
                        tv = changeValue(ctx, p, i, pt, bodyRef);
                    } catch (ConstructionException e) {
                        throw e;
                    } catch (Exception e) {
                        String methodFullName = mWrap.getDeclaringClz().getName() + "::" + mWrap.getName() + "@" + p.spec().getName();
                        throw new StatusException("Action parameter change failed: " + methodFullName, e, 400);
                    }
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
                            throw new IllegalArgumentException("Please enter a valid parameter @" + p.spec().getName());
                        }
                    }
                }

                if (tv == null) {
                    if (p.spec().isRequiredInput()) {
                        throw new StatusException(p.spec().getRequiredHint(), 400);
                    }
                }

                args.add(tv);
            }
        }

        return args;
    }

    /**
     * 尝试将body转换为特定对象
     *
     * @param ctx   请求上下文
     * @param mWrap 函数包装器
     */
    protected Object changeBody(Context ctx, MethodWrap mWrap) throws Exception {
        return ctx.paramMap();
    }

    /**
     * 尝试将值按类型转换
     *
     * @param ctx     请求上下文
     * @param p       参数包装
     * @param pi      参数序位
     * @param pt      参数类型
     * @param bodyRef 主体对象
     */
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, LazyReference bodyRef) throws Throwable {
        String pn = p.spec().getName();        //参数名
        String pv = p.spec().getValue(ctx);    //参数值
        Object tv = null;               //目标值

        if (pv == null) {
            pv = p.spec().getDefaultValue();
        }

        if (pv == null) {
            //
            // 没有从 ctx.param 直接找到值
            //
            if (UploadedFile.class == pt) {
                //1.如果是 UploadedFile 类型
                tv = ctx.file(pn);
            } else if (UploadedFile[].class == pt) {
                //2.如果是 UploadedFile[] 类型
                tv = ctx.fileValues(pn);
            } else {
                //$name 的变量，从attr里找
                if (pn.startsWith("$")) {
                    tv = ctx.attr(pn);
                } else {
                    if (pt.getName().startsWith("java.") || pt.isArray() || pt.isPrimitive() || pt.isEnum()) {
                        //如果是java基础类型，则为null（后面统一地 isPrimitive 做处理）
                        //
                        tv = null;
                    } else {
                        //尝试转为实体
                        tv = changeEntityDo(ctx, p, pn, pt);
                    }
                }
            }
        } else {
            //如果拿到了具体的参数值，则开始转换
            tv = changeValueDo(ctx, p, pn, pt, pv);
        }

        return tv;
    }

    /**
     * 尝试将值转换为目标值
     *
     * @param ctx  请求上下文
     * @param p    参数包装
     * @param name 数据名字
     * @param type 数据类型
     */
    protected Object changeValueDo(Context ctx, ParamWrap p, String name, Class<?> type, String value) {
        return ConvertUtil.to(p.spec(), value, ctx);
    }

    /**
     * 尝试将值转换为目标实体
     *
     * @param ctx  请求上下文
     * @param p    参数包装
     * @param name 数据名字
     * @param type 数据类型
     */
    protected Object changeEntityDo(Context ctx, ParamWrap p, String name, Class<?> type) throws Exception {
        ClassWrap clzW = ClassWrap.get(type);
        MultiMap<String> map = ctx.paramMap();

        return clzW.newBy(map::get, ctx);
    }
}