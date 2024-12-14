/*
 * Copyright 2017-2024 noear.org and authors
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

import org.noear.solon.Utils;
import org.noear.solon.annotation.Consumes;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Multipart;
import org.noear.solon.annotation.Produces;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Constants;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.DataThrowable;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.PathMatcher;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * mvc:动作 默认实现
 *
 * @author noear
 * @since 1.0
 * @since 3.0
 * */
public class ActionDefault extends HandlerAide implements Action {
    //bean 包装器
    private final BeanWrap bWrap;
    //bean 相关aide
    private final HandlerAide bAide;
    //bean 相关reader
    private Render bRender;

    //method 处理器
    private final MethodWrap mWrap;
    //method 相关的 produces（输出产品）
    private String mProduces;
    //method 相关的 consumes（输入产品）
    private String mConsumes;
    //action name
    private final String mName;
    private final String mFullName;//包类的 Mapping，去掉 / 开头
    //action remoting
    private final boolean mRemoting;
    private final Mapping mMapping;

    private boolean mMultipart;

    //path 分析器
    private PathMatcher pathKeysAnalyzer;//路径分析器
    //path key 列表
    private List<String> pathKeys;

    public ActionDefault(BeanWrap bWrap, Method method) {
        this(bWrap, null, method, null, null, false, null);
    }

    public ActionDefault(BeanWrap bWrap, HandlerAide bAide, Method method, Mapping mapping, String path, boolean remoting, Render render) {
        this.bWrap = bWrap;
        this.bAide = bAide;

        method.setAccessible(true);

        if (NativeDetector.isAotRuntime()) {
            bWrap.context().methodGet(bWrap.rawClz(), method);
        }

        //@since 3.0
        mWrap = new MethodWrap(bWrap.context(), bWrap.rawClz(), method).ofHandler();
        mRemoting = remoting;
        mMapping = mapping;
        bRender = render;

        if (bRender == null) {
            //如果控制器是XRender
            if (Render.class.isAssignableFrom(bWrap.clz())) {
                bRender = bWrap.raw();
            }
        }

        if (mapping == null) {
            mName = method.getName();
        } else {
            //of method
            Produces producesAnno = method.getAnnotation(Produces.class);
            Consumes consumesAnno = method.getAnnotation(Consumes.class);
            Multipart multipartAnno = method.getAnnotation(Multipart.class);

            if (producesAnno == null) {
                mProduces = mapping.produces();
            } else {
                mProduces = producesAnno.value();
            }

            if (consumesAnno == null) {
                mConsumes = mapping.consumes();
            } else {
                mConsumes = consumesAnno.value();
            }


            //of class
            if (Utils.isEmpty(mProduces)) {
                producesAnno = bWrap.rawClz().getAnnotation(Produces.class);
                if (producesAnno != null) {
                    mProduces = producesAnno.value();
                }
            }

            if (Utils.isEmpty(mConsumes)) {
                consumesAnno = bWrap.rawClz().getAnnotation(Consumes.class);
                if (consumesAnno != null) {
                    mConsumes = consumesAnno.value();
                }
            }

            if (multipartAnno == null) {
                multipartAnno = bWrap.rawClz().getAnnotation(Multipart.class);
            }

            //for Multipart
            if (multipartAnno == null) {
                mMultipart = mapping.multipart();
            } else {
                mMultipart = true;
            }

            mName = Utils.annoAlias(mapping.value(), mapping.path());
        }

        if (Utils.isEmpty(path)) {
            mFullName = mName;
        } else {
            if (path.startsWith("/")) {
                mFullName = path.substring(1);
            } else {
                mFullName = path;
            }
        }

        //支持多分片申明
        if (mMultipart == false) {
            for (Class<?> clz : method.getParameterTypes()) {
                if (UploadedFile.class.isAssignableFrom(clz)) {
                    mMultipart = true;
                    break;
                }
            }
        }

        //支持path变量
        if (path != null && path.contains("{")) {
            pathKeys = new ArrayList<>();
            Matcher pm = PathUtil.pathKeyExpr.matcher(path);
            while (pm.find()) {
                pathKeys.add(pm.group(1));
            }

            if (pathKeys.size() > 0) {
                pathKeysAnalyzer = PathMatcher.get(path);
            }
        }
    }

    /**
     * 名称
     */
    public String name() {
        return mName;
    }

    /**
     * 全名称
     */
    public String fullName() {
        return mFullName;
    }

    /**
     * 映射（可能为Null）
     */
    public Mapping mapping() {
        return mMapping;
    }

    /**
     * 函数包装器
     */
    public MethodWrap method() {
        return mWrap;
    }

    /**
     * 控制类包装器
     */
    public BeanWrap controller() {
        return bWrap;
    }

    /**
     * 生产者（用于文档生成）
     */
    public String produces() {
        return mProduces;
    }

    /**
     * 消息费（用于文档生成）
     */
    public String consumes() {
        return mConsumes;
    }


    @Override
    public void handle(Context x) throws Throwable {
        if (Utils.isNotEmpty(mConsumes)) {
            if (x.contentType() == null || x.contentType().contains(mConsumes) == false) {
                throw new StatusException("Unsupported Media Type, path=" + x.path(), 415);
            }
        }

        if (mMultipart) {
            x.autoMultipart(true);
        }

        invoke(x, null);
    }

    /**
     * 调用
     */
    public void invoke(Context c, Object obj) throws Throwable {
        c.remotingSet(mRemoting);

        try {
            //预加载控制器，确保所有的'处理器'可以都可以获取控制器
            if (obj == null) {
                obj = bWrap.get(true);
            }

            //传递控制器实例
            c.attrSet(Constants.ATTR_CONTROLLER, obj);
            c.attrSet(Constants.ATTR_MAIN_HANDLER, this);


            if (bAide == null) {
                invokeFilterDo(c);
            } else {
                new FilterChainImpl(bAide.filters(), this::invokeFilterDo).doFilter(c);
            }
        } catch (Throwable e) {
            c.setHandled(true); //停止处理

            e = Utils.throwableUnwrap(e);

            if (e instanceof DataThrowable) {
                DataThrowable ex = (DataThrowable) e;

                if (ex.data() == null) {
                    renderDo(ex, c);
                } else {
                    renderDo(ex.data(), c);
                }
            } else {
                c.errors = e;
                renderDo(e, c);
            }
        }
    }

    /**
     * 执行内部过滤处理
     */
    protected void invokeFilterDo(Context x) throws Throwable {
        new FilterChainImpl(filters(), this::invokeHandleDo).doFilter(x);
    }

    /**
     * 执行内部转换处理
     */
    protected void invokeHandleDo(Context x) throws Throwable {
        Object obj = x.controller();
        invokeMethodDo(x, obj);
    }


    /**
     * 执行内部方法调用
     */
    protected void invokeMethodDo(Context c, Object obj) throws Throwable {

        /**
         * 1.确保所有'处理器'，能拿到控制器
         * 2.确保后置'处理器'，能被触发（前面的异常不能影响后置处理）
         * 3.确保最多一次渲染
         * */

        try {
            //主体处理（最多一次渲染）//非主体处理 或 未处理
            if (c.getHandled() == false) { //保留这个，过滤器可以有两种控制方式（软控，硬控）

                //获取path var
                bindPathVarDo(c);

                //执行
                c.result = executeDo(c, obj);

                //设定输出产品（放在这个位置正好）
                if (Utils.isEmpty(mProduces)) {
                    String tmp = c.accept();
                    if (c.contentTypeNew() == null && tmp != null && tmp.indexOf(',') < 0) {
                        //如果未设过；且是单个内容类型
                        c.contentType(tmp);
                    }
                } else {
                    c.accept(mProduces);
                    c.contentType(mProduces);
                }

                if (void.class != method().getReturnType()) {
                    //结果处理
                    ActionReturnHandler returnHandler = c.attr(Constants.ATTR_RETURN_HANDLER);
                    if (returnHandler == null) {
                        returnHandler = bWrap.context().app().chainManager().getReturnHandler(c, method().getReturnType());
                    }

                    if (returnHandler != null) {
                        //执行函数
                        returnHandler.returnHandle(c, this, c.result);
                    } else {
                        //渲染
                        renderDo(c.result, c);
                    }
                }
            }
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);

            if (e instanceof DataThrowable) {
                DataThrowable ex = (DataThrowable) e;
                if (ex.data() == null) {
                    renderDo(ex, c);
                } else {
                    renderDo(ex.data(), c);
                }
            } else {
                c.errors = e; //为 afters，留个参考
                throw e;
            }
        }
    }

    private void bindPathVarDo(Context c) throws Throwable {
        if (pathKeysAnalyzer != null) {
            Matcher pm = pathKeysAnalyzer.matcher(c.pathNew());
            if (pm.find()) {
                for (int i = 0, len = pathKeys.size(); i < len; i++) {
                    c.paramMap().add(pathKeys.get(i), pm.group(i + 1));//不采用group name,可解决_的问题
                }
            }
        }
    }

    protected Object executeDo(Context c, Object target) throws Throwable {
        ActionExecuteHandler executeHandler = bWrap.context().app().chainManager()
                .getExecuteHandler(c, mWrap.getParamWraps().length);


        //分析参数
        Object[] args = executeHandler.resolveArguments(c, target, mWrap);

        //参数提交确认
        bWrap.context().app().chainManager().postArguments(c, mWrap.getParamWraps(), args);

        //质变行
        return mWrap.invokeByAspect(target, args);
    }

    public void render(Object obj, Context c, boolean allowMultiple) throws Throwable {
        renderDo(obj, c, allowMultiple);
    }

    protected void renderDo(Object obj, Context c) throws Throwable {
        renderDo(obj, c, false);
    }

    /**
     * 执行渲染（便于重写）
     */
    protected void renderDo(Object obj, Context c, boolean allowMultiple) throws Throwable {
        //
        //可以通过before关掉render
        //
        obj = bWrap.context().app().chainManager().postResult(c, obj);

        if (allowMultiple || c.getRendered() == false) {
            c.result = obj;
        }


        if (bRender == null) {
            //没有代理时，跳过 DataThrowable
            if (obj instanceof DataThrowable) {
                return;
            }

            if (obj instanceof Throwable) {
                if (c.remoting()) {
                    //尝试推送异常，不然没机会记录；也可对后继做控制
                    Throwable objE = (Throwable) obj;
                    LogUtil.global().warn("Action remoting handle failed: " + c.pathNew(), objE);

                    if (allowMultiple || c.getRendered() == false) {
                        c.render(obj);
                    }
                } else {
                    c.setHandled(false); //传递给 filter, 可以统一处理未知异常
                    throw (Throwable) obj;
                }
            } else {
                if (allowMultiple || c.getRendered() == false) {
                    c.render(obj);
                }
            }
        } else {
            //是否再渲染或处理，由代理内部控制
            bRender.render(obj, c);
        }
    }

    @Override
    public String toString() {
        return "Mapping: " + fullName();
    }
}