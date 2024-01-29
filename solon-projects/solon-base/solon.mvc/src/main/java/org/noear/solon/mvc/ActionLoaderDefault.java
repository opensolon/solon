package org.noear.solon.mvc;

import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.FactoryManager;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.util.ConsumerEx;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.core.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 动作加载器默认实现（根据bean加载）
 *
 * @author noear
 * @since 1.0
 * */
public class ActionLoaderDefault extends HandlerAide implements ActionLoader {
    protected BeanWrap bw;
    protected Render bRender;
    protected Mapping bMapping;
    protected String bPath;
    protected boolean bRemoting;

    protected boolean allowMapping;

    public ActionLoaderDefault(BeanWrap wrap) {
        bMapping = wrap.clz().getAnnotation(Mapping.class);

        if (bMapping == null) {
            initDo(wrap, null, wrap.remoting(), null, true);
        } else {
            String bPath = Utils.annoAlias(bMapping.value(), bMapping.path());
            initDo(wrap, bPath, wrap.remoting(), null, true);
        }
    }

    public ActionLoaderDefault(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
        initDo(wrap, mapping, remoting, render, allowMapping);
    }

    protected void initDo(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
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
     * 加载 Action 到目标容器
     *
     * @param slots 接收加载结果的容器（槽）
     */
    public void load(HandlerSlots slots) {
        load(bRemoting, slots);
    }

    /**
     * 加载 Action 到目标容器
     *
     * @param all   加载全部函数（一般 remoting 会全部加载）
     * @param slots 接收加载结果的容器（槽）
     */
    protected void load(boolean all, HandlerSlots slots) {
        if (Handler.class.isAssignableFrom(bw.clz())) {
            loadHandlerDo(slots);
        } else {
            loadActions(slots, all || bRemoting);
        }
    }

    /**
     * 加载处理
     *
     * @param slots 接收加载结果的容器（槽）
     */
    protected void loadHandlerDo(HandlerSlots slots) {
        if (bMapping == null) {
            throw new IllegalStateException(bw.clz().getName() + " No @Mapping!");
        }

        Handler handler = bw.raw();
        Set<MethodType> v0 = FactoryManager.mvcFactory().findMethodTypes(new HashSet<>(), t -> bw.annotationGet(t) != null);
        if (v0.size() == 0) {
            v0 = new HashSet<>(Arrays.asList(bMapping.method()));
        }
        slots.add(bMapping, v0, handler);
    }

    /**
     * 查找 method
     */
    protected Method[] findMethods(Class<?> clz) {
        Map<Method, Method> methods = new LinkedHashMap<>();

        //最终会弃用这部分（监时过度）
        for (Method m1 : ReflectUtil.getDeclaredMethods(clz)) {
            methods.put(m1, m1);
        }

        for (Method m1 : ReflectUtil.getMethods(clz)) {
            methods.put(m1, m1);
        }

        return methods.values().toArray(new Method[methods.size()]);
    }


    /**
     * 加载 Action 处理
     */
    protected void loadActions(HandlerSlots slots, boolean all) {
        if (bPath == null) {
            bPath = "";
        }

        Set<MethodType> b_limitMethodSet = new HashSet<>();
        Set<MethodType> b_addinMethodSet = new HashSet<>();

        FactoryManager.mvcFactory().findMethodTypes(b_limitMethodSet, t -> bw.clz().getAnnotation(t) != null);
        loadControllerAide(b_addinMethodSet);
        if (b_limitMethodSet.size() == 0 && bMapping != null) {
            //如果没有独立注解，尝试获取 Mapping 上的方式
            for (MethodType b_mt : bMapping.method()) {
                if (b_mt != MethodType.ALL) {
                    b_limitMethodSet.add(b_mt);
                }
            }
        }

        //只支持 public 函数为 Action
        for (Method method : findMethods(bw.clz())) {
            loadActionItem(slots, all, method, b_limitMethodSet, b_addinMethodSet);
        }
    }

    /**
     * 加载 Action item 处理
     */
    protected void loadActionItem(HandlerSlots slots, boolean all, Method method, Set<MethodType> b_limitMethodSet, Set<MethodType> b_addinMethodSet) {
        Mapping m_map = method.getAnnotation(Mapping.class);

        //检测注解和限制
        if (m_map == null) {
            //如果没有注解，则只允许 public
            if (Modifier.isPublic(method.getModifiers()) == false) {
                return;
            }
        } else {
            //如果有注解，不是 public 时，则告警提醒（以后改为异常）//v2.5
            if (Modifier.isPublic(method.getModifiers()) == false) {
                LogUtil.global().warn("This mapping method is not public: " + method.getDeclaringClass().getName() + ":" + method.getName());
            }
        }

        String m_path;
        Set<MethodType> m_limitMethodSet = new HashSet<>(b_limitMethodSet);
        Set<MethodType> m_addinMethodSet = new HashSet<>(b_addinMethodSet);

        //获取 action 的 methodTypes
        FactoryManager.mvcFactory().findMethodTypes(m_limitMethodSet, t -> method.getAnnotation(t) != null);

        //构建 path and method
        if (m_map != null) {
            m_path = Utils.annoAlias(m_map.value(), m_map.path());

            if (m_limitMethodSet.size() == 0) {
                //如果没有找到，则用Mapping上自带的
                m_limitMethodSet.addAll(Arrays.asList(m_map.method()));
            }
        } else {
            m_path = method.getName();

            if (m_limitMethodSet.size() == 0) {
                //如果没有找到，则用Mapping上自带的；或默认
                if (bMapping == null) {
                    m_limitMethodSet.add(MethodType.HTTP);
                } else {
                    m_limitMethodSet.addAll(Arrays.asList(bMapping.method()));
                }
            }
        }

        //如果是service，method 就不需要map
        if (m_map != null || all) {
            String newPath = PathUtil.mergePath(bPath, m_path);

            Action action = createAction(bw, method, m_map, newPath, bRemoting);

            //m_method 必须之前已准备好，不再动  //用于支持 Cors
            loadActionAide(method, action, m_addinMethodSet);

            if (m_limitMethodSet.size() > 0 &&
                    m_limitMethodSet.contains(MethodType.HTTP) == false &&
                    m_limitMethodSet.contains(MethodType.ALL) == false) {
                //用于支持 Cors
                m_limitMethodSet.addAll(m_addinMethodSet);
            }

            for (MethodType m1 : m_limitMethodSet) {
                if (m_map == null) {
                    slots.add(newPath, m1, action);
                } else {
                    slots.add(newPath, m1, action);
                }
            }
        }
    }


    /**
     * 加载控制器助理（Before、After）
     */
    protected void loadControllerAide(Set<MethodType> addinMethodSet) {
        for (Annotation anno : bw.clz().getAnnotations()) {
            if (anno instanceof Before) {
                addDo(((Before) anno).value(), (b) -> this.before(bw.context().getBeanOrNew(b)));
            } else if (anno instanceof After) {
                addDo(((After) anno).value(), (f) -> this.after(bw.context().getBeanOrNew(f)));
            } else {
                for (Annotation anno2 : anno.annotationType().getAnnotations()) {
                    if (anno2 instanceof Before) {
                        addDo(((Before) anno2).value(), (b) -> this.before(bw.context().getBeanOrNew(b)));
                    } else if (anno2 instanceof After) {
                        addDo(((After) anno2).value(), (f) -> this.after(bw.context().getBeanOrNew(f)));
                    } else if (anno2 instanceof Addition) {
                        //用于支持 Cors
                        Addition additionAnno = (Addition) anno2;
                        for (Class<?> annoClz : additionAnno.value()) {
                            MethodType methodType = MethodTypeUtil.valueOf(annoClz.getSimpleName().toUpperCase());
                            if (methodType != MethodType.UNKNOWN) {
                                addinMethodSet.add(methodType);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 加载动作助理（Before、After）
     */
    protected void loadActionAide(Method method, Action action, Set<MethodType> addinMethodSet) {
        for (Annotation anno : method.getAnnotations()) {
            if (anno instanceof Before) {
                addDo(((Before) anno).value(), (b) -> action.before(bw.context().getBeanOrNew(b)));
            } else if (anno instanceof After) {
                addDo(((After) anno).value(), (f) -> action.after(bw.context().getBeanOrNew(f)));
            } else {
                for (Annotation anno2 : anno.annotationType().getAnnotations()) {
                    if (anno2 instanceof Before) {
                        addDo(((Before) anno2).value(), (b) -> action.before(bw.context().getBeanOrNew(b)));
                    } else if (anno2 instanceof After) {
                        addDo(((After) anno2).value(), (f) -> action.after(bw.context().getBeanOrNew(f)));
                    } else if (anno2 instanceof Addition) {
                        //用于支持 Cors
                        Addition additionAnno = (Addition) anno2;
                        for (Class<?> annoClz : additionAnno.value()) {
                            MethodType methodType = MethodTypeUtil.valueOf(annoClz.getSimpleName().toUpperCase());
                            if (methodType != MethodType.UNKNOWN) {
                                addinMethodSet.add(methodType);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 构建 Action
     */
    protected Action createAction(BeanWrap bw, Method method, Mapping mp, String path, boolean remoting) {
        if (allowMapping) {
            return new ActionDefault(bw, this, method, mp, path, remoting, bRender);
        } else {
            return new ActionDefault(bw, this, method, null, path, remoting, bRender);
        }
    }

    /**
     * 附加处理
     */
    protected <T> void addDo(T[] ary, ConsumerEx<T> fun) {
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