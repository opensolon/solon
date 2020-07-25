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
public class XAction extends XHandlerAide implements XHandler {
    protected final BeanWrap _bw;//
    protected final MethodWrap _mw;
    protected String _produces;//输出产品

    private String _name;
    private boolean _remoting;

    private PathAnalyzer _pr;//路径分析器
    private List<String> _pks;
    private static Pattern _pkr = Pattern.compile("\\{([^\\\\}]+)\\}");

    public XAction(BeanWrap bw, Method m, XMapping mp, String path, boolean remoting) {
        _bw = bw;
        _mw = MethodWrap.get(m);

        _remoting = remoting;

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

    @Override
    public void handle(XContext x) throws Throwable {
        x.remotingSet(_remoting);

        try {
            if (XUtil.isEmpty(_produces) == false) {
                x.contentType(_produces);
            }

            do_handle(x);
        } catch (Throwable ex) {
            x.attrSet("error", ex);
            x.render(ex);
            XMonitor.sendError(x, ex);
        }
    }

    private void do_handle(XContext x) throws Throwable {
        //前置处理
        for (XHandler h : _before) {
            h.handle(x);
        }

        if (x.getHandled() == false) {
            try {
                if (_pr != null) {
                    Matcher pm = _pr.matcher(x.path());
                    if (pm.find()) {
                        for (int i = 0, len = _pks.size(); i < len; i++) {
                            x.paramSet(_pks.get(i), pm.group(i + 1));//不采用group name,可解决_的问题
                        }
                    }
                }

                renderDo(x, callDo(x));
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
    protected Object callDo(XContext x) throws Throwable {
        return XActionUtil.exeMethod(_bw.get(), _mw, x);
    }

    /**
     * 执行渲染（便于重写）
     */
    protected void renderDo(XContext x, Object result) throws Throwable {
        x.render(result);
    }
}
