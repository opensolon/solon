package org.noear.solon.core.handle;

import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.ext.ConsumerEx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * 通用处理接口加载器（根据bean加载）
 *
 * @author noear
 * @since 1.0
 * */
public class HandlerLoader extends HandlerAide {
    protected BeanWrap bw;
    protected Render bRender;
    protected Mapping bMapping;
    protected String bPath;
    protected boolean bRemoting;

    protected boolean allowMapping;

    public HandlerLoader(BeanWrap wrap) {
        bMapping = wrap.clz().getAnnotation(Mapping.class);

        if (bMapping == null) {
            initDo(wrap, null, wrap.remoting(), null, true);
        } else {
            String bPath = Utils.annoName(bMapping.value(), bMapping.path());
            initDo(wrap, bPath, wrap.remoting(), null, true);
        }
    }

    public HandlerLoader(BeanWrap wrap, String mapping) {
        initDo(wrap, mapping, wrap.remoting(), null, true);
    }

    public HandlerLoader(BeanWrap wrap, String mapping, boolean remoting) {
        initDo(wrap, mapping, remoting, null, true);
    }

    public HandlerLoader(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
        initDo(wrap, mapping, remoting, render, allowMapping);
    }

    private void initDo(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
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
    public void load(HandlerSlots slots) {
        load(bRemoting, slots);
    }

    /**
     * 加载 XAction 到目标容器
     *
     * @param all   加载全部函数（一般 remoting 会全部加载）
     * @param slots 接收加载结果的容器（槽）
     */
    public void load(boolean all, HandlerSlots slots) {
        if (Handler.class.isAssignableFrom(bw.clz())) {
            loadHandlerDo(slots);
        } else {
            loadActionDo(slots, all || bRemoting);
        }
    }

    /**
     * 加载处理
     */
    protected void loadHandlerDo(HandlerSlots slots) {
        if (bMapping == null) {
            throw new RuntimeException(bw.clz().getName() + " No @Mapping!");
        }

        Handler handler = bw.raw();
        List<MethodType> v0 = MethodTypeUtil.findAndFill(new ArrayList<>(), t -> bw.annotationGet(t) != null);
        if (v0.size() == 0) {
            v0 = Arrays.asList(bMapping.method());
        }

        slots.add(bMapping, v0, handler);
    }


    /**
     * 加载 XAction 处理
     */
    protected void loadActionDo(HandlerSlots slots, boolean all) {
        String m_path;

        if (bPath == null) {
            bPath = "";
        }

        loadControllerAide();

        List<MethodType> m_method;
        Mapping m_map;
        int m_index = 0;

        //只支持public函数为XAction
        for (Method method : bw.clz().getDeclaredMethods()) {
            m_map = method.getAnnotation(Mapping.class);
            m_index = 0;
            m_method = new ArrayList<>();

            //获取 action 的methodTypes
            MethodTypeUtil.findAndFill(m_method, t -> method.getAnnotation(t) != null);

            //构建path and method
            if (m_map != null) {
                m_path = Utils.annoName(m_map.value(), m_map.path());

                if (m_method.size() == 0) {
                    //如果没有找到，则用Mapping上自带的
                    m_method = Arrays.asList(m_map.method());
                }
                m_index = m_map.index();
            } else {
                m_path = method.getName();

                if (m_method.size() == 0) {
                    //获取 controller 的methodTypes
                    MethodTypeUtil.findAndFill(m_method, t -> bw.clz().getAnnotation(t) != null);
                }

                if (m_method.size() == 0) {
                    //如果没有找到，则用Mapping上自带的；或默认
                    if (bMapping == null) {
                        m_method.add(MethodType.HTTP);
                    } else {
                        m_method = Arrays.asList(bMapping.method());
                    }
                }
            }

            //如果是service，method 就不需要map
            if (m_map != null || all) {
                String newPath = PathUtil.mergePath(bPath, m_path);

                Action action = createAction(bw, method, m_map, newPath, bRemoting);

                loadActionAide(method, action);

                for (MethodType m1 : m_method) {
                    if (m_map == null) {
                        slots.add(newPath, m1, action);
                    } else {
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
            if (anno instanceof Before) {
                addDo(((Before) anno).value(), (b) -> this.before(Aop.get(b)));
            } else if (anno instanceof After) {
                addDo(((After) anno).value(), (f) -> this.after(Aop.get(f)));
            } else {
                for (Annotation anno2 : anno.annotationType().getAnnotations()) {
                    if (anno2 instanceof Before) {
                        addDo(((Before) anno2).value(), (b) -> this.before(Aop.get(b)));
                    } else if (anno2 instanceof After) {
                        addDo(((After) anno2).value(), (f) -> this.after(Aop.get(f)));
                    }
                }
            }
        }
    }

    protected void loadActionAide(Method method, Action action) {
        for (Annotation anno : method.getAnnotations()) {
            if (anno instanceof Before) {
                addDo(((Before) anno).value(), (b) -> action.before(Aop.get(b)));
            } else if (anno instanceof After) {
                addDo(((After) anno).value(), (f) -> action.after(Aop.get(f)));
            } else {
                for (Annotation anno2 : anno.annotationType().getAnnotations()) {
                    if (anno2 instanceof Before) {
                        addDo(((Before) anno2).value(), (b) -> action.before(Aop.get(b)));
                    } else if (anno2 instanceof After) {
                        addDo(((After) anno2).value(), (f) -> action.after(Aop.get(f)));
                    }
                }
            }
        }
    }

    /**
     * 构建 XAction
     */
    protected Action createAction(BeanWrap bw, Method method, Mapping mp, String path, boolean remoting) {
        if (allowMapping) {
            return new Action(bw, this, method, mp, path, remoting, bRender);
        } else {
            return new Action(bw, this, method, null, path, remoting, bRender);
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
