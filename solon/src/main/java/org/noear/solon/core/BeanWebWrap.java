package org.noear.solon.core;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.*;
import org.noear.solon.ext.Act1Ex;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Bean 包装
 * */
public class BeanWebWrap {
    protected BeanWrap _bw;
    protected XMapping _cxm;
    protected int _poi = XEndpoint.main;
    protected String c_path;

    public BeanWebWrap(BeanWrap wrap) {
        _bw = wrap;
        _cxm = _bw.clz().getAnnotation(XMapping.class);
        if (_cxm != null) {
            c_path = _cxm.value();
        }
    }

    public BeanWebWrap(BeanWrap wrap, String expr) {
        _bw = wrap;
        if (expr != null) {
            c_path = expr;
        }
    }

    public String expr() {
        return c_path;
    }

    /**
     * 设置切入点
     */
    public void endpointSet(int endpoint) {
        _poi = endpoint;
    }

    public void load(XHandlerSlots slots) {
        if (XHandler.class.isAssignableFrom(_bw.clz())) {
            loadHandlerDo(slots, _bw, _cxm);
        } else {
            loadActionDo(slots);
        }
    }

    protected void loadHandlerDo(XHandlerSlots slots, BeanWrap bw, XMapping cxm) {
        if (cxm == null) {
            throw new RuntimeException(bw.clz().getName() + " No XMapping!");
        }

        for (XMethod m1 : cxm.method()) {
            switch (_poi) {
                case XEndpoint.before:
                    slots.before(cxm.value(), m1, cxm.index(), bw.raw());
                    break;
                case XEndpoint.after:
                    slots.after(cxm.value(), m1, cxm.index(), bw.raw());
                    break;
                default:
                    slots.add(cxm.value(), m1, bw.raw());
            }
        }
    }

    private void loadActionDo(XHandlerSlots slots) {
        String m_path;

        if(c_path == null){
            c_path = "";
        }

        XBefore c_befs = _bw.clz().getAnnotation(XBefore.class);
        XAfter c_afts = _bw.clz().getAnnotation(XAfter.class);
        List<XHandler> c_befs2 = new ArrayList<>();
        List<XHandler> c_afts2 = new ArrayList<>();

        if (c_befs != null) {
            addDo(c_befs.value(), (b) -> c_befs2.add(b.newInstance()));
        }
        if (c_afts != null) {
            addDo(c_afts.value(), (f) -> c_afts2.add(f.newInstance()));
        }

        XMethod[] m_method;
        XMapping m_map;
        XBefore m_befores;
        XAfter m_afters;
        int m_index = 0;

        //只支持public函数为XAction
        for (Method method : _bw.clz().getDeclaredMethods()) {
            m_map = method.getAnnotation(XMapping.class);
            m_befores = method.getAnnotation(XBefore.class);
            m_afters = method.getAnnotation(XAfter.class);
            m_index = 0;

            //构建path and method
            if (m_map != null) {
                m_path = m_map.value();
                m_method = m_map.method();
                m_index = m_map.index();
            } else {
                m_path = method.getName();
                if (_cxm == null) {
                    m_method = new XMethod[]{XMethod.HTTP};
                } else {
                    m_method = _cxm.method();
                }
            }

            //如果是service，method 就不需要map
            if (m_map != null || _bw.remoting()) {
                String newPath = XUtil.mergePath(c_path, m_path);

                XAction action = createAction(_bw,method, m_map,newPath);

                //加载控制器的前置拦截器
                addDo(c_befs2.toArray(), (b) -> action.before((XHandler) b));
                if (m_befores != null) {
                    addDo(m_befores.value(), (b) -> action.before(b.newInstance()));
                }

                //加载控制器的后置拦截器
                addDo(c_afts2.toArray(), (f) -> action.after((XHandler) f));
                if (m_afters != null) {
                    addDo(m_afters.value(), (f) -> action.after(f.newInstance()));
                }

                for (XMethod m1 : m_method) {
                    switch (_poi) {
                        case XEndpoint.before:
                            slots.before(newPath, m1, m_index, action);
                            break;
                        case XEndpoint.after:
                            slots.after(newPath, m1, m_index, action);
                            break;
                        default:
                            slots.add(newPath, m1, action);
                    }
                }
            }
        }
    }

    protected XAction createAction(BeanWrap bw,  Method method, XMapping mp, String path){
        return new XAction(bw, method, mp, path);
    }

    /**
     * 附加触发器（前后置处理）
     */
    private static <T> void addDo(T[] ary, Act1Ex<T> fun) {
        if (ary != null) {
            for (T t : ary) {
                try {
                    fun.run(t);
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
