package org.noear.solon.core;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.*;
import org.noear.solon.ext.ConsumerEx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 通用处理接口加载器（根据bean加载）
 *
 * @author noear
 * @since 1.0
 * */
public class XHandlerLoader extends XHandlerAide {
    protected BeanWrap bw;
    protected XRender  bRender;
    protected XMapping bMapping;
    protected String   bPath;
    protected boolean  bRemoting;

    protected boolean allowMapping;

    public XHandlerLoader(BeanWrap wrap) {
        bMapping = wrap.clz().getAnnotation(XMapping.class);

        if (bMapping == null) {
            initDo(wrap, null, wrap.remoting(), null, true);
        } else {
            initDo(wrap, bMapping.value(), wrap.remoting(), null, true);
        }
    }

    public XHandlerLoader(BeanWrap wrap, String mapping) {
        initDo(wrap, mapping, wrap.remoting(), null, true);
    }

    public XHandlerLoader(BeanWrap wrap, String mapping, boolean remoting) {
        initDo(wrap, mapping, remoting, null, true);
    }

    public XHandlerLoader(BeanWrap wrap, String mapping, boolean remoting, XRender render, boolean allowMapping) {
        initDo(wrap, mapping, remoting, render, allowMapping);
    }

    private void initDo(BeanWrap wrap, String mapping, boolean remoting, XRender render, boolean allowMapping) {
        bw = wrap;
        bRender = render;
        this.allowMapping = allowMapping;

        if (mapping != null) {
            bPath = mapping;
        }

        bRemoting = remoting;
    }

    /**
     * mapping expr
     */
    public String mapping() {
        return bPath;
    }

    /**
     * 加载 XAction 到目标容器
     *
     * @param slots 接收加载结果的容器（槽）
     */
    public void load(XHandlerSlots slots) {
        load(bRemoting, slots);
    }

    /**
     * 加载 XAction 到目标容器
     *
     * @param all   加载全部函数（一般 remoting 会全部加载）
     * @param slots 接收加载结果的容器（槽）
     */
    public void load(boolean all, XHandlerSlots slots) {
        if (XHandler.class.isAssignableFrom(bw.clz())) {
            loadHandlerDo(slots);
        } else {
            loadActionDo(slots, all || bRemoting);
        }
    }

    /**
     * 加载处理
     */
    protected void loadHandlerDo(XHandlerSlots slots) {
        if (bMapping == null) {
            throw new RuntimeException(bw.clz().getName() + " No @XMapping!");
        }

        XHandler handler = bw.raw();
        slots.add(bMapping, handler);
    }


    /**
     * 加载 XAction 处理
     */
    protected void loadActionDo(XHandlerSlots slots, boolean all) {
        String m_path;

        if (bPath == null) {
            bPath = "";
        }

        loadControllerAide();

        XMethod[] m_method;
        XMapping m_map;
        int m_index = 0;

        //只支持public函数为XAction
        for (Method method : bw.clz().getDeclaredMethods()) {
            m_map = method.getAnnotation(XMapping.class);

            m_index = 0;

            //构建path and method
            if (m_map != null) {
                m_path = m_map.value();
                m_method = m_map.method();
                m_index = m_map.index();
            } else {
                m_path = method.getName();
                if (bMapping == null) {
                    m_method = new XMethod[]{XMethod.HTTP};
                } else {
                    m_method = bMapping.method();
                }
            }

            //如果是service，method 就不需要map
            if (m_map != null || all) {
                String newPath = XUtil.mergePath(bPath, m_path);

                XAction action = createAction(bw, method, m_map, newPath, bRemoting);

                loadActionAide(method, action);

                for (XMethod m1 : m_method) {
                    if (m_map == null) {
                        slots.add(newPath, m1, action);
                    }else{
                        if ((m_map.after() || m_map.before())) {
                            if (m_map.after()) {
                                slots.after(newPath, m1, m_index, action);
                            } else {
                                slots.before(newPath, m1, m_index, action);
                            }
                        } else {
                            slots.add(newPath, m1, action);
                        }
                    }
                }
            }
        }
    }


    protected void loadControllerAide() {
        for (Annotation anno : bw.clz().getAnnotations()) {
            if (anno instanceof XBefore) {
                addDo(((XBefore) anno).value(), (b) -> this.before(Aop.get(b)));
            } else if (anno instanceof XAfter) {
                addDo(((XAfter) anno).value(), (f) -> this.after(Aop.get(f)));
            } else {
                for (Annotation anno2 : anno.annotationType().getAnnotations()) {
                    if (anno2 instanceof XBefore) {
                        addDo(((XBefore) anno2).value(), (b) -> this.before(Aop.get(b)));
                    } else if (anno2 instanceof XAfter) {
                        addDo(((XAfter) anno2).value(), (f) -> this.after(Aop.get(f)));
                    }
                }
            }
        }
    }

    protected void loadActionAide(Method method, XAction action) {
        for (Annotation anno : method.getAnnotations()) {
            if (anno instanceof XBefore) {
                addDo(((XBefore) anno).value(), (b) -> action.before(Aop.get(b)));
            } else if (anno instanceof XAfter) {
                addDo(((XAfter) anno).value(), (f) -> action.after(Aop.get(f)));
            } else {
                for (Annotation anno2 : anno.annotationType().getAnnotations()) {
                    if (anno2 instanceof XBefore) {
                        addDo(((XBefore) anno2).value(), (b) -> action.before(Aop.get(b)));
                    } else if (anno2 instanceof XAfter) {
                        addDo(((XAfter) anno2).value(), (f) -> action.after(Aop.get(f)));
                    }
                }
            }
        }
    }

    /**
     * 构建 XAction
     */
    protected XAction createAction(BeanWrap bw, Method method, XMapping mp, String path, boolean remoting) {
        if (allowMapping) {
            return new XAction(bw, this,  method, mp, path, remoting, bRender);
        } else {
            return new XAction(bw, this, method, null, path, remoting, bRender);
        }
    }

    /**
     * 附加触发器（前后置处理）
     */
    private static <T> void addDo(T[] ary, ConsumerEx<T> fun) {
        if (ary != null) {
            for (T t : ary) {
                try {
                    fun.accept(t);
                } catch (RuntimeException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
