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
package org.noear.solon.core;

import org.noear.eggg.*;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.core.convert.ConverterFactory;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.runtime.IndexFileUtil;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.core.util.*;
import org.noear.solon.core.wrap.*;
import org.noear.solon.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

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
    static final Logger log = LoggerFactory.getLogger(AppContext.class);

    public AppContext() {
        this(new Props());
    }

    public AppContext(Props props) {
        this(Thread.currentThread().getContextClassLoader(), props);
    }

    public AppContext(ClassLoader classLoader, Props props) {
        this(Solon.app(), classLoader, props);
    }

    public AppContext(SolonApp app, ClassLoader classLoader, Props props) {
        super(app, classLoader, props);
        initialize();
        lifecycle(Constants.LF_IDX_FIELD_COLLECTION_INJECT, () -> {
            this.startInjectReview(0);
        });
        lifecycle(Constants.LF_IDX_PARAM_COLLECTION_INJECT, () -> {
            this.startInjectReview(1);
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

    private final Map<MethodKey, MethodWrap> methodCached = new ConcurrentHashMap<>();
    private final Set<Class<?>> beanBuildedCached = new HashSet<>();

    private final Set<InjectGather> gatherSet = new HashSet<>();

    /**
     * 获取方法包装（方便 aot 收集）
     *
     * @param method 方法
     * @deprecated 3.7 {@link #methodWrap(ClassEggg, Method)}
     */
    @Deprecated
    public MethodWrap methodGet(Method method) {
        return methodGet(method.getDeclaringClass(), method);
    }


    /**
     * 获取方法包装（方便 aot 收集）
     *
     * @param method 方法
     * @deprecated 3.7 {@link #methodWrap(ClassEggg, Method)}
     */
    @Deprecated
    public MethodWrap methodGet(Class<?> clz, Method method) {
        MethodKey methodKey = new MethodKey(method, clz);

        return methodCached.computeIfAbsent(methodKey, k -> new MethodWrap(this, clz, EgggUtil.getClassEggg(clz).findMethodEgggOrNew(method)));
    }

    public MethodWrap methodWrap(ClassEggg ce, Method m) {
        MethodKey methodKey = new MethodKey(m, ce.getType());

        return methodCached.computeIfAbsent(methodKey, k -> new MethodWrap(this, ce.getType(), ce.findMethodEgggOrNew(m)));
    }

    public MethodWrap methodWrap(ClassEggg ce, MethodEggg me) {
        MethodKey methodKey = new MethodKey(me.getMethod(), ce.getType());

        return methodCached.computeIfAbsent(methodKey, k -> new MethodWrap(this, ce.getType(), me));
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
            tryImport(clz, bw.annotations());

            //尝试填充属性 //3.1
            tryFill(bw.raw(), bw.annotations());

            //构建小饼
            beanExtractOrProxy(bw, true, false);

            //特定能力接口交付
            beanDeliver(bw);

            //注册到容器 //Configuration 不进入二次注册
            //beanRegister(bw,bw.name(),bw.typed());

            //支持基类注册
            beanRegisterSupI(clz, bw);
        });

        beanExtractorAdd(Bean.class, (bw, m, anno) -> {
            tryBuildBeanOfMethod(m, bw, anno.priority(), (mw, raw) -> {
                tryBuildBeanOfMethod3(mw, bw, raw, anno);
            });

            //如果有注解，不是 public 时，则告警提醒（以后改为异常）//v3.0
            if (Modifier.isPublic(m.getModifiers()) == false) {
                log.warn("This @" + anno.annotationType().getSimpleName() + " method is not public: " + m.getDeclaringClass().getName() + ":" + m.getName());
            }
        });

        beanExtractorAdd(Managed.class, (bw, m, mm) -> {
            ManagedToBeanAnno anno = new ManagedToBeanAnno(mm);

            tryBuildBeanOfMethod(m, bw, (mw, raw) -> {
                tryBuildBeanOfMethod3(mw, bw, raw, anno);
            });

            //如果有注解，不是 public 时，则告警提醒（以后改为异常）//v3.0
            if (Modifier.isPublic(m.getModifiers()) == false) {
                log.warn("This @" + anno.annotationType().getSimpleName() + " method is not public: " + m.getDeclaringClass().getName() + ":" + m.getName());
            }
        });

        //注册 @Component 构建器
        beanBuilderAdd(Component.class, (clz, bw, anno) -> {
            String beanName = Utils.annoAlias(anno.value(), anno.name());

            bw.nameSet(beanName);
            bw.tagSet(anno.tag());
            bw.typedSet(anno.typed());

            //确定顺序位
            bw.indexSet(anno.index());

            beanComponentized(bw, anno.delivered());
        });

        //注册 @Managed 构建器
        beanBuilderAdd(Managed.class, (clz, bw, anno) -> {
            String beanName = Utils.annoAlias(anno.value(), anno.name());

            bw.nameSet(beanName);
            bw.tagSet(anno.tag());
            bw.typedSet(anno.typed());

            //确定顺序位
            bw.indexSet(anno.index());

            beanComponentized(bw, anno.delivered());
        });

        //注册 @Remoting 构建器
        beanBuilderAdd(Remoting.class, (clz, bw, anno) -> {
            //设置remoting状态
            bw.remotingSet(true);
            //注册到容器
            beanRegister(bw, "", false);

            if (app() != null) {
                app().router().add(bw);
            }
        });

        //注册 @Controller 构建器
        beanBuilderAdd(Controller.class, (clz, bw, anno) -> {
            if (app() != null) {
                app().router().add(bw);
            }
        });

        //注册 @Inject 注入器
        beanInjectorAdd(Inject.class, new BeanInjector<Inject>() {
            @Override
            public void doInject(VarHolder vh, Inject anno) {
                beanInject(vh, anno.value(), anno.required(), anno.autoRefreshed());
            }

            @Override
            public void doFill(Object obj, Inject anno) {
                beanFillProperties(obj, anno);
            }
        });

        beanInjectorAdd(BindProps.class, new BeanInjector<BindProps>() {
            @Override
            public void doInject(VarHolder vh, BindProps anno) {
                //不支持注入
            }

            @Override
            public void doFill(Object obj, BindProps anno) {
                cfg().getProp(anno.prefix()).bindTo(obj);
            }
        });

        //注册 @To 拦截器
        beanInterceptorAdd(To.class, inv -> {
            Object rst = inv.invoke();

            Context ctx = Context.current();
            if (ctx != null) {
                To anno = inv.method().getAnnotation(To.class);
                if (anno.value().length > 0) {
                    //支持模板处理
                    List<String> list = new ArrayList<>(anno.value().length);
                    for (String val : anno.value()) {
                        list.add(SnelUtil.evalTmpl(val, inv));
                    }
                    ctx.attrSet(Constants.ATTR_TO, list);
                }
            }

            return rst;
        });
    }

    /**
     * 组件化处理
     *
     * @param bw        bean 包装器
     * @param delivered 要交付的（特定能力接口交付）
     */
    protected void beanComponentized(BeanWrap bw, boolean delivered) {
        //尝试提取函数并确定自动代理
        beanExtractOrProxy(bw);

        //特定能力接口交付
        if (delivered) {
            beanDeliver(bw);
        }

        //注册到容器
        beanRegister(bw, bw.name(), bw.typed());


        //单例，进行事件通知
        if (bw.singleton()) {
            //EventBus.publish(bw.raw()); //@deprecated
            beanPublish(bw);
        }
    }

    @Override
    protected void beanInject(VarHolder vh, String name, boolean required, boolean autoRefreshed) {
        super.beanInject(vh, name, required, autoRefreshed);

        if (vh.isDone()) {
            return;
        }

        try {
            if (Utils.isEmpty(name) && vh.getTypeEggg().isParameterizedType()) {
                TypeEggg typeEggg = vh.getTypeEggg();

                if (List.class == vh.getType()) {
                    //支持 List<Bean> 注入 //@since 3.0
                    Type tmp = typeEggg.getActualTypeArguments()[0];

                    final ParameterizedType genericType; //过滤泛型
                    final Type type;
                    if (tmp instanceof ParameterizedType) {
                        genericType = ((ParameterizedType) tmp);
                        type = ((ParameterizedType) tmp).getRawType();
                    } else {
                        genericType = null;
                        type = tmp;
                    }

                    if (type instanceof Class) {
                        if (vh.isField() == false) {
                            vh.setDependencyType((Class<?>) type);
                        }
                        vh.required(required);
                        //设置默认值（放下面）
                        vh.setValueDefault(() -> this.getBeansOfType((Class<? extends Object>) type, genericType));
                    }
                } else if (Map.class == vh.getType()) {
                    //支持 Map<String,Bean> 注入 //@since 3.0

                    Type keyType = typeEggg.getActualTypeArguments()[0];
                    TypeEggg valType = EgggUtil.getTypeEggg(typeEggg.getActualTypeArguments()[1]);

                    if (String.class == keyType) {
                        if (vh.isField() == false) {
                            vh.setDependencyType(valType.getType());
                        }
                        vh.required(required);
                        //设置默认值（放下面）
                        vh.setValueDefault(() -> this.getBeansMapOfType(valType.getType(), valType.getGenericType()));
                    }
                }
            }
        } finally {
            if (isStarted()) {
                //测试或启动之后的注入
                vh.commit();
            }
        }
    }

    /**
     * 尝试 bean 交付（特定能力接口交付）
     *
     * @param bw Bean 包装
     */
    public void beanDeliver(BeanWrap bw) {
        if (bw.raw() == null) {
            return;
        }

        //Plugin
        if (bw.raw() instanceof Plugin) {
            //如果是插件，则插入
            throw new IllegalStateException("'Plugin' cannot be component, please use 'LifecycleBean': " + bw.clz().getName());
        }

        String singletonHint = null;

        //LifecycleBean（替代 Plugin，提供组件的生态周期控制）
        if (bw.raw() instanceof LifecycleBean) {
            //让注解产生的生命周期，排序晚1个点
            int index = bw.index();
            if (index == 0) {
                index = org.noear.solon.core.util.IndexUtil.buildLifecycleIndex(bw.rawClz());
            }

            lifecycle(index + 1, bw.raw());
            singletonHint = "LifecycleBean";
        }

        //EventListener
        if (bw.raw() instanceof EventListener) {
            addEventListener(bw.clz(), bw);
            singletonHint = "EventListener";
        }

        ///////////////////////

        if (app() == null) {
            return;
        }

        //LoadBalance.Factory
        if (bw.raw() instanceof LoadBalance.Factory) {
            FactoryManager.getGlobal().loadBalanceFactory(bw.raw());
            singletonHint = "LoadBalance.Factory";
        }

        //Handler
        if (bw.raw() instanceof Handler) {
            //不再支持 @Bean, @Managed @Mapping fun() //v3.0
            Mapping mapping = bw.clz().getAnnotation(Mapping.class);
            if (mapping != null) {
                app().router().add(bw);
                singletonHint = "Handler";
            }
        }

        //Serializer
        if (bw.raw() instanceof Serializer) {
            app().serializers().register(bw.name(), bw.raw());
            singletonHint = "Serializer";
        }

        //Render
        if (bw.raw() instanceof Render) {
            //指定 mapping
            if (Assert.isNotEmpty(bw.name())) {
                app().renders().register(bw.name(), (Render) bw.raw());
            }

            //接口 mapping
            app().renders().register((Render) bw.raw());

            singletonHint = "Render";
        }

        //EntityConverter //v3.6
        if (bw.raw() instanceof EntityConverter) {
            app().chains().addEntityConverter(bw.raw(), bw.index());
            singletonHint = "EntityConverter";
        }

        //RenderFactory //将弃用 v3.6
        if (bw.raw() instanceof RenderFactory) {
            app().renders().register((RenderFactory) bw.raw());
            singletonHint = "RenderFactory";
        }

        //ActionExecuteHandler //将弃用 v3.6
        if (bw.raw() instanceof ActionExecuteHandler) {
            //app().chains().addExecuteHandler(bw.raw(), bw.index());
            app().chains().addEntityConverter(new EntityConverterFromExecutor(bw.raw()), bw.index());
            singletonHint = "ActionExecuteHandler";
            log.warn("The ActionExecuteHandler will be deprecated. Please use EntityConverter instead: {}", bw.clz().getName());
        }

        //Filter
        if (bw.raw() instanceof Filter) {
            app().filter(bw.index(), bw.raw());
            singletonHint = "Filter";
        }

        //RouterInterceptor
        if (bw.raw() instanceof RouterInterceptor) {
            app().routerInterceptor(bw.index(), bw.raw());
            singletonHint = "RouterInterceptor";
        }

        //ReturnValueHandler
        if (bw.raw() instanceof ReturnValueHandler) {
            app().chains().addReturnHandler(bw.raw(), bw.index());
            singletonHint = "ReturnValueHandler";
        }

        //MethodArgumentResolver
        if (bw.raw() instanceof MethodArgumentResolver) {
            app().chains().addArgumentResolver(bw.raw(), bw.index());
            singletonHint = "MethodArgumentResolver";
        }

        //Converter
        if (bw.raw() instanceof Converter) {
            Converter c = bw.raw();
            app().converters().register(c);
            singletonHint = "Converter";
        }

        //ConverterFactory
        if (bw.raw() instanceof ConverterFactory) {
            ConverterFactory cf = bw.raw();
            app().converters().register(cf);
            singletonHint = "ConverterFactory";
        }


        if (bw.singleton() == false && singletonHint != null) {
            log.warn(singletonHint + " does not support @Singleton(false), class=" + bw.clz().getName());
        }
    }

    /**
     * 添加事件监听支持
     */
    private void addEventListener(Class<?> clz, BeanWrap bw) {
        Type tType = EgggUtil.findGenericInfo(clz, EventListener.class).get("T");
        if (tType instanceof Class<?>) {
            EventBus.subscribe((Class<? extends Object>) tType, bw.index(), bw.raw());
        }
    }

    //::提取

    /**
     * 为一个对象提取函数或自动代理
     */
    public void beanExtractOrProxy(BeanWrap bw) {
        beanExtractOrProxy(bw, true, true);
    }

    /**
     * 为一个对象提取函数或自动代理
     *
     * @param tryExtract 尝试提取
     * @param tryProxy   尝试代理
     */
    public void beanExtractOrProxy(BeanWrap bw, boolean tryExtract, boolean tryProxy) {
        if (bw == null || bw.proxy() != null) { //如果代理不为 null，说明来过
            return;
        }

        boolean enableProxy = false;
        List<Map.Entry<MethodEggg, Map.Entry<Annotation, BeanExtractor>>> extraList = new ArrayList<>();

        if (beanExtractors.size() > 0 || beanInterceptors.size() > 0) {
            ClassEggg classEggg = bw.rawEggg();

            for (MethodEggg me : classEggg.getOwnMethodEgggs()) { //只支持公有或自有函数检查
                for (Annotation a : me.getAnnotations()) {
                    if (tryExtract) {
                        BeanExtractor extr = beanExtractors.get(a.annotationType());
                        if (extr != null) {
                            //有提取处理
                            extraList.add(new AbstractMap.SimpleEntry<>(me, new AbstractMap.SimpleEntry<>(a, extr)));
                        }
                    }

                    if (tryProxy) {
                        //是否需要自动代理
                        enableProxy = enableProxy || beanInterceptorHas(a);
                    }

                }
            }
        }

        //先尝试代理
        if (tryProxy) {
            //是否需要自动代理
            enableProxy = enableProxy || beanInterceptorHas(bw.clz());

            if (enableProxy) {
                ProxyBinder.global().binding(bw);
            }
        }

        //再尝试提取
        for (Map.Entry<MethodEggg, Map.Entry<Annotation, BeanExtractor>> ma : extraList) {
            MethodEggg me = ma.getKey();
            Annotation a = ma.getValue().getKey();
            BeanExtractor be = ma.getValue().getValue();

            try {
                //起到 aot 注册效果
                if (NativeDetector.isAotRuntime()) {
                    aot().registerMethodEggg(me);
                }
                be.doExtract(bw, me.getMethod(), a);
            } catch (Throwable e) {
                e = Utils.throwableUnwrap(e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    //::注入

    /**
     * 为一个对象注入（可以重写）
     */
    public void beanInject(Object obj) {
        if (obj == null) {
            return;
        }

        ClassEggg clzEggg = EgggUtil.getClassEggg(obj.getClass());
        List<FieldEggg> fgList = new ArrayList<>();

        //支持父类注入(找到有注解的字段)
        for (FieldEggg fg : clzEggg.getAllFieldEgggs()) { //非静态 和 静态
            if (fg.getAnnotations().length > 0) {
                fgList.add(fg);
            }
        }

        if (fgList.size() == 0) {
            //略过
        } else {
            //需要注入（可能）
            InjectGather gather = new InjectGather(0, clzEggg.getType(), true, fgList.size(), null);

            //添加到集合
            gatherSet.add(gather);

            //添加要收集的字段
            for (FieldEggg fw : fgList) {
                VarHolder vh = new VarHolderOfField(this, fw, obj, gather);
                gather.add(vh);
                tryInject(vh, fw.getAnnotations());
            }
        }
    }

    ////////////

    /**
     * 排除扫描类（需要在扫描之前排除）
     */
    public void beanExclude(Class<?>... clzs) {
        //相当于提前构建了
        for (Class<?> clz : clzs) {
            beanBuildedCached.add(clz);
        }
    }

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
        if (classLoader == null || Utils.isEmpty(basePackage)) {
            return;
        }

        if (NativeDetector.isAotRuntime()) {
            //（aot 运行时）
            List<String> clzIndexs = new ArrayList<>();
            List<Class<?>> clzList = new ArrayList<>();

            String dir = basePackage.replace('.', '/');
            ScanUtil.scan(classLoader, dir, n -> n.endsWith(".class"), name -> {
                //直接回调可减少一次 HashSet 收集（提升性能）；tryBuildBeanOfClass 有重复过滤（不用担心重复）
                String clzName = name.substring(0, name.length() - 6).replace('/', '.');

                Class<?> clz = ClassUtil.loadClass(classLoader, clzName);
                if (clz != null) {
                    clzList.add(clz);
                }
            });

            doBuildBeans(clzList, clzIndexs);

            if (clzIndexs.size() > 0) {
                // 排序，确保索引文件内容稳定
                Collections.sort(clzIndexs);
                // 写入索引文件
                IndexFileUtil.writeIndexFile(basePackage, clzIndexs);
            }
        } else {
            //（非 aot 运行时） 优先使用类索引文件（如果存在）
            List<Class<?>> clzList = new ArrayList<>();

            Collection<String> clzNames = IndexFileUtil.loadIndexFile(basePackage);
            if (clzNames != null) {
                for (String clzName : clzNames) {
                    Class<?> clz = ClassUtil.loadClass(classLoader, clzName);
                    if (clz != null) {
                        clzList.add(clz);
                    }
                }
            } else {
                String dir = basePackage.replace('.', '/');
                ScanUtil.scan(classLoader, dir, n -> n.endsWith(".class"), name -> {
                    //直接回调可减少一次 HashSet 收集（提升性能）；tryBuildBeanOfClass 有重复过滤（不用担心重复）
                    String clzName = name.substring(0, name.length() - 6).replace('/', '.');

                    Class<?> clz = ClassUtil.loadClass(classLoader, clzName);
                    if (clz != null) {
                        clzList.add(clz);
                    }
                });
            }

            doBuildBeans(clzList, null);
        }
    }

    //两阶段加载
    private void doBuildBeans(Collection<Class<?>> clzList, List<String> clzIndexs) {
        // 创建两个集合：配置类集合和其他类集合
        List<Class<?>> configClasses = new ArrayList<>();
        List<Class<?>> otherClasses = new ArrayList<>();

        // 先分析所有类，分类存储
        for (Class<?> clz : clzList) {
            if (clz != null) {
                // 检查是否为配置类（带有@Configuration注解）
                if (clz.isAnnotationPresent(Configuration.class)) {
                    configClasses.add(clz);
                } else {
                    otherClasses.add(clz);
                }
            }
        }

        if (clzIndexs == null) {
            for (Class<?> clz : configClasses) {
                tryBuildBeanOfClass(clz);
            }

            for (Class<?> clz : otherClasses) {
                tryBuildBeanOfClass(clz);
            }
        } else { //for aot
            for (Class<?> clz : configClasses) {
                if (tryBuildBeanOfClass(clz) > build_bean_ofclass_state0) {
                    clzIndexs.add(clz.getName());
                }
            }

            for (Class<?> clz : otherClasses) {
                if (tryBuildBeanOfClass(clz) > build_bean_ofclass_state0) {
                    clzIndexs.add(clz.getName());
                }
            }
        }
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
     * 尝试导入
     */
    protected void tryImport(Class<?> clz, Annotation[] annS) {
        for (Annotation a1 : annS) {
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
    }

    /**
     * 尝试为bean填充
     */
    protected void tryFill(Object obj, Annotation[] annS) {
        if (obj == null) {
            return;
        }

        for (Annotation anno : annS) {
            TypeMap<BeanInjector<?>> biMap = beanInjectors.get(anno.annotationType());
            if (biMap != null) {
                //只允许一个注入器有效 //如果有多个略过
                BeanInjector injector = biMap.get(obj.getClass());
                if (injector != null) {
                    injector.doFill(obj, anno);
                    return;
                }
            }
        }
    }

    /**
     * 尝试为bean注入
     */
    protected void tryInject(VarHolder vh, Annotation[] annS) {
        for (Annotation a : annS) {
            TypeMap<BeanInjector<?>> biMap = beanInjectors.get(a.annotationType());
            if (biMap != null) {
                //只允许一个注入器有效 //如果有多个略过
                BeanInjector injector = biMap.get(vh.getType());
                if (injector != null) {
                    injector.doInject(vh, a);
                    return;
                }
            }
        }

        //如果没有注入器，则为null。用于触发事件
        vh.setValue(null);
    }

    /**
     * 尝试托管方法调用
     */
    public void tryBuildBeanOfMethod(Method m, BeanWrap bw, BiConsumerEx<MethodEggg, Object> completionConsumer) throws Throwable {
        tryBuildBeanOfMethod(m, bw, 0, completionConsumer);
    }

    /**
     * 尝试托管方法调用
     */
    private void tryBuildBeanOfMethod(Method m, BeanWrap bw, int priority, BiConsumerEx<MethodEggg, Object> completionConsumer) throws Throwable {
        if (NativeDetector.isAotRuntime()) {
            //如果是 aot 则注册函数
            aot().registerMethodEggg(bw.rawEggg().findMethodEgggOrNew(m));
        }

        Condition mc = m.getAnnotation(Condition.class);

        if (started == false && ConditionUtil.ifMissingBean(mc)) {
            priority = (mc.priority() > 0 ? mc.priority() : priority);
            lifecycle(Constants.LF_IDX_METHOD_CONDITION_IF_MISSING, priority, () -> tryBuildBeanOfMethod0(bw, m, mc, completionConsumer));
        } else {
            tryBuildBeanOfMethod0(bw, m, mc, completionConsumer);
        }
    }

    private void tryBuildBeanOfMethod0(BeanWrap bw, Method m, Condition mc, BiConsumerEx<MethodEggg, Object> completionConsumer) throws Throwable {
        //增加条件检测
        if (ConditionUtil.test(this, mc) == false) {
            return;
        }

        //支持非公有函数
        ClassUtil.accessibleAsTrue(m);

        MethodEggg me = bw.rawEggg().findMethodEgggOrNew(m);
        if (NativeDetector.isAotRuntime()) {
            aot().registerMethodEggg(me);
        }

        //开始执行
        if (ConditionUtil.ifBean(mc)) {
            //如果有 onBean 条件
            ConditionUtil.onBeanRun(mc, this, () -> tryBuildBeanOfMethod1(me, bw, completionConsumer));
        } else {
            //没有 onBean 条件
            tryBuildBeanOfMethod1(me, bw, completionConsumer);
        }
    }

    private void tryBuildBeanOfMethod1(MethodEggg me, BeanWrap bw, BiConsumerEx<MethodEggg, Object> completionConsumer) throws Throwable {
        if (me.getParamCount() == 0) {
            //0.没有参数
            tryBuildBeanOfMethod2(me, bw, BeanWrap.args_empty, completionConsumer);
        } else {
            tryBuildArgsOfMethod(bw.context(), 1, me.getReturnType(), me.getParamEgggAry(), (args2) -> {
                RunUtil.runOrThrow(() -> tryBuildBeanOfMethod2(me, bw, args2, completionConsumer));
            });
        }
    }

    /**
     * 尝试托管方法参数收集
     */
    protected void tryBuildArgsOfMethod(AppContext context, int label, Class<?> outType, Collection<ParamEggg> paramAry, ConsumerEx<Object[]> completionConsumer) {
        //1.构建参数 (requireRun=false => true) //运行条件已经确认过，且必须已异常
        InjectGather gather = new InjectGather(label, outType, true, paramAry.size(), (args2) -> {
            //变量收集完成后，会回调此处
            completionConsumer.accept(args2);
        });

        //1.1.登录到集合
        gatherSet.add(gather);

        //1.2.添加要收集的参数；并为参数注入（注入是异步的；全部完成后，VarGather 会回调）
        for (ParamEggg pw1 : paramAry) {
            VarHolder vh = new VarHolderOfParam(context, pw1, gather);
            gather.add(vh);

            if (pw1.getAnnotations().length == 0) {
                //没带注解的，算必须
                beanInject(vh, null, true, false);
            } else {
                tryInject(vh, pw1.getAnnotations());
            }
        }
    }

    private void tryBuildBeanOfMethod2(MethodEggg me, BeanWrap bw, Object[] args, BiConsumerEx<MethodEggg, Object> completionConsumer) {
        try {
            Object raw = me.invoke(bw.raw(), args);

            if (raw != null) {
                //尝试填充属性 //v3.1
                tryFill(raw, me.getAnnotations());

                completionConsumer.accept(me, raw);
            }
        } catch (Throwable ex) {
            Class<?> declClz = me.getMethod().getDeclaringClass();
            Class<?> fileClz = declClz;
            if (declClz.isMemberClass()) {
                fileClz = declClz.getEnclosingClass();
            }

            StringBuilder buf = new StringBuilder();
            buf.append("Build bean of method failed: \r\n\tat ");
            buf.append(declClz.getName()).append(".");
            buf.append(me.getName()).append("(").append(fileClz.getSimpleName()).append(".java:0)");

            throw new IllegalStateException(buf.toString(), ex);
        }
    }

    /**
     * 尝试托管方法 bean 注册
     *
     */
    protected void tryBuildBeanOfMethod3(MethodEggg me, BeanWrap bw, Object raw, Bean anno) {
        Class<?> beanClz = me.getReturnType();
        Type beanGtp = me.getGenericReturnType();

        //产生的bean，不再支持二次注入

        BeanWrap m_bw = null;
        if (raw instanceof BeanWrap) {
            m_bw = (BeanWrap) raw;
        } else {
            if (anno.autoInject() || anno.injected()) {
                //执行注入
                beanInject(raw);
            }

            //@Bean,@Managed 动态构建的bean, 可通过事件广播进行扩展 //后面不用再发布了
            //EventBus.publish(raw);//@deprecated

            //动态构建的bean，都用新生成wrap（否则会类型混乱）
            m_bw = new BeanWrap(this, beanClz, raw, null, false, anno.initMethod(), anno.destroyMethod());
        }

        String beanName = Utils.annoAlias(anno.value(), anno.name());

        m_bw.nameSet(beanName);
        m_bw.tagSet(anno.tag());
        m_bw.typedSet(anno.typed());

        //确定顺序位
        m_bw.indexSet(anno.index());
        m_bw.done();

        //尝试提取函数并确定自动代理
        if (anno.autoProxy()) {
            beanExtractOrProxy(m_bw);
        }

        //特定能力接口交付
        if (anno.delivered()) {
            beanDeliver(m_bw);
        }

        //注册到容器
        beanRegister(m_bw, beanName, anno.typed());

        //尝试泛型注册(通过 name 实现)
        if (beanGtp instanceof ParameterizedType) {
            putWrap(beanGtp.getTypeName(), m_bw);
            m_bw.genericList().add((ParameterizedType) beanGtp);
        }

        //@Bean,@Managed 动态产生的 beanWrap（含 name,tag,attrs），进行事件通知
        beanPublish(m_bw);
    }

    private static final int build_bean_ofclass_state0 = 0; //没有处理
    private static final int build_bean_ofclass_state1 = 1; //异步处理，或条件未达（可以返回 null）
    private static final int build_bean_ofclass_state2 = 2; //有处理了（可以返回 warp）

    /**
     * 根据类尝试生成 bean（用 protected，方便扩展时复用）
     *
     * @return 处理状态
     */
    protected int tryBuildBeanOfClass(Class<?> clz) {
        //去重处理
        if (beanBuildedCached.contains(clz)) {
            return build_bean_ofclass_state2;
        } else {
            beanBuildedCached.add(clz);
        }

        Annotation[] annoS = clz.getAnnotations();

        if (annoS.length > 0) {
            //return handled?
            Condition cc = clz.getAnnotation(Condition.class);

            if (started == false && ConditionUtil.ifMissingBean(cc)) {
                lifecycle(Constants.LF_IDX_CLASS_CONDITION_IF_MISSING, cc.priority(), () -> tryBuildBeanOfClass0(clz, cc, annoS));
                return build_bean_ofclass_state1;
            } else {
                return tryBuildBeanOfClass0(clz, cc, annoS);
            }
        } else {
            return build_bean_ofclass_state0;
        }
    }

    private int tryBuildBeanOfClass0(Class<?> clz, Condition cc, Annotation[] annS) {
        //return handled?
        if (ConditionUtil.test(this, cc) == false) {
            return build_bean_ofclass_state1;
        }

        if (ConditionUtil.ifBean(cc)) {
            ConditionUtil.onBeanRun(cc, this, () -> tryBuildBeanOfClass1(clz, annS));
            return build_bean_ofclass_state1;
        } else {
            return tryBuildBeanOfClass1(clz, annS);
        }
    }

    private int tryBuildBeanOfClass1(Class<?> clz, Annotation[] annS) {
        //return state?
        int state = build_bean_ofclass_state0;

        for (Annotation a : annS) {
            TypeMap<BeanBuilder<?>> bbMap = beanBuilders.get(a.annotationType());

            if (bbMap != null) {
                BeanBuilder builder = bbMap.get(clz);
                if (builder != null) {
                    try {
                        state = build_bean_ofclass_state2;
                        tryBuildBeanOfClass2(clz, builder, a, annS);
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

    private void tryBuildBeanOfClass2(Class<?> clz, BeanBuilder builder, Annotation anno, Annotation[] annoS) throws Throwable {
        ClassEggg clzEggg = EgggUtil.getClassEggg(clz);

        if (clzEggg.getCreator() == null) {
            //没有（可能是接口）
            tryBuildBeanOfClass3(clz, builder, anno, null, null, annoS);
        } else if (clzEggg.getCreator().getParamCount() == 0) {
            //默认构造方法
            tryBuildBeanOfClass3(clz, builder, anno, (Constructor) clzEggg.getCreator().getConstr(), BeanWrap.args_empty, annoS);
        } else {
            //有参数的构造方法。需处理泛型参数
            tryBuildArgsOfMethod(this, 2, clz, clzEggg.getCreator().getParamEgggAry(), (args2) -> {
                tryBuildBeanOfClass3(clz, builder, anno, (Constructor) clzEggg.getCreator().getConstr(), args2, annoS);
            });
        }
    }

    private void tryBuildBeanOfClass3(Class<?> clz, BeanBuilder builder, Annotation anno, Constructor rawCon, Object[] rawConArgs, Annotation[] annoS) throws Throwable {
        //包装
        BeanWrap bw = new BeanWrap(this, clz, rawCon, rawConArgs, annoS);
        //执行构建
        builder.doBuild(clz, bw, anno);
        //尝试入库
        bw.done();
        this.putWrap(clz, bw);
    }


    /////////


    //::bean事件处理

    /**
     * 添加生命周期 bean（保持向下兼容）
     */
    public void lifecycle(LifecycleBean lifecycle) {
        lifecycle(0, (Lifecycle) lifecycle);
    }

    /**
     * 添加生命周期 bean（保持向下兼容）
     */
    public void lifecycle(int index, LifecycleBean lifecycle) {
        lifecycle.setAppContext(this);

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

        if (isStarting()) {
            //如果开始启动了，则执行启动函数
            RunUtil.runOrThrow(lifecycle::start);
        }

        if (isStarted()) {
            //如果启动完成了，则执行启动提交函数
            RunUtil.runOrThrow(lifecycle::postStart);
        }
    }

    //启动
    private boolean starting;

    //已启动标志
    private boolean started;

    /**
     * 是否正在启动
     */
    public boolean isStarting() {
        return starting;
    }

    /**
     * 是否已启动完成
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * 启动（一般在插件启动之后，应用完成扫描，再执行）
     */
    public void start() {
        //标为开始
        starting = true;

        try {
            //开始执行生命周期bean（侧重做初始化） //支持排序
            startBeanLifecycle();

            //标为已开始（Bean 生周期已启动）
            started = true;

            //开始注入审查 //支持自动排序
            startInjectReview(2);

            //再次审查（审查后可能会产生二次处理）
            startInjectReview(2);

            //开始之后
            postStartBeanLifecycle();
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
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
    private void startInjectReview(int sel) throws Throwable {
        //全部跑完后，检查注入情况
        List<InjectGather> gatherList = new ArrayList<>();

        if (sel == 0) {
            for (InjectGather g1 : gatherSet) {
                if (g1.isDone() == false && g1.isMethod() == false) {
                    gatherList.add(g1);
                }
            }
        } else if (sel == 1) {
            for (InjectGather g1 : gatherSet) {
                if (g1.isDone() == false && g1.isMethod() == true) {
                    gatherList.add(g1);
                }
            }
        } else {
            for (InjectGather g1 : gatherSet) {
                if (g1.isDone() == false) {
                    gatherList.add(g1);
                }
            }
        }

        if (gatherList.size() > 0) {
            for (InjectGather gather : gatherList) {
                org.noear.solon.core.util.IndexUtil.buildGatherIndex(gather, gatherList);
            }

            if (sel > 0) {
                Collections.sort(gatherList);
                for (InjectGather g1 : gatherList) {
                    g1.check();
                }
            } else {
                for (InjectGather gather : gatherList) {
                    gather.commit();
                }
            }
        }
    }

    /**
     * 预停止（一般在插件预停止之后，再执行）
     *
     * @deprecated 3.7 {@link #preStop()}
     */
    @Deprecated
    public void prestop() {
        preStop();
    }

    /**
     * 预停止（一般在插件预停止之后，再执行）
     *
     * @since 3.7
     */
    public void preStop() {
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
            log.warn("AppContext prestop error", ignored);
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
            log.warn("AppContext stop error", ignored);
        }
    }
}