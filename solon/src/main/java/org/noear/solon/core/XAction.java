package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.ext.RunnableEx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mvc:动作
 * */
public class XAction extends XHandlerAide implements XHandler{
    protected final BeanWrap _bw;//
    protected final MethodWrap _mw;
    protected String _produces;//输出产品
    protected XRender _render;
    protected boolean _poi_main;

    private String _name;
    private boolean _remoting;

    private PathAnalyzer _pr;//路径分析器
    private List<String> _pks;
    private static Pattern _pkr = Pattern.compile("\\{([^\\\\}]+)\\}");

    public XAction(BeanWrap bw, boolean poi_main, Method m, XMapping mp, String path, boolean remoting, XRender render) {
        _bw = bw;
        _mw = MethodWrap.get(m);

        _remoting = remoting;
        _render = render;
        _poi_main = poi_main;

        if (mp != null) {
            _produces = mp.produces();
            _name = mp.value();
        } else {
            _name = m.getName();
        }

        //支持path变量
        if (path != null && path.indexOf("{") >= 0) {
            _pks = new ArrayList<>();
            Matcher pm = _pkr.matcher(path);
            while (pm.find()) {
                _pks.add(pm.group(1));
            }

            if (_pks.size() > 0) {
                _pr = new PathAnalyzer(path);
            }
        }
    }

    /**
     * 接口名称
     */
    public String name() {
        return _name;
    }

    /**
     * 函数包装器
     */
    public MethodWrap method() {
        return _mw;
    }

    /**
     * 控制器类包装
     */
    public BeanWrap bean() {
        return _bw;
    }

    @Override
    public void handle(XContext x) throws Throwable {
        invoke(x, null);
    }

    /**
     * 调用
     */
    public void invoke(XContext x, Object obj) throws Throwable {
        x.remotingSet(_remoting);

        try {
            //预加载控制器，确保所有的处理者可以都可以获取控制器
            if (obj == null) {
                obj = _bw.get();
            }

            if (_poi_main) {
                //传递控制器实例
                x.attrSet("controller", obj);
                x.attrSet("action",this);
            }

            invoke0(x, obj);
        } catch (Throwable ex) {
            x.attrSet("error", ex);
            x.render(ex);
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
        if (_poi_main) {
            handleDo(x, () -> {

                for (XHandler h : XApp.global().router().atBefore()) {
                    h.handle(x);
                }

                for (XHandler h : _before) {
                    h.handle(x);
                }
            });
        }


        //主体处理（最多一次渲染）
        if (x.getHandled() == false) {
            handleDo(x, () -> {
                //获取path var
                if (_pr != null) {
                    Matcher pm = _pr.matcher(x.path());
                    if (pm.find()) {
                        for (int i = 0, len = _pks.size(); i < len; i++) {
                            x.paramSet(_pks.get(i), pm.group(i + 1));//不采用group name,可解决_的问题
                        }
                    }
                }

                Object tmp = callDo(x, obj, _mw);

                //如果是主处理（不支持非主控的返回值；有可能是拦截器）
                if (_poi_main) {

                    //记录返回值（后续不一定会再记录）
                    x.result = tmp;

                    //设定输出产品（放在这个位置正好）
                    if (XUtil.isEmpty(_produces) == false) {
                        x.contentType(_produces);
                    }

                    renderDo(tmp, x);
                }
            });
        }

        //后置处理
        if (_poi_main) {
            for (XHandler h : XApp.global().router().atAfter()) {
                h.handle(x);
            }

            for (XHandler h : _after) {
                h.handle(x);
            }
        }
    }

    protected void handleDo(XContext c, RunnableEx runnable) throws Throwable {
        try {
            runnable.run();
        } catch (DataThrowable ex) {
            c.setHandled(true); //停止处理

            renderDo(ex, c);
        } catch (Throwable ex) {
            if(ex instanceof RuntimeException) {
                if (ex.getCause() != null) {
                    ex = ex.getCause();
                }
            }

            if (ex instanceof InvocationTargetException) {
                ex = ((InvocationTargetException) ex).getTargetException();
            }

            c.setHandled(true); //停止处理

            c.attrSet("error", ex);
            renderDo(ex, c);
            XEventBus.push(ex);
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

        if (_render == null) {
            x.setRendered(true); //最多一次渲染
            x.render(result);
        } else {
            _render.render(result, x);
        }
    }
}
