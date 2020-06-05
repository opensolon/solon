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
    private BeanWrap _c_bw;//
    private boolean _c_remoting;//是否为remoting
    private MethodWrap _mw;
    private XMapping _mp;
    private String _produces;//输出产品

    private PathAnalyzer _pr;//路径分析器
    private List<String> _pks;
    private static Pattern _pkr = Pattern.compile("\\{([^\\\\}]+)\\}");

    public XAction(BeanWrap beanWrap, Method method, XMapping mp, String path) {
        _c_bw = beanWrap;
        _c_remoting = beanWrap.remoting();
        _mw = MethodWrap.get(method);
        _mp = mp;

        if (_mp != null) {
            _produces = _mp.produces();
        }

        //支持path变量
        if (path.indexOf("{") >= 0) {
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

    @Override
    public void handle(XContext x) throws Throwable {
        if (_c_remoting) {
            x.remotingSet(_c_remoting);
        }

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

                innerRender(x, XActionUtil.exeMethod(_c_bw.get(), _mw, x));
            } catch (Throwable ex) {
                x.attrSet("error", ex);
                innerRender(x, ex);
                XMonitor.sendError(x, ex);
            }
        }

        //后置处理
        for (XHandler h : _after) {
            h.handle(x);
        }
    }

    protected XMapping mapping(){
        return _mp;
    }

    protected void innerRender(XContext x, Object result) throws Throwable {
        x.render(result);
    }
}
