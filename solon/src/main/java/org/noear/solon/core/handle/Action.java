package org.noear.solon.core.handle;

import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.ext.DataThrowable;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.util.PathAnalyzer;
import org.noear.solon.annotation.Mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * mvc:动作
 *
 * @author noear
 * @since 1.0
 * */
public class Action extends HandlerAide implements Handler {
    //bean 包装器
    private final BeanWrap bWrap;
    //bean 相关aide
    private final HandlerAide bAide;
    //bean 相关reader
    private Render bRender;

    //method 是否为 main endpoint
    private final boolean mIsMain;
    //method 包装器
    private final MethodWrap mWrap;
    //method 相关的 produces（输出产品）
    private String mProduces;
    //method 相关的 consumes（输入产品）
    private String mConsumes;
    //action name
    private final String mName;
    //action remoting
    private final boolean mRemoting;
    private final Mapping mMapping;

    private boolean mMultipart;

    //path 分析器
    private PathAnalyzer pathAnalyzer;//路径分析器
    //path key 列表
    private List<String> pathKeys;

    public Action(BeanWrap bWrap, Method method) {
        this(bWrap, null, method, null, null, false, null);
    }

    public Action(BeanWrap bWrap, HandlerAide bAide, Method method, Mapping mapping, String path, boolean remoting, Render render) {
        this.bWrap = bWrap;
        this.bAide = bAide;

        method.setAccessible(true);

        mWrap = MethodWrap.get(method);
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
            mIsMain = true;
        } else {
            mProduces = mapping.produces();
            mConsumes = mapping.consumes();
            mMultipart = mapping.multipart();
            mName = Utils.annoAlias(mapping.value(), mapping.path());
            mIsMain = !(mapping.after() || mapping.before());
        }

        //支持多分片申明
        if(mMultipart == false) {
            for (Class<?> clz : method.getParameterTypes()) {
                if (UploadedFile.class.isAssignableFrom(clz)) {
                    mMultipart = true;
                }
            }
        }

        //支持path变量
        if (path != null && path.indexOf("{") >= 0) {
            pathKeys = new ArrayList<>();
            Matcher pm = PathUtil.pathKeyExpr.matcher(path);
            while (pm.find()) {
                pathKeys.add(pm.group(1));
            }

            if (pathKeys.size() > 0) {
                pathAnalyzer = PathAnalyzer.get(path);
            }
        }
    }

    /**
     * 接口名称
     */
    public String name() {
        return mName;
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
     * 生产者
     */
    public String produces() {
        return mProduces;
    }

    /**
     * 消息费
     */
    public String consumes() {
        return mConsumes;
    }


    @Override
    public void handle(Context x) throws Throwable {
        if (Utils.isNotEmpty(mConsumes)) {
            if (x.contentType() == null || x.contentType().contains(mConsumes) == false) {
                x.status(415);
                return;
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
            //预加载控制器，确保所有的处理者可以都可以获取控制器
            if (obj == null) {
                obj = bWrap.get();
            }

            if (mIsMain) {
                //传递控制器实例
                c.attrSet("controller", obj);
                c.attrSet("action", this);
            }

            invoke0(c, obj);
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

                //1.推送事件（先于渲染，可做自定义渲染）
                EventBus.push(e);

                //2.渲染
                if (c.result == null) {
                    renderDo(e, c);
                } else {
                    renderDo(c.result, c);
                }
            }
        }
    }


    /**
     * 执行内部调用
     */
    protected void invoke0(Context c, Object obj) throws Throwable {

        /**
         * 1.确保所有处理者，能拿到控制器
         * 2.确保后置处理者，能被触发（前面的异常不能影响后置处理）
         * 3.确保最多一次渲染
         * */

        try {
            //前置处理（最多一次渲染）
            if (mIsMain) {
                if (bAide != null) {
                    for (Handler h : bAide.befores) {
                        h.handle(c);
                    }
                }

                for (Handler h : befores) {
                    h.handle(c);
                }
            }


            //主体处理（最多一次渲染）//非主体处理 或 未处理
            if (mIsMain == false || c.getHandled() == false) {

                //获取path var
                if (pathAnalyzer != null) {
                    Matcher pm = pathAnalyzer.matcher(c.pathNew());
                    if (pm.find()) {
                        for (int i = 0, len = pathKeys.size(); i < len; i++) {
                            c.paramSet(pathKeys.get(i), pm.group(i + 1));//不采用group name,可解决_的问题
                        }
                    }
                }

                Object tmp = callDo(c, obj, mWrap);

                //如果是主处理（不支持非主控的返回值；有可能是拦截器）
                if (mIsMain) {

                    //记录返回值（后续不一定会再记录）
                    c.result = tmp;

                    //设定输出产品（放在这个位置正好）
                    if (Utils.isEmpty(mProduces) == false) {
                        c.contentType(mProduces);
                    }

                    renderDo(tmp, c);
                }
            }
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);

            if (e instanceof DataThrowable) {
                DataThrowable ex = (DataThrowable)e;
                if (ex.data() == null) {
                    renderDo(ex, c);
                } else {
                    renderDo(ex.data(), c);
                }
            } else {
                c.errors = e; //为 afters，留个参考
                throw e;
            }
        } finally {
            //后置处理
            if (mIsMain) {
                if (bAide != null) {
                    for (Handler h : bAide.afters) {
                        h.handle(c);
                    }
                }

                for (Handler h : afters) {
                    h.handle(c);
                }
            }
        }
    }

    /**
     * 执行动作（便于重写）
     */
    protected Object callDo(Context ctx, Object obj, MethodWrap mWrap) throws Throwable {
        String ct = ctx.contentType();

        if (mWrap.getParamWraps().length > 0) {
            //
            //仅有参数时，才执行执行其它执行器
            //
            for (ActionExecutor me : Bridge.actionExecutors()) {
                if (me.matched(ctx, ct)) {
                    return me.execute(ctx, obj, mWrap);
                }
            }
        }

        return Bridge.actionExecutorDef().execute(ctx, obj, mWrap);
    }

    /**
     * 执行渲染（便于重写）
     */
    protected void renderDo(Object obj, Context c) throws Throwable {
        //
        //可以通过before关掉render
        //
        if (c.getRendered()) {
            return;
        }

        c.result = obj;

        if (bRender == null) {
            //没有代理时，跳过 DataThrowable
            if (obj instanceof DataThrowable) {
                return;
            }

            if (obj instanceof Throwable) {
                if (c.remoting()) {
                    c.render(obj);
                } else {
                    c.setHandled(false); //透传到上传，让Filter 可以统一处理未知异常
                    throw (Throwable) obj;
                }
            } else {
                c.render(obj);
            }
        } else {
            bRender.render(obj, c);
        }
    }
}
