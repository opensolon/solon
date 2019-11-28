package org.noear.solon.core;

import org.noear.solon.XUtil;

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
    private boolean _c_remoting;
    private Method _m;
    private String _produces;

    private PathAnalyzer _pr;//路径分析器
    private List<String> _pks;
    private static Pattern _pkr = Pattern.compile("\\{([^\\\\}]+)\\}");

    public XAction(BeanWrap beanWrap, boolean is_remoting, String produces, Method method, String path) {
        _c_bw = beanWrap;
        _c_remoting = is_remoting;
        _m = method;

        _produces = produces;

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
            x.attrSet("solon.reader.mode", "serialize");
        }

        try {
            if (XUtil.isEmpty(_produces) == false) {
                x.contentType(_produces);
            }

            do_handle(x);
        } catch (Throwable ex) {
            x.attrSet("error", ex);
            x.render(ex);
            XMonitorUtil.sendError(x, ex);
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

                x.render(XActionUtil.exeMethod(_c_bw.get(), _m, x));
            }catch (Throwable ex){
                x.attrSet("error", ex);
                x.render(ex);
                XMonitorUtil.sendError(x, ex);
            }
        }

        //后置处理
        for (XHandler h : _after) {
            h.handle(x);
        }
    }
}
