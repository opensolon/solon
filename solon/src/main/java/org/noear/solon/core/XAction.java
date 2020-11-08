package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.ext.DataThrowable;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.util.PathAnalyzer;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.ext.RunnableEx;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mvc:动作
 *
 * @author noear
 * @since 1.0
 * */
public class XAction extends XHandlerAide implements XHandler {
    //bean 包装器
    private final BeanWrap bWrap;
    //bean 相关aide
    private final XHandlerAide bAide;
    //bean 相关reader
    private XRender bRender;

    //method 是否为 main endpoint
    private final boolean mIsMain;
    //method 包装器
    private final MethodWrap mWrap;
    //method 相关的 produces（输出产品）
    private String mProduces;
    //action name
    private final String mName;
    //action remoting
    private final boolean mRemoting;

    //path 分析器
    private PathAnalyzer pathAnalyzer;//路径分析器
    //path key 列表
    private List<String> pathKeys;
    //path key 表达式
    private static Pattern pathKeyExpr = Pattern.compile("\\{([^\\\\}]+)\\}");

    public XAction(BeanWrap bWrap, XHandlerAide bAide, Method method, XMapping mapping, String path, boolean remoting, XRender render) {
        this.bWrap = bWrap;
        this.bAide = bAide;

        mWrap = MethodWrap.get(method);
        mRemoting = remoting;
        bRender = render;

        if (bRender == null) {
            //如果控制器是XRender
            if (XRender.class.isAssignableFrom(bWrap.clz())) {
                bRender = bWrap.raw();
            }
        }

        if (mapping == null) {
            mName = method.getName();
            mIsMain = true;
        } else {
            mProduces = mapping.produces();
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

    @Override
    public void handle(XContext x) throws Throwable {
        invoke(x, null);
    }

    /**
     * 调用
     */
    public void invoke(XContext x, Object obj) throws Throwable {
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
            ex = XUtil.throwableUnwrap(ex);

            x.attrSet("error", ex);
            renderDo(ex, x);
            XEventBus.push(ex);
        }
    }


    protected void invoke0(XContext x, Object obj) throws Throwable {

        /**
         * 1.确保所有处理者，能拿到控制器
         * 2.确保后置处理者，能被触发（前面的异常不能影响后置处理）
         * 3.确保最多一次渲染
         * */

        //前置处理（最多一次渲染）
        if (mIsMain) {
            handleDo(x, () -> {
                for (XHandler h : bAide.befores) {
                    h.handle(x);
                }

                for (XHandler h : befores) {
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
                    if (XUtil.isEmpty(mProduces) == false) {
                        x.contentType(mProduces);
                    }

                    renderDo(tmp, x);
                }
            });
        }

        //后置处理
        if (mIsMain) {
            for (XHandler h : bAide.afters) {
                h.handle(x);
            }

            for (XHandler h : afters) {
                h.handle(x);
            }
        }
    }

    protected void handleDo(XContext c, RunnableEx runnable) throws Throwable {
        try {
            runnable.run();
        } catch (Throwable ex) {
            c.setHandled(true); //停止处理

            ex = XUtil.throwableUnwrap(ex);

            if (ex instanceof DataThrowable) {
                renderDo(ex, c);
            } else {
                c.attrSet("error", ex);
                renderDo(ex, c);
                XEventBus.push(ex);
            }
        }
    }

    /**
     * 执行动作（便于重写）
     */
    protected Object callDo(XContext ctx, Object obj, MethodWrap mWrap) throws Throwable {
        String ct = ctx.contentType();

        for (XActionExecutor me : XBridge.actionExecutors()) {
            if (me.matched(ctx, ct)) {
                return me.execute(ctx, obj, mWrap);
            }
        }

        return XBridge.actionExecutorDef().execute(ctx, obj, mWrap);
    }

    /**
     * 执行渲染（便于重写）
     */
    protected void renderDo(Object result, XContext x) throws Throwable {
        //可以通过before关掉render
        if (x.getRendered()) {
            return;
        }

        x.result = result;

        if (bRender == null) {
            //最多一次渲染
            x.setRendered(true);

            if (result instanceof Throwable) {
                if (x.remoting()) {
                    x.render(result);
                }else {
                    x.statusSet(500);

                    if (XApp.cfg().isDebugMode()) {
                        x.output(XUtil.getFullStackTrace(((Throwable) result)));
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
