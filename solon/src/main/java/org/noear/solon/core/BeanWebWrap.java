package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.*;
import org.noear.solon.ext.Act1Ex;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Bean 包装
 * */
public class BeanWebWrap{
    protected BeanWrap _bw;
    protected XMapping _cxm;
    protected XRender _render = null;
    protected int _poi = XEndpoint.main;
    protected boolean _rpc = false;

    public BeanWebWrap(BeanWrap wrap) {
        _bw = wrap;

        if (_bw.raw() instanceof XRender) { //支持自我渲染
            _render = _bw.raw();
        }

        _cxm = _bw.clz().getAnnotation(XMapping.class);
    }

    public void renderSet(XRender render){
        _render = render;
    }
    public void endpointSet(int endpoint){
        _poi  = endpoint;
    }
    public void rpcSet(boolean rpc){_rpc = rpc;}

    public void load(XApp app) {
        if (XHandler.class.isAssignableFrom(_bw.clz())) {
            do_loadHandler(app, _bw, _cxm);
        } else {
            do_loadAction(app);
        }
    }

    protected void do_loadHandler(XApp app, BeanWrap bw, XMapping cxm) {
        if (cxm == null) {
            throw new RuntimeException(bw.clz().getName() + " No XMapping!");
        }

        switch (_poi){
            case XEndpoint.before:app.before(cxm.value(), cxm.method(), bw.raw());break;
            case XEndpoint.after:app.after(cxm.value(), cxm.method(), bw.raw());break;
            default:app.add(cxm.value(), cxm.method(), bw.raw());
        }
    }

    private void do_loadAction(XApp app) {
        XMapping m_map = null;

        String c_path = "";
        String m_path, m_method;

        if (_cxm != null) {
            c_path = _cxm.value();
        }

        XBefore c_befs = _bw.clz().getAnnotation(XBefore.class);
        XAfter  c_afts = _bw.clz().getAnnotation(XAfter.class);
        List<XHandler> c_befs2 = new ArrayList<>();
        List<XHandler> c_afts2 = new ArrayList<>();

        if (c_befs != null) {
            do_add(c_befs.value(), (b) -> c_befs2.add(b.newInstance()));
        }
        if (c_afts != null) {
            do_add(c_afts.value(), (f) -> c_afts2.add(f.newInstance()));
        }

        XBefore m_befores;
        XAfter m_afters;

        //只支持public函数为XAction
        for (Method method : _bw.clz().getDeclaredMethods()) {
            m_map = method.getAnnotation(XMapping.class);
            m_befores = method.getAnnotation(XBefore.class);
            m_afters = method.getAnnotation(XAfter.class);

            //构建path and method
            if (m_map != null) {
                m_path = m_map.value();
                m_method = m_map.method();
            } else {
                m_path = method.getName();
                if (_cxm == null) {
                    m_method = XMethod.ALL;
                } else {
                    m_method = _cxm.method();
                }
            }

            //如果是service，method 就不需要map
            if (m_map != null || _rpc) {
                String newPath = XUtil.mergePath(c_path, m_path);

                XAction action = new XAction(_bw , _render, _rpc, method, newPath);

                //加载控制器的前置拦截器
                do_add(c_befs2.toArray(), (b) -> action.before((XHandler) b));
                if (m_befores != null) {
                    do_add(m_befores.value(), (b) -> action.before(b.newInstance()));
                }

                //加载控制器的后置拦截器
                do_add(c_afts2.toArray(), (f) -> action.after((XHandler) f));
                if (m_afters != null) {
                    do_add(m_afters.value(), (f) -> action.after(f.newInstance()));
                }

                switch (_poi){
                    case XEndpoint.before:app.before(newPath, m_method, action);break;
                    case XEndpoint.after:app.after(newPath, m_method, action);break;
                    default:app.add(newPath, m_method, action);
                }
            }
        }
    }

    /** 附加前后置处理 */
    private static <T> void do_add(T[] ary, Act1Ex<T> fun) {
        if (ary != null) {
            for (T t : ary) {
                try {
                    fun.run(t);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
