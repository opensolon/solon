package org.noear.solon.core.handle;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.ext.DataThrowable;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.util.PathAnalyzer;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.ext.RunnableEx;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //path 分析器
    private PathAnalyzer pathAnalyzer;//路径分析器
    //path key 列表
    private List<String> pathKeys;
    //path key 表达式
    private static Pattern pathKeyExpr = Pattern.compile("\\{([^\\\\}]+)\\}");

    public Action(BeanWrap bWrap, HandlerAide bAide, Method method, Mapping mapping, String path, boolean remoting, Render render) {
        this.bWrap = bWrap;
        this.bAide = bAide;

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
            mName = mapping.value();
            mIsMain = !(mapping.after() || mapping.before());
        }

        //支持path变量
        if (path != null && path.indexOf("{") >= 0) {
            pathKeys = new ArrayList<>();
            Matcher pm = pathKeyExpr.matcher(path);
            while (pm.find()) {
                pathKeys.add(pm.group(1));
            }

            if (pathKeys.size() > 0) {
                pathAnalyzer = new PathAnalyzer(path);
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
     * */
    public Mapping mapping(){
        return mMapping;
    }

    /**
     * 函数包装器
     */
    public MethodWrap method() {
        return mWrap;
    }

    /**
     * 控制器类包装
     */
    public BeanWrap bean() {
        return bWrap;
    }

    public String produces() {
        return mProduces;
    }

    public String consumes() {
        return mConsumes;
    }

    @Override
    public void handle(Context x) throws Throwable {
        if (Utils.isNotEmpty(mConsumes)) {
            if (x.contentType() == null || x.contentType().contains(mConsumes) == false) {
                x.statusSet(415);
                return;
            }
        }

        invoke(x, null);
    }

    /**
     * 调用
     */
    public void invoke(Context x, Object obj) throws Throwable {
        x.remotingSet(mRemoting);

        try {
            //预加载控制器，确保所有的处理者可以都可以获取控制器
            if (obj == null) {
                obj = bWrap.get();
            }

            if (mIsMain) {
                //传递控制器实例
                x.attrSet("controller", obj);
                x.attrSet("action", this);
            }

            invoke0(x, obj);
        } catch (Throwable ex) {
            ex = Utils.throwableUnwrap(ex);
            x.errors = ex;

            renderDo(ex, x);
            EventBus.push(ex);
        }
    }


    protected void invoke0(Context x, Object obj) throws Throwable {

        /**
         * 1.确保所有处理者，能拿到控制器
         * 2.确保后置处理者，能被触发（前面的异常不能影响后置处理）
         * 3.确保最多一次渲染
         * */

        //前置处理（最多一次渲染）
        if (mIsMain) {
            handleDo(x, () -> {
                for (Handler h : bAide.befores) {
                    h.handle(x);
                }

                for (Handler h : befores) {
                    h.handle(x);
                }
            });
        }


        //主体处理（最多一次渲染）
        if (x.getHandled() == false) {
            handleDo(x, () -> {
                //获取path var
                if (pathAnalyzer != null) {
                    Matcher pm = pathAnalyzer.matcher(x.path());
                    if (pm.find()) {
                        for (int i = 0, len = pathKeys.size(); i < len; i++) {
                            x.paramSet(pathKeys.get(i), pm.group(i + 1));//不采用group name,可解决_的问题
                        }
                    }
                }

                Object tmp = callDo(x, obj, mWrap);

                //如果是主处理（不支持非主控的返回值；有可能是拦截器）
                if (mIsMain) {

                    //记录返回值（后续不一定会再记录）
                    x.result = tmp;

                    //设定输出产品（放在这个位置正好）
                    if (Utils.isEmpty(mProduces) == false) {
                        x.contentType(mProduces);
                    }

                    renderDo(tmp, x);
                }
            });
        }

        //后置处理
        if (mIsMain) {
            for (Handler h : bAide.afters) {
                h.handle(x);
            }

            for (Handler h : afters) {
                h.handle(x);
            }
        }
    }

    protected void handleDo(Context c, RunnableEx runnable) throws Throwable {
        try {
            runnable.run();
        } catch (Throwable ex) {
            c.setHandled(true); //停止处理

            ex = Utils.throwableUnwrap(ex);

            if (ex instanceof DataThrowable) {
                renderDo(ex, c);
            } else {
                c.errors = ex;

                renderDo(ex, c);
                EventBus.push(ex);
            }
        }
    }

    /**
     * 执行动作（便于重写）
     */
    protected Object callDo(Context ctx, Object obj, MethodWrap mWrap) throws Throwable {
        String ct = ctx.contentType();

        for (ActionExecutor me : Bridge.actionExecutors()) {
            if (me.matched(ctx, ct)) {
                return me.execute(ctx, obj, mWrap);
            }
        }

        return Bridge.actionExecutorDef().execute(ctx, obj, mWrap);
    }

    /**
     * 执行渲染（便于重写）
     */
    protected void renderDo(Object result, Context x) throws Throwable {
        //
        //可以通过before关掉render
        //
        if (x.getRendered()) {
            return;
        }

        x.result = result;

        if (bRender == null) {
            //
            //最多一次渲染
            //
            x.setRendered(true);

            if (result instanceof Throwable) {
                if (x.remoting()) {
                    x.render(result);
                } else {
                    if (x.status() < 400) {
                        x.statusSet(500);
                    }

                    if (Solon.cfg().isDebugMode()) {
                        x.output(Utils.getFullStackTrace(((Throwable) result)));
                    }
                }
            } else {
                x.render(result);
            }
        } else {
            bRender.render(result, x);
        }
    }
}
