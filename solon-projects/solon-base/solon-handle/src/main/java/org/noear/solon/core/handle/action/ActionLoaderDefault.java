/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.handle.action;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Addition;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.FactoryManager;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.ConsumerEx;
import org.noear.solon.core.util.ProxyBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 动作加载器默认实现（根据bean加载）
 *
 * @author noear
 * @since 1.0
 * @since 3.0
 * */
public class ActionLoaderDefault extends HandlerAide implements ActionLoader {
    static final Logger log = LoggerFactory.getLogger(ActionLoaderDefault.class);

    protected final BeanWrap bw;
    protected final Render bRender;
    protected final Mapping bMapping;
    protected final String bPath;
    protected final String bVersion;
    protected final boolean bRemoting;

    protected final boolean allowMapping;

    public ActionLoaderDefault(BeanWrap wrap) {
        this(wrap, null, wrap.remoting(), null, true);
    }

    public ActionLoaderDefault(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
        if (render == null) {
            if (wrap.raw() instanceof Render) {
                render = wrap.raw();
            }
        }

        if (mapping == null) {
            bMapping = wrap.clz().getAnnotation(Mapping.class);

            if (bMapping != null) {
                mapping = Utils.annoAlias(bMapping.value(), bMapping.path());
            }
        } else {
            bMapping = null;
        }

        this.bw = wrap;
        this.bRender = render;
        this.allowMapping = allowMapping;

        this.bPath = Utils.annoAlias(mapping, "");
        this.bRemoting = remoting;

        if (bMapping == null) {
            bVersion = null;
        } else {
            if (Utils.isNotEmpty(bMapping.version())) {
                bVersion = bMapping.version();
            } else {
                bVersion = null;
            }
        }
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
        Set<MethodType> mtSet = FactoryManager.getGlobal().actionLoaderFactory().findMethodTypes(new HashSet<>(), t -> bw.annotationGet(t) != null);
        if (mtSet.size() == 0) {
            mtSet = new HashSet<>(Arrays.asList(bMapping.method()));
        }

        slots.add(bMapping, mtSet, bw.index(), handler);
    }


    /**
     * 加载 Action 处理
     */
    protected void loadActions(HandlerSlots slots, boolean all) {
        Set<MethodType> b_limitMethodSet = new HashSet<>();
        Set<MethodType> b_addinMethodSet = new HashSet<>();

        FactoryManager.getGlobal().actionLoaderFactory().findMethodTypes(b_limitMethodSet, t -> bw.clz().getAnnotation(t) != null);
        loadControllerAide(b_addinMethodSet);
        if (b_limitMethodSet.size() == 0 && bMapping != null) {
            //如果没有独立注解，尝试获取 Mapping 上的方式
            for (MethodType b_mt : bMapping.method()) {
                if (b_mt != MethodType.ALL) {
                    b_limitMethodSet.add(b_mt);
                }
            }
        }

        boolean enableProxy = false;

        //只支持 public 函数为 Action
        for (Method method : ClassUtil.findPublicMethods(bw.clz())) {
            loadActionItem(slots, all, method, b_limitMethodSet, b_addinMethodSet);

            //是否需要自动代理 //只支持 public
            if (Modifier.isPublic(method.getModifiers())) {
                enableProxy = enableProxy || bw.context().beanInterceptorHas(method);
            }
        }

        //是否需要自动代理
        enableProxy = enableProxy || bw.context().beanInterceptorHas(bw.rawClz());

        if (enableProxy) {
            //需要代理（作为一般托管 bean 使用时，会跳过 Action 执行；此时需要代理）
            ProxyBinder.global().binding(bw);
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
                log.warn("This @Mapping method is not public: " + method.getDeclaringClass().getName() + ":" + method.getName());
            }
        }

        String m_path;
        Set<MethodType> m_limitMethodSet = new HashSet<>(b_limitMethodSet);
        Set<MethodType> m_addinMethodSet = new HashSet<>(b_addinMethodSet);

        //获取 action 的 methodTypes
        FactoryManager.getGlobal().actionLoaderFactory().findMethodTypes(m_limitMethodSet, t -> method.getAnnotation(t) != null);

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
            String newPath = postActionPath(bw, bPath, method, m_path);

            ActionDefault action = createAction(bw, method, m_map, newPath, bRemoting);

            //m_method 必须之前已准备好，不再动  //用于支持 Cors
            loadActionAide(method, action, m_addinMethodSet);

            if (m_limitMethodSet.size() > 0 &&
                    m_limitMethodSet.contains(MethodType.HTTP) == false &&
                    m_limitMethodSet.contains(MethodType.ALL) == false) {
                //用于支持 Cors
                m_limitMethodSet.addAll(m_addinMethodSet);
            }

            for (MethodType m1 : m_limitMethodSet) {
                slots.add(newPath, m1, action);
            }
        }
    }


    /**
     * 加载控制器附件
     */
    protected void loadControllerAide(Set<MethodType> addinMethodSet) {
        for (Annotation anno : bw.clz().getAnnotations()) {
            if (loadControllerAideAdd(anno, addinMethodSet)) {
                continue;
            }

            for (Annotation anno2 : anno.annotationType().getAnnotations()) {
                loadControllerAideAdd(anno2, addinMethodSet);
            }
        }
    }

    /**
     * @since 3.0
     * */
    protected boolean loadControllerAideAdd(Annotation anno, Set<MethodType> addinMethodSet) {
        if (anno instanceof Addition) {
            Addition additionAnno = (Addition) anno;
            for (Class<?> clz : additionAnno.value()) {
                if (Filter.class.isAssignableFrom(clz)) {
                    //用于替代 @After,@Before
                    filter(additionAnno.index(), (Filter) bw.context().getBeanOrNew(clz));
                } else if (Annotation.class.isAssignableFrom(clz)) {
                    //用于支持 Cors
                    MethodType methodType = MethodTypeUtil.valueOf(clz.getSimpleName().toUpperCase());
                    if (methodType != MethodType.UNKNOWN) {
                        addinMethodSet.add(methodType);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * 加载动作附件
     */
    protected void loadActionAide(Method method, ActionDefault action, Set<MethodType> addinMethodSet) {
        for (Annotation anno : method.getAnnotations()) {
            if (loadActionAideAdd(anno, action, addinMethodSet)) {
                continue;
            }

            for (Annotation anno2 : anno.annotationType().getAnnotations()) {
                loadActionAideAdd(anno2, action, addinMethodSet);
            }
        }
    }

    /**
     * @since 3.0
     * */
    protected boolean loadActionAideAdd(Annotation anno, ActionDefault action, Set<MethodType> addinMethodSet) {
        if (anno instanceof Addition) {
            Addition additionAnno = (Addition) anno;
            for (Class<?> clz : additionAnno.value()) {
                if (Filter.class.isAssignableFrom(clz)) {
                    //用于替代 @After,@Before
                    action.filter(additionAnno.index(), (Filter) bw.context().getBeanOrNew(clz));
                } else if (Annotation.class.isAssignableFrom(clz)) {
                    //用于支持 Cors
                    MethodType methodType = MethodTypeUtil.valueOf(clz.getSimpleName().toUpperCase());
                    if (methodType != MethodType.UNKNOWN) {
                        addinMethodSet.add(methodType);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * 确认 Action 路径
     */
    protected String postActionPath(BeanWrap bw, String bPath, Method method, String mPath) {
        return FactoryManager.getGlobal().actionLoaderFactory().postActionPath(bw, bPath, method, mPath);
    }

    /**
     * 构建 Action
     */
    protected ActionDefault createAction(BeanWrap bw, Method method, Mapping mp, String path, boolean remoting) {
        if (allowMapping) {
            return new ActionDefault(bw, this, bVersion, method, mp, path, remoting, bRender);
        } else {
            return new ActionDefault(bw, this, bVersion, method, null, path, remoting, bRender);
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