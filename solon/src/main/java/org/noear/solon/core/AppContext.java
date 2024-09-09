/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.core;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.core.convert.ConverterFactory;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.*;
import org.noear.solon.core.wrap.*;
import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * App 上下文（ 为全局对象；热插拨的插件，会产生独立的上下文）
 *
 * 主要实现四个动作：
 * 1.bean 构建
 * 2.bean 注入（字段 或 参数）
 * 3.bean 提取
 * 4.bean 拦截
 *
 * @author noear
 * @since 1.0
 * @since 2.6
 * */
public class AppContext extends BeanContainer {

    public AppContext(ClassLoader classLoader, Props props) {
        super(classLoader, props);
        initialize();
        lifecycle(Constants.LF_IDX_PARAM_COLLECTION_INJECT, () -> {
            this.startInjectReview(true);
        });
    }

    /**
     * 订阅事件
     */
    public <T> AppContext onEvent(Class<T> type, EventListener<T> handler) {
        EventBus.subscribe(type, handler);
        return this;
    }

    /**
     * 订阅事件
     */
    public <T> AppContext onEvent(Class<T> type, int index, EventListener<T> handler) {
        EventBus.subscribe(type, index, handler);
        return this;
    }


    private final Set<RankEntity<Lifecycle>> lifecycleBeans = new HashSet<>();

    private final Map<MethodKey, MethodWrap> methodCached = new HashMap<>();
    private final Set<Class<?>> beanBuildedCached = new HashSet<>();

    private final Set<InjectGather> gatherSet = new HashSet<>();

    /**
     * 获取方法包装（方便 aot 收集）
     *
     * @param method 方法
     */
    public MethodWrap methodGet(Method method) {
        return methodGet(method.getDeclaringClass(), method);
    }


    /**
     * 获取方法包装（方便 aot 收集）
     *
     * @param method 方法
     */
    public MethodWrap methodGet(Class<?> clz, Method method) {
        MethodKey methodKey = new MethodKey(method, clz);

        MethodWrap mw = methodCached.get(methodKey);
        if (mw == null) {
            SYNC_LOCK.lock();

            try {
                mw = methodCached.get(methodKey);
                if (mw == null) {
                    mw = new MethodWrap(this, clz, method);
                    methodCached.put(methodKey, mw);
                }
            } finally {
                SYNC_LOCK.unlock();
            }
        }

        return mw;
    }

    /**
     * 遍历method (拿到的是method包装)
     *
     * @param action 遍历动作
     */
    public void methodForeach(Consumer<MethodWrap> action) {
        methodCached.values().forEach(action);
    }

    @Override
    public void clear() {
        super.clear();

        methodCached.clear();
        beanBuildedCached.clear();

        gatherSet.clear();

        lifecycleBeans.clear();

        started = false;
    }

    /**
     * 生成 bean 包装
     */
    @Override
    protected BeanWrap wrapCreate(Class<?> type, Object bean, String name, boolean typed) {
        return new BeanWrap(this, type, bean, name, typed);
    }

    /**
     * ::初始化（独立出 initialize，方便重写）
     */
    protected void initialize() {

        //注册 @Configuration 构建器
        beanBuilderAdd(Configuration.class, (clz, bw, anno) -> {
            //尝试导入（可能会导入属性源，或小饼依赖的组件）
            for (Annotation a1 : clz.getAnnotations()) {
                if (a1 instanceof Import) {
                    cfg().loadAdd((Import) a1);//v2.5
                    beanImport((Import) a1);
                } else {
                    a1 = a1.annotationType().getAnnotation(Import.class);
                    if (a1 != null) {
                        cfg().loadAdd((Import) a1);//v2.5
                        beanImport((Import) a1);
                    }
                }
            }


            //尝试注入属性
            beanInjectProperties(clz, bw.raw());

            //构建小饼
            for (Method m : ClassUtil.findPublicMethods(bw.clz())) {
                Bean ma = m.getAnnotation(Bean.class);

                if (ma != null) {
                    tryBuildBeanOfMethod(bw, m, ma);
                }
            }

            //添加bean形态处理
            beanShapeRegister(clz, bw, clz);

            //注册到容器 //Configuration 不进入二次注册
            //beanRegister(bw,bw.name(),bw.typed());

            //支持基类注册
            beanRegisterSup0(clz, bw);
        });

        //注册 @Component 构建器
        beanBuilderAdd(Component.class, (clz, bw, anno) -> {
            String beanName = Utils.annoAlias(anno.value(), anno.name());

            bw.nameSet(beanName);
            bw.tagSet(anno.tag());
            bw.typedSet(anno.typed());

            //确定顺序位
            bw.indexSet(anno.index());

            beanComponentized(bw, anno.registered());
        });

        //注册 @Remoting 构建器
        beanBuilderAdd(Remoting.class, (clz, bw, anno) -> {
            //设置remoting状态
            bw.remotingSet(true);
            //注册到容器
            beanRegister(bw, "", false);
        });

        //注册 @Controller 构建器
        beanBuilderAdd(Controller.class, (clz, bw, anno) -> {
            Solon.app().router().add(bw);
        });

        //注册 @Inject 注入器
        beanInjectorAdd(Inject.class, ((varH, anno) -> {
            beanInject(varH, anno.value(), anno.required(), anno.autoRefreshed());
        }));
    }

    /**
     * 组件化处理
     */
    protected void beanComponentized(BeanWrap bw, boolean registered) {
        //尝试提取函数并确定自动代理
        beanExtractOrProxy(bw);

        //添加bean形态处理
        if (registered) {
            beanShapeRegister(bw.clz(), bw, bw.clz());
        }

        //注册到容器
        beanRegister(bw, bw.name(), bw.typed());


        //单例，进行事件通知
        if (bw.singleton()) {
            EventBus.publish(bw.raw()); //@deprecated
            wrapPublish(bw);
        }
    }

    @Override
    protected void beanInject(VarHolder varH, String name, boolean required, boolean autoRefreshed) {
        super.beanInject(varH, name, required, autoRefreshed);

        if (varH.isDone()) {
            return;
        }

        if (Utils.isEmpty(name) && varH.getGenericType() != null) {
            if (List.class == varH.getType()) {
                //支持 List<Bean> 注入
                Type type0 = varH.getGenericType().getActualTypeArguments()[0];
                if (type0 instanceof ParameterizedType) {
                    type0 = ((ParameterizedType) type0).getRawType();
                }

                Type type = type0;

                if (type instanceof Class) {
                    if (varH.isField()) {
                        varH.required(required);
                        lifecycle(Constants.LF_IDX_FIELD_COLLECTION_INJECT, () -> {
                            if (varH.isDone()) {
                                return;
                            }

                            BeanWrap.Supplier beanListSupplier = () -> this.getBeansOfType((Class<? extends Object>) type);
                            varH.setValue(beanListSupplier);
                        });
                    } else {
                        varH.required(false);
                        varH.setDependencyType((Class<?>) type);
                        BeanWrap.Supplier beanListSupplier = () -> this.getBeansOfType((Class<? extends Object>) type);
                        varH.setValueOnly(beanListSupplier);
                    }
                    return;
                }
            }

            if (Map.class == varH.getType()) {
                //支持 Map<String,Bean> 注入
                Type keyType = varH.getGenericType().getActualTypeArguments()[0];
                Type valType0 = varH.getGenericType().getActualTypeArguments()[1];

                if (valType0 instanceof ParameterizedType) {
                    valType0 = ((ParameterizedType) valType0).getRawType();
                }

                Type valType = valType0;

                if (String.class == keyType && valType instanceof Class) {
                    if (varH.isField()) {
                        varH.required(required);
                        lifecycle(Constants.LF_IDX_FIELD_COLLECTION_INJECT, () -> {
                            if (varH.isDone()) {
                                return;
                            }

                            BeanWrap.Supplier beanMapSupplier = () -> this.getBeansMapOfType((Class<?>) valType);
                            varH.setValue(beanMapSupplier);
                        });
                    } else {
                        varH.required(false);
                        varH.setDependencyType((Class<?>) valType);
                        BeanWrap.Supplier beanMapSupplier = () -> this.getBeansMapOfType((Class<?>) valType);
                        varH.setValueOnly(beanMapSupplier);
                    }
                    return;
                }
            }
        }
    }

    /**
     * 尝试 bean 形态注册
     *
     * @param clz    类
     * @param bw     Bean 包装
     * @param annoEl 有注解元素（类 或 方法）
     */
    public void beanShapeRegister(Class<?> clz, BeanWrap bw, AnnotatedElement annoEl) {
        if (bw.raw() == null) {
            return;
        }

        //Plugin
        if (bw.raw() instanceof Plugin) {
            //如果是插件，则插入
            throw new IllegalStateException("'Plugin' cannot be component, please use 'LifecycleBean': " + clz.getName());
        }

        String singletonHint = null;

        //LifecycleBean（替代 Plugin，提供组件的生态周期控制）
        if (bw.raw() instanceof LifecycleBean) {
            //让注解产生的生命周期，排序晚1个点
            int index = bw.index();
            if (index == 0) {
                index = IndexUtil.buildLifecycleIndex(bw.rawClz());
            }

            lifecycle(index + 1, bw.raw());
            singletonHint = "LifecycleBean";
        }

        //EventListener
        if (bw.raw() instanceof EventListener) {
            addEventListener(clz, bw);
            singletonHint = "EventListener";
        }

        //LoadBalance.Factory
        if (bw.raw() instanceof LoadBalance.Factory) {
            Solon.app().factoryManager().loadBalanceFactory(bw.raw());
            singletonHint = "LoadBalance.Factory";
        }

        //Handler
        if (bw.raw() instanceof Handler) {
            Mapping mapping = annoEl.getAnnotation(Mapping.class);
            if (mapping != null) {
                Handler handler = new HandlerProxy(bw); //支持原型
                Set<MethodType> v0 = FactoryManager.getGlobal().mvcFactory().findMethodTypes(new HashSet<>(), t -> annoEl.getAnnotation(t) != null);
                if (v0.size() == 0) {
                    v0 = new HashSet<>(Arrays.asList(mapping.method()));
                }
                Solon.app().add(mapping, v0, handler);
            }
        }

        //Render
        if (bw.raw() instanceof Render) {
            RenderManager.mapping(bw.name(), (Render) bw.raw());
            singletonHint = "Render";
        }

        //Filter
        if (bw.raw() instanceof Filter) {
            Solon.app().filter(bw.index(), bw.raw());
            singletonHint = "Filter";
        }

        //RouterInterceptor
        if (bw.raw() instanceof RouterInterceptor) {
            Solon.app().routerInterceptor(bw.index(), bw.raw());
            singletonHint = "RouterInterceptor";
        }

        //ActionReturnHandler
        if (bw.raw() instanceof ActionReturnHandler) {
            Solon.app().chainManager().addReturnHandler(bw.raw());
            singletonHint = "ActionReturnHandler";
        }

        //ActionExecuteHandler
        if (bw.raw() instanceof ActionExecuteHandler) {
            Solon.app().chainManager().addExecuteHandler(bw.raw());
            singletonHint = "ActionExecuteHandler";
        }

        //Converter
        if (bw.raw() instanceof Converter) {
            Converter c = bw.raw();
            Solon.app().converterManager().register(c);
            singletonHint = "Converter";
        }

        //ConverterFactory
        if (bw.raw() instanceof ConverterFactory) {
            ConverterFactory cf = bw.raw();
            Solon.app().converterManager().register(cf);
            singletonHint = "ConverterFactory";
        }


        if (bw.singleton() == false && singletonHint != null) {
            LogUtil.global().warn(singletonHint + " does not support @Singleton(false), class=" + clz.getName());
        }
    }

    /**
     * 添加事件监听支持
     */
    private void addEventListener(Class<?> clz, BeanWrap bw) {
        Class<?>[] ets = GenericUtil.resolveTypeArguments(clz, EventListener.class);
        if (ets != null && ets.length > 0) {
            EventBus.subscribe(ets[0], bw.index(), bw.raw());
        }
    }

    //::提取

    /**
     * 为一个对象提取函数或自动代理
     */
    public void beanExtractOrProxy(BeanWrap bw) {
        if (bw == null) {
            return;
        }

        boolean enableProxy = false;

        if (beanExtractors.size() > 0 || beanInterceptors.size() > 0) {
            ClassWrap clzWrap = ClassWrap.get(bw.clz());

            for (Method m : clzWrap.getMethods()) { //只支持公有函数检查
                for (Annotation a : m.getAnnotations()) {
                    BeanExtractor be = beanExtractors.get(a.annotationType());

                    //是否需要提取
                    if (be != null) {
                        try {
                            be.doExtract(bw, m, a);
                        } catch (Throwable e) {
                            e = Utils.throwableUnwrap(e);
                            if (e instanceof RuntimeException) {
                                throw (RuntimeException) e;
                            } else {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    //是否需要自动代理
                    enableProxy = enableProxy || requiredProxy(a);

                }
            }
        }

        if (enableProxy == false) {
            for (Annotation a : bw.clz().getAnnotations()) {
                //是否需要自动代理
                enableProxy = enableProxy || requiredProxy(a);
            }
        }

        if (enableProxy) {
            ProxyBinder.global().binding(bw);
        }
    }

    /**
     * 是否需要有代理
     */
    private boolean requiredProxy(Annotation a) {
        return beanInterceptors.containsKey(a.annotationType())
                || a.annotationType().isAnnotationPresent(Around.class)
                || a.annotationType().equals(Around.class);
    }


    //::注入

    /**
     * 为一个对象注入（可以重写）
     */
    public void beanInject(Object obj) {
        if (obj == null) {
            return;
        }

        ClassWrap clzWrap = ClassWrap.get(obj.getClass());
        List<FieldWrap> fwList = new ArrayList<>();

        //支持父类注入(找到有注解的字段)
        for (Map.Entry<String, FieldWrap> kv : clzWrap.getFieldWraps().entrySet()) {
            Annotation[] annS = kv.getValue().annoS;
            if (annS.length > 0) {
                fwList.add(kv.getValue());
            }
        }

        if (fwList.size() == 0) {
            //略过
        } else {
            //需要注入（可能）
            InjectGather gather = new InjectGather(0, clzWrap.clz(), true, fwList.size(), null);

            //添加到集合
            gatherSet.add(gather);

            //添加要收集的字段
            for (FieldWrap fw : fwList) {
                VarHolder varH = fw.holder(this, obj, gather);
                gather.add(varH);
                tryInject(varH, fw.annoS);
            }
        }
    }

    ////////////

    /**
     * 根据注解配置导入bean
     */
    public void beanImport(Import anno) {
        if (anno != null) {
            //导入类
            for (Class<?> c1 : anno.value()) {
                beanMake(c1);
            }

            for (Class<?> c1 : anno.classes()) {
                beanMake(c1);
            }

            //扫描包
            for (String p1 : anno.scanPackages()) {
                beanScan(p1);
            }

            //扫描包
            for (Class<?> s1 : anno.scanPackageClasses()) {
                beanScan(s1);
            }
        }
    }

    /**
     * ::扫描源下的所有 bean 及对应处理
     */
    public void beanScan(Class<?> source) {
        //确定文件夹名
        if (source.getPackage() != null) {
            beanScan(source.getClassLoader(), source.getPackage().getName());
        }
    }

    /**
     * ::扫描源下的所有 bean 及对应处理
     */
    public void beanScan(String basePackage) {
        beanScan(getClassLoader(), basePackage);
    }

    /**
     * ::扫描源下的所有 bean 及对应处理
     */
    public void beanScan(ClassLoader classLoader, String basePackage) {
        if (Utils.isEmpty(basePackage)) {
            return;
        }

        if (classLoader == null) {
            return;
        }

        String dir = basePackage.replace('.', '/');

        //扫描类文件并处理（采用两段式加载，可以部分bean先处理；剩下的为第二段处理）
        ScanUtil.scan(classLoader, dir, n -> n.endsWith(".class"))
                .stream()
                .sorted(Comparator.comparing(s -> s.length()))
                .forEach(name -> {
                    String className = name.substring(0, name.length() - 6);
                    className = className.replace('/', '.');

                    Class<?> clz = ClassUtil.loadClass(classLoader, className);
                    if (clz != null) {
                        tryBuildBeanOfClass(clz);
                    }
                });
    }

    /**
     * ::制造 bean 及对应处理
     */
    public @Nullable BeanWrap beanMake(Class<?> clz) {
        int state = tryBuildBeanOfClass(clz);

        if (state == 1) {
            //异步处理或条件未达（一般由条件注解引起的）
            return null;
        } else if (state == build_bean_ofclass_state2) {
            //已处理
            return getWrap(clz);
        } else {
            //未处理的（一般是没有注解的普通类）
            return wrapAndPut(clz);
        }
    }


    ////////////////////////////////////////////////////
    //
    //

    /**
     * 尝试为bean注入
     */
    protected void tryInject(VarHolder varH, Annotation[] annS) {
        for (Annotation a : annS) {
            BeanInjector bi = beanInjectors.get(a.annotationType());
            if (bi != null) {
                //只允许一个注入器有效 //如果有多个略过
                bi.doInject(varH, a);
                return;
            }
        }

        //如果没有注入器，则为null。用于触发事件
        varH.setValue(null);
    }

    /**
     * 根据方法尝试生成 bean（用 protected，方便扩展时复用）
     */
    protected void tryBuildBeanOfMethod(BeanWrap bw, Method m, Bean ma) throws Throwable {
        if (NativeDetector.isAotRuntime()) {
            //如果是 aot 则注册函数
            methodGet(bw.rawClz(), m);
        }

        Condition mc = m.getAnnotation(Condition.class);

        if (started == false && ConditionUtil.ifMissingBean(mc)) {
            lifecycle(Constants.LF_IDX_METHOD_CONDITION_IF_MISSING, ma.priority(), () -> tryBuildBeanOfMethod0(bw, m, ma, mc));
        } else {
            tryBuildBeanOfMethod0(bw, m, ma, mc);
        }
    }

    private void tryBuildBeanOfMethod0(BeanWrap bw, Method m, Bean ma, Condition mc) throws Throwable {
        //增加条件检测
        if (ConditionUtil.test(this, mc) == false) {
            return;
        }

        //支持非公有函数
        if (m.isAccessible() == false) {
            m.setAccessible(true);
        }

        MethodWrap mWrap = methodGet(bw.rawClz(), m);

        //开始执行
        if (ConditionUtil.ifBean(mc)) {
            //如果有 onBean 条件
            ConditionUtil.onBeanRun(mc, this, () -> tryBuildBeanOfMethod1(ma, mWrap, bw));
        } else {
            //没有 onBean 条件
            tryBuildBeanOfMethod1(ma, mWrap, bw);
        }
    }

    /**
     * 尝试构建 bean
     *
     * @param anno  bean 注解
     * @param mWrap 方法包装器
     * @param bw    bean 包装器
     */
    private void tryBuildBeanOfMethod1(Bean anno, MethodWrap mWrap, BeanWrap bw) throws Throwable {
        if (mWrap.getParamWraps().length == 0) {
            //0.没有参数
            tryBuildBeanOfMethod2(anno, mWrap, bw, new Object[]{});
        } else {
            tryMethodParamsGather(bw.context(), 1, mWrap.getReturnType(), mWrap.getRawParameters(), (args2) -> {
                RunUtil.runOrThrow(() -> tryBuildBeanOfMethod2(anno, mWrap, bw, args2));
            });
        }
    }

    /**
     * 尝试方法参数收集
     */
    protected void tryMethodParamsGather(AppContext context, int label, Class<?> outType, Parameter[] paramAry, ConsumerEx<Object[]> completionConsumer) {
        //1.构建参数 (requireRun=false => true) //运行条件已经确认过，且必须已异常
        InjectGather gather = new InjectGather(label, outType, true, paramAry.length, (args2) -> {
            //变量收集完成后，会回调此处
            completionConsumer.accept(args2);
        });

        //1.1.登录到集合
        gatherSet.add(gather);

        //1.2.添加要收集的参数；并为参数注入（注入是异步的；全部完成后，VarGather 会回调）
        for (Parameter p1 : paramAry) {
            VarHolder varH = new VarHolderOfParam(context, p1, gather);
            gather.add(varH);

            Annotation[] annoS = p1.getAnnotations();
            if (annoS.length == 0) {
                //没带注解的，算必须
                beanInject(varH, null, true, false);
            } else {
                tryInject(varH, annoS);
            }
        }
    }

    protected void tryBuildBeanOfMethod2(Bean anno, MethodWrap mWrap, BeanWrap bw, Object[] args) throws Throwable {
        Object raw = mWrap.invoke(bw.raw(), args);

        if (raw != null) {
            Class<?> beanClz = mWrap.getReturnType();
            Type beanGtp = mWrap.getGenericReturnType();

            //产生的bean，不再支持二次注入

            BeanWrap m_bw = null;
            if (raw instanceof BeanWrap) {
                m_bw = (BeanWrap) raw;
            } else {
                if (anno.injected()) {
                    //执行注入
                    beanInject(raw);
                }

                //@Bean 动态构建的bean, 可通过事件广播进行扩展 //后面不用再发布了
                EventBus.publish(raw);//@deprecated

                //动态构建的bean，都用新生成wrap（否则会类型混乱）
                m_bw = new BeanWrap(this, beanClz, raw, null, false, anno.initMethod(), anno.destroyMethod());
            }

            String beanName = Utils.annoAlias(anno.value(), anno.name());

            m_bw.nameSet(beanName);
            m_bw.tagSet(anno.tag());
            m_bw.typedSet(anno.typed());

            //确定顺序位
            m_bw.indexSet(anno.index());

            //添加bean形态处理
            if (anno.registered()) {
                beanShapeRegister(m_bw.clz(), m_bw, mWrap.getMethod());
            }

            //注册到容器
            beanRegister(m_bw, beanName, anno.typed());

            //尝试泛型注册(通过 name 实现)
            if (beanGtp instanceof ParameterizedType) {
                putWrap(beanGtp.getTypeName(), m_bw);
            }

            //@Bean 动态产生的 beanWrap（含 name,tag,attrs），进行事件通知
            wrapPublish(m_bw);
        }
    }

    private static final int build_bean_ofclass_state0 = 0; //没有处理
    private static final int build_bean_ofclass_state1 = 1; //异步处理，或条件未达（可以返回 null）
    private static final int build_bean_ofclass_state2 = 2; //有处理了（可以返回 warp）

    /**
     * 根据类尝试生成 bean（用 protected，方便扩展时复用）
     */
    protected int tryBuildBeanOfClass(Class<?> clz) {
        //return handled?
        Condition cc = clz.getAnnotation(Condition.class);

        if (started == false && ConditionUtil.ifMissingBean(cc)) {
            lifecycle(Constants.LF_IDX_CLASS_CONDITION_IF_MISSING, () -> tryBuildBeanOfClass0(clz, cc));
            return build_bean_ofclass_state1;
        } else {
            return tryBuildBeanOfClass0(clz, cc);
        }
    }

    private int tryBuildBeanOfClass0(Class<?> clz, Condition cc) {
        //return handled?
        if (ConditionUtil.test(this, cc) == false) {
            return build_bean_ofclass_state1;
        }

        if (ConditionUtil.ifBean(cc)) {
            ConditionUtil.onBeanRun(cc, this, () -> tryBuildBeanOfClass1(clz));
            return build_bean_ofclass_state1;
        } else {
            return tryBuildBeanOfClass1(clz);
        }
    }

    private int tryBuildBeanOfClass1(Class<?> clz) {
        //return state?
        Annotation[] annS = clz.getAnnotations();
        int state = 0;

        if (annS.length > 0) {
            //去重处理
            if (beanBuildedCached.contains(clz)) {
                return build_bean_ofclass_state2;
            } else {
                beanBuildedCached.add(clz);
            }

            for (Annotation a : annS) {
                BeanBuilder builder = beanBuilders.get(a.annotationType());

                if (builder != null) {
                    try {
                        state = build_bean_ofclass_state2;
                        tryBuildBeanOfClass2(clz, builder, a);
                    } catch (Throwable e) {
                        e = Utils.throwableUnwrap(e);
                        if (e instanceof RuntimeException) {
                            throw (RuntimeException) e;
                        } else {
                            throw new IllegalStateException(e);
                        }
                    }
                }
            }
        }

        return state;
    }

    private void tryBuildBeanOfClass2(Class<?> clz, BeanBuilder builder, Annotation anno) throws Throwable {
        final Constructor c1;
        if (clz.isInterface() == false) {
            c1 = clz.getDeclaredConstructors()[0]; //组件只允许有一个构造函数
        } else {
            c1 = null;
        }

        if (c1 == null || c1.getParameterCount() == 0) {
            tryBuildBeanOfClass3(clz, builder, anno, null, null);
        } else {
            tryMethodParamsGather(this, 2, clz, c1.getParameters(), (args2) -> {
                tryBuildBeanOfClass3(clz, builder, anno, c1, args2);
            });
        }
    }


    private void tryBuildBeanOfClass3(Class<?> clz, BeanBuilder builder, Annotation anno, Constructor rawCon, Object[] rawConArgs) throws Throwable {
        //包装
        BeanWrap bw = new BeanWrap(this, clz, rawCon, rawConArgs);
        //执行构建
        builder.doBuild(clz, bw, anno);
        //尝试入库
        this.putWrap(clz, bw);
    }


    /////////


    //::bean事件处理
    /**
     * 添加生命周期 bean
     *
     * @deprecated 2.9
     */
    @Deprecated
    public void lifecycle(LifecycleBean lifecycle) {
        lifecycle(0, (Lifecycle) lifecycle);
    }

    /**
     * 添加生命周期 bean
     *
     * @deprecated 2.9
     */
    @Deprecated
    public void lifecycle(int index, LifecycleBean lifecycle) {
        lifecycle(index, (Lifecycle) lifecycle);
    }

    /**
     * 添加生命周期 bean
     */
    @Override
    public void lifecycle(Lifecycle lifecycle) {
        lifecycle(0, lifecycle);
    }

    /**
     * 添加生命周期 bean
     *
     * @param index 顺序
     */
    @Override
    public void lifecycle(int index, Lifecycle lifecycle) {
        lifecycle(index, 0, lifecycle);
    }

    /**
     * 添加生命周期 bean
     *
     * @param index    顺序
     * @param priority 优先级（此处，相当于二级顺序）
     */
    @Override
    public void lifecycle(int index, int priority, Lifecycle lifecycle) {
        lifecycleBeans.add(new RankEntity<>(lifecycle, index, priority));

        if (started) {
            //如果已启动，则执行启动函数
            RunUtil.runOrThrow(lifecycle::start);
        }
    }


    //已启动标志
    private boolean started;

    /**
     * 是否已启动
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * 启动（一般在插件启动之后，应用完成扫描，再执行）
     */
    public void start() {
        started = true;

        try {
            //开始执行生命周期bean（侧重做初始化） //支持排序
            startBeanLifecycle();

            //开始注入审查 //支持自动排序
            startInjectReview(false);

            //开始之后
            postStartBeanLifecycle();
        } catch (Throwable e) {
            throw new IllegalStateException("AppContext start failed", e);
        }
    }

    /**
     * 开始Bean生命周期（支持排序）
     */
    private void startBeanLifecycle() throws Throwable {
        //执行生命周期bean //支持排序
        List<RankEntity<Lifecycle>> beans = new ArrayList<>(lifecycleBeans);
        Collections.sort(beans);

        //start
        for (RankEntity<Lifecycle> b : beans) {
            b.target.start();
        }
    }

    /**
     * 开始Bean生命周期（支持排序）
     */
    private void postStartBeanLifecycle() throws Throwable {
        //执行生命周期bean //支持排序
        List<RankEntity<Lifecycle>> beans = new ArrayList<>(lifecycleBeans);
        Collections.sort(beans);

        //postStart
        for (RankEntity<Lifecycle> b : beans) {
            b.target.postStart();
        }
    }

    /**
     * 开始注入审查（支持自动排序）
     */
    private void startInjectReview(boolean onlyMethod) throws Throwable {
        //全部跑完后，检查注入情况
        List<InjectGather> gatherList = null;

        if (onlyMethod) {
            gatherList = gatherSet.stream().filter(g1 -> g1.isDone() == false && g1.isMethod() == true)
                    .collect(Collectors.toList());
        } else {
            gatherList = gatherSet.stream().filter(g1 -> g1.isDone() == false)
                    .collect(Collectors.toList());
        }


        if (gatherList.size() > 0) {
            for (InjectGather gather : gatherList) {
                IndexUtil.buildGatherIndex(gather, gatherList);
            }

            Collections.sort(gatherList);
            for (InjectGather g1 : gatherList) {
                g1.check();
            }
        }
    }

    /**
     * 停止（一般在插件停止之后，再执行）
     */
    public void prestop() {
        started = false;

        try {
            //执行生命周期bean //支持排序
            List<RankEntity<Lifecycle>> beans = new ArrayList<>(lifecycleBeans);
            Collections.sort(beans);

            for (RankEntity<Lifecycle> b : beans) {
                try {
                    b.target.preStop();
                } catch (Throwable e) {
                    //e.printStackTrace();
                }
            }
        } catch (Throwable ignored) {
            LogUtil.global().warn("AppContext prestop error", ignored);
        }
    }

    /**
     * 停止（一般在插件停止之后，再执行）
     */
    public void stop() {
        started = false;

        try {
            //执行生命周期bean //支持排序
            List<RankEntity<Lifecycle>> beans = new ArrayList<>(lifecycleBeans);
            Collections.sort(beans);

            for (RankEntity<Lifecycle> b : beans) {
                try {
                    b.target.stop();
                } catch (Throwable e) {
                    //e.printStackTrace();
                }
            }

            //执行 Closeable 接口的 bean
            beanStop0();
        } catch (Throwable ignored) {
            LogUtil.global().warn("AppContext stop error", ignored);
        }
    }
}