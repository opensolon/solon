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
    protected XMapping c_map;
    protected int c_poi = XEndpoint.main;
    protected String c_path;

    public BeanWebWrap(BeanWrap wrap) {
        _bw = wrap;
        c_map = _bw.clz().getAnnotation(XMapping.class);
        if (c_map != null) {
            c_path = c_map.value();
        }
    }

    public BeanWebWrap(BeanWrap wrap, String mapping) {
        _bw = wrap;
        if (mapping != null) {
            c_path = mapping;
        }
    }

    /**
     * mapping expr
     * */
    public String mapping() {
        return c_path;
    }

    /**
     * 设置切入点
     */
    public void endpointSet(int endpoint) {
        c_poi = endpoint;
    }

    /**
     * 加载 XAction 到目标容器
     *
     * @param slots 接收加载结果的容器（槽）
     * */
    public void load(XHandlerSlots slots) {
        load(_bw.remoting(), slots);
    }

    /**
     * 加载 XAction 到目标容器
     *
     * @param all 加载全部函数（一般 remoting 会全部加载）
     * @param slots 接收加载结果的容器（槽）
     * */
    public void load(boolean all, XHandlerSlots slots) {
        if (XHandler.class.isAssignableFrom(_bw.clz())) {
            loadHandlerDo(slots);
        } else {
            loadActionDo(slots, all || _bw.remoting());
        }
    }

    /**
     * 加载处理
     * */
    protected void loadHandlerDo(XHandlerSlots slots) {
        if (c_map == null) {
            throw new RuntimeException(_bw.clz().getName() + " No XMapping!");
        }

        for (XMethod m1 : c_map.method()) {
            switch (c_poi) {
                case XEndpoint.before:
                    slots.before(c_map.value(), m1, c_map.index(), _bw.raw());
                    break;
                case XEndpoint.after:
                    slots.after(c_map.value(), m1, c_map.index(), _bw.raw());
                    break;
                default:
                    slots.add(c_map.value(), m1, _bw.raw());
            }
        }
    }

    /**
     * 加载 XAction 处理
     * */
    protected void loadActionDo(XHandlerSlots slots, boolean all) {
        String m_path;

        if(c_path == null){
            c_path = "";
        }

        XBefore c_befs = _bw.clz().getAnnotation(XBefore.class);
        XAfter c_afts = _bw.clz().getAnnotation(XAfter.class);
        List<XHandler> c_befs2 = new ArrayList<>();
        List<XHandler> c_afts2 = new ArrayList<>();

        if (c_befs != null) {
            addDo(c_befs.value(), (b) -> c_befs2.add(Aop.get(b)));
        }
        if (c_afts != null) {
            addDo(c_afts.value(), (f) -> c_afts2.add(Aop.get(f)));
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
                if (c_map == null) {
                    m_method = new XMethod[]{XMethod.HTTP};
                } else {
                    m_method = c_map.method();
                }
            }

            //如果是service，method 就不需要map
            if (m_map != null || all) {
                String newPath = XUtil.mergePath(c_path, m_path);

                XAction action = action(_bw,method, m_map,newPath);

                //加载控制器的前置拦截器
                addDo(c_befs2.toArray(), (b) -> action.before((XHandler) b));
                if (m_befores != null) {
                    addDo(m_befores.value(), (b) -> action.before(Aop.get(b)));
                }

                //加载控制器的后置拦截器
                addDo(c_afts2.toArray(), (f) -> action.after((XHandler) f));
                if (m_afters != null) {
                    addDo(m_afters.value(), (f) -> action.after(Aop.get(f)));
                }

                for (XMethod m1 : m_method) {
                    switch (c_poi) {
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

    /**
     * 构建 XAction
     * */
    protected XAction action(BeanWrap bw, Method method, XMapping mp, String path){
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
