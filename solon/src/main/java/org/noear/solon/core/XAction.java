package org.noear.solon.core;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mvc:动作
 * */
public class XAction extends XHandlerAide {
    protected final BeanWrap _bw;//
    protected final MethodWrap _mw;
    protected String _produces;//输出产品
    protected XRender _render;
    protected int _poi; //endpoint

    private String _name;
    private boolean _remoting;

    private PathAnalyzer _pr;//路径分析器
    private List<String> _pks;
    private static Pattern _pkr = Pattern.compile("\\{([^\\\\}]+)\\}");

    public XAction(BeanWrap bw, Method m, int poi, XMapping mp, String path, boolean remoting, XRender render) {
        _bw = bw;
        _mw = MethodWrap.get(m);

        _poi = poi;
        _remoting = remoting;
        _render = render;

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
     * 获取一个控制器实例
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
            //前置加载控制器（用于拦截器获取）
            if (obj == null) {
                obj = _bw.get();
                if (_poi == XEndpoint.main) {
                    //传递控制器实例
                    x.attrSet("controller", obj);
                }
            }

            invoke0(x, obj);
        } catch (Throwable ex) {
            x.attrSet("error", ex);
            x.render(ex);
            XMonitor.sendError(x, ex);
        }
    }


    protected void invoke0(XContext x, Object obj) throws Throwable {

        /**
         * 1.确保所有处理者，能拿到控制器
         * 2.确保后置处理者，能被触发（前面的异常不能影响后置处理）
         * */

        //前置处理
        for (XHandler h : _before) {
            try {
                h.handle(x);
            } catch (DataThrowable ex) {
                //数据抛出，不进入异常系统
                //
                x.setHandled(true); //停止处理

                renderDo(x, ex); //渲染数据
            } catch (Throwable ex) {
                x.setHandled(true); //停止处理

                x.attrSet("error", ex);
                renderDo(x, ex);
                XMonitor.sendError(x, ex);
            }
        }


        //主体处理
        if (x.getHandled() == false) {
            try {
                //获取path var
                if (_pr != null) {
                    Matcher pm = _pr.matcher(x.path());
                    if (pm.find()) {
                        for (int i = 0, len = _pks.size(); i < len; i++) {
                            x.paramSet(_pks.get(i), pm.group(i + 1));//不采用group name,可解决_的问题
                        }
                    }
                }

                //此处必须赋值；下面不一定再赋值
                x.result = callDo(x, obj);

                //成功后，控制输出产品（放在这个位置正好）
                if (XUtil.isEmpty(_produces) == false) {
                    x.contentType(_produces);
                }

                renderDo(x, x.result);
            } catch (DataThrowable ex) {
                //数据抛出，不进入异常系统
                renderDo(x, ex);
            } catch (Throwable ex) {
                x.attrSet("error", ex);
                renderDo(x, ex);
                XMonitor.sendError(x, ex);
            }
        }

        //后置处理
        for (XHandler h : _after) {
            h.handle(x);
        }
    }

    /**
     * 执行动作（便于重写）
     */
    protected Object callDo(XContext x, Object obj) throws Throwable {
        return XActionUtil.exeMethod(x, obj, _mw);
    }

    /**
     * 执行渲染（便于重写）
     */
    protected void renderDo(XContext x, Object result) throws Throwable {
        //可以通过before关掉render
        if (x.getRendered()) {
            return;
        }

        x.result = result;

        if (_render == null) {
            x.render(result);
        } else {
            _render.render(result, x);
        }
    }
}
