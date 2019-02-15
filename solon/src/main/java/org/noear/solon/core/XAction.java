package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mvc:动作
 * */
public class XAction extends XHandlerAide implements XHandler, XRender {
    private BeanWrap _c_bw;//
    private boolean _c_rpc;
    private Method _m;

    private Pattern _pr;
    private List<String> _pks;
    private static Pattern _pkr = Pattern.compile("\\{([^\\\\}]+)\\}");

    private XRender _reader;

    public XAction(BeanWrap beanWrap, XRender reader, boolean is_rpc, Method method, String path) {
        _c_bw = beanWrap;
        _c_rpc = is_rpc;
        _m = method;
        _reader = reader;

        //支持path变量
        if (path.indexOf("{") >= 0) {
            _pks = new ArrayList<>();
            Matcher pm = _pkr.matcher(path);
            while (pm.find()) {
                _pks.add(pm.group(1));
            }

            if (_pks.size() > 0) {
                _pr = Pattern.compile(XUtil.expCompile(path), Pattern.CASE_INSENSITIVE);
            }
        }
    }

    @Override
    public void render(Object obj, XContext ctx) throws Exception {
        if (obj != null) {
            if (_reader == null) {
                XApp.global().render(obj, ctx);
            } else {
                _reader.render(obj, ctx);
            }
        }
    }

    @Override
    public void handle(XContext x) throws Exception {
        if(_c_rpc) {
            x.attrSet("solon.reader.source", "service");
        }

        try{
            do_handle(x);
        }catch (Exception ex){
            render(ex, x);
        }
    }

    private void do_handle(XContext x) throws Exception {
        for (XHandler h : _before) {
            h.handle(x);
        }

        if (x.getHandled() == false) {
            if (_pr != null) {
                Matcher pm = _pr.matcher(x.path());
                if (pm.find()) {
                    for (int i = 0, len = _pks.size(); i < len; i++) {
                        x.paramSet(_pks.get(i), pm.group(i + 1));//不采用group name,可解决_的问题
                    }
                }
            }

            render(XActionUtil.exeMethod(_c_bw.get(), _m, x), x);
        }

        for (XHandler h : _after) {
            h.handle(x);
        }
    }
}
