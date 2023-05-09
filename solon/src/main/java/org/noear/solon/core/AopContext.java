package org.noear.solon.core;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.bean.InitializingBean;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.*;
import org.noear.solon.core.wrap.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * Aop 上下文（ 为全局对象；热插拨的插件，会产生独立的上下文）
 *
 * 主要实现四个动作：
 * 1.bean 构建
 * 2.bean 注入（字段 或 参数）
 * 3.bean 提取
 * 4.bean 拦截
 *
 * @author noear
 * @since 1.0
 * */
public class AopContext extends BeanContainer {

    public AopContext() {
        this(null, null);
    }

    public AopContext(ClassLoader classLoader, Props props) {
        super(classLoader, props);
        initialize();
    }


    private final Set<RankEntity<LifecycleBean>> lifecycleBeans = new HashSet<>();

    private final Map<Method, MethodWrap> methodCached = new HashMap<>();
    private final Set<VarGather> gatherSet = new HashSet<>();


    public MethodWrap methodGet(Method method) {
        MethodWrap mw = methodCached.get(method);
        if (mw == null) {
            synchronized (method) {
                mw = methodCached.get(method);
                if (mw == null) {
                    mw = new MethodWrap(this, method);
                    methodCached.put(method, mw);
                }
            }
        }

        return mw;
    }

	/**
	 * 遍历method (拿到的是method包装)
	 */
	public void methodForeach(Consumer<MethodWrap> action) {
		methodCached.values().forEach(action);
	}

    @Override
    public void clear() {
        super.clear();

        methodCached.clear();
        beanCreatedCached.clear();

        gatherSet.clear();

        lifecycleBeans.clear();

        started = false;
    }

    @Override
    protected BeanWrap wrapCreate(Class<?> type, Object bean, String name) {
        return new BeanWrap(this, type, bean, name);
    }

    public AopContext copy() {
        return copy(null, null);
    }

    public AopContext copy(ClassLoader classLoader, Props props) {
        AopContext tmp = new AopContext(classLoader, props);
        copyTo(tmp);
        return tmp;
    }

    /**
     * ::初始化（独立出 initialize，方便重写）
     */
    protected void initialize() {

        //注册 @Configuration 构建器
        beanBuilderAdd(Configuration.class, (clz, bw, anno) -> {
            //尝试导入注解配置（先导）//v1.12
            cfg().loadAdd(clz.getAnnotation(PropertySource.class));

            //尝试导入（可能会导入属性源，或小饼依赖的组件）
            for (Annotation a1 : clz.getAnnotations()) {
                if (a1 instanceof Import) {
                    beanImport((Import) a1);
                } else {
                    beanImport(a1.annotationType().getAnnotation(Import.class));
                }
            }


            //尝试注入属性
            beanInjectProperties(clz, bw.raw());

            //构建小饼
            for (Method m : ClassWrap.get(bw.clz()).getMethods()) {
                Bean ma = m.getAnnotation(Bean.class);

                if (ma != null) {
                    tryCreateBeanOfMethod(bw, m, ma);
                }
            }

            //添加bean形态处理
            addBeanShape(clz, bw, clz);

            //注册到容器 //Configuration 不进入二次注册
            //beanRegister(bw,bw.name(),bw.typed());

            //支持基类注册
            beanRegisterSup0(bw);
        });

        //注册 @Component 构建器
        beanBuilderAdd(Component.class, (clz, bw, anno) -> {
            String beanName = Utils.annoAlias(anno.value(), anno.name());

            bw.nameSet(beanName);
            bw.tagSet(anno.tag());
            bw.typedSet(anno.typed());

            //确定顺序位
            bw.indexSet(anno.index());

            //添加bean形态处理
            addBeanShape(clz, bw, clz);

            //注册到容器
            beanRegister(bw, beanName, anno.typed());

            //尝试提取函数
            beanExtract(bw);

            //单例，进行事件通知
            if (bw.singleton()) {
                EventBus.push(bw.raw()); //@deprecated
                //EventBus.push(bw); //@deprecated
                wrapPublish(bw);
            }
        });

        //注册 @ProxyComponent 构建器
        beanBuilderAdd(ProxyComponent.class, (clz, bw, anno) -> {
            if(NativeDetector.isAotRuntime() == false) {
                throw new IllegalStateException("Missing plugin dependency: 'solon.proxy'");
            }
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
            new HandlerLoader(bw).load(Solon.app());
        });

        //注册 @ServerEndpoint 构建器
        beanBuilderAdd(ServerEndpoint.class, (clz, wrap, anno) -> {
            if (Listener.class.isAssignableFrom(clz)) {
                Listener l = wrap.raw();
                Solon.app().router().add(Utils.annoAlias(anno.value(), anno.path()), anno.method(), l);
            }
        });


        //注册 @Inject 注入器
        beanInjectorAdd(Inject.class, ((fwT, anno) -> {
            beanInject(fwT, anno.value(), anno.required(), anno.autoRefreshed());
        }));
    }

    /**
     * 添加bean的不同形态
     */
    private void addBeanShape(Class<?> clz, BeanWrap bw, AnnotatedElement annoEl) {
        //Plugin
        if (Plugin.class.isAssignableFrom(clz)) {
            //如果是插件，则插入
            Solon.app().plug(bw.raw());
            LogUtil.global().warn("'Plugin' will be deprecated as a component, please use 'LifecycleBean'");return;
        }

        //LifecycleBean（替代 Plugin，提供组件的生态周期控制）
        if (LifecycleBean.class.isAssignableFrom(clz)) {
            //让注解产生的生命周期，排序晚1个点
            int index = bw.index();
            if (index == 0) {
                index = new IndexBuilder().buildIndex(clz);
            }

            lifecycle(index + 1, bw.raw());
        }

        //EventListener
        if (EventListener.class.isAssignableFrom(clz)) {
            addEventListener(clz, bw);
        }

        //LoadBalance.Factory
        if (LoadBalance.Factory.class.isAssignableFrom(clz)) {
            Bridge.upstreamFactorySet(bw.raw());
        }

        //Handler
        if (Handler.class.isAssignableFrom(clz)) {
            Mapping mapping = annoEl.getAnnotation(Mapping.class);
            if (mapping != null) {
                Handler handler = bw.raw();
                Set<MethodType> v0 = MethodTypeUtil.findAndFill(new HashSet<>(), t -> annoEl.getAnnotation(t) != null);
                if (v0.size() == 0) {
                    v0 = new HashSet<>(Arrays.asList(mapping.method()));
                }
                Solon.app().add(mapping, v0, handler);
            }
        }

        //Filter
        if (Filter.class.isAssignableFrom(clz)) {
            Solon.app().filter(bw.index(), bw.raw());
        }

        //RouterInterceptor
        if (RouterInterceptor.class.isAssignableFrom(clz)) {
            Solon.app().routerInterceptor(bw.index(), bw.raw());
        }
    }

    //添加事件监听
    private void addEventListener(Class<?> clz, BeanWrap bw) {
        Class<?>[] ets = GenericUtil.resolveTypeArguments(clz, EventListener.class);
        if (ets != null && ets.length > 0) {
            EventBus.subscribe(ets[0], bw.raw());
        }
    }

    //::提取

    /**
     * 为一个对象提取函数
     */
    public void beanExtract(BeanWrap bw) {
        if (bw == null) {
            return;
        }

        if (beanExtractors.size() == 0) {
            return;
        }

        ClassWrap clzWrap = ClassWrap.get(bw.clz());

        for (Method m : clzWrap.getMethods()) {
            for (Annotation a : m.getAnnotations()) {
                BeanExtractor be = beanExtractors.get(a.annotationType());

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

        ClassWrap clzWrap = ClassWrap.get(obj.getClass());
        List<FieldWrap> fwList = new ArrayList<>();

        //支持父类注入(找到有注解的字段)
        for (Map.Entry<String, FieldWrap> kv : clzWrap.getFieldAllWraps().entrySet()) {
            Annotation[] annS = kv.getValue().annoS;
            if (annS.length > 0) {
                fwList.add(kv.getValue());
            }
        }

        if (obj instanceof InitializingBean) {
            InitializingBean initBean = (InitializingBean)obj;

            if (fwList.size() == 0) {
                //不需要注入
                RunUtil.runOrThrow(initBean::afterInjection);
            } else {
                //需要注入（可能）
                VarGather gather = new VarGather(true, fwList.size(), (args2) -> {
                    RunUtil.runOrThrow(initBean::afterInjection);
                });

                //添加到集合
                gatherSet.add(gather);

                //添加要收集的字段
                for (FieldWrap fw : fwList) {
                    VarHolder varH = fw.holder(this, obj, gather);
                    gather.add(varH);
                    tryInject(varH, fw.annoS);
                }
            }
        } else {
            if (fwList.size() == 0) {

            }else {
                //需要注入（可能）
                VarGather gather = new VarGather(true, fwList.size(), null);

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
    }

    ////////////

    /**
     * 根据配置导入bean
     */
    public void beanImport(Import anno) {
        if (anno != null) {
            //导入类（beanMake）
            for (Class<?> clz : anno.value()) {
                beanMake(clz);
            }

            //扫描包（beanScan）
            for (String pkg : anno.scanPackages()) {
                beanScan(pkg);
            }

            //扫描包（beanScan）
            for (Class<?> src : anno.scanPackageClasses()) {
                beanScan(src);
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
                    className = className.replace("/", ".");

                    Class<?> clz = ClassUtil.loadClass(classLoader, className);
                    if (clz != null) {
                        tryCreateBeanOfClass(clz);
                    }
                });
    }

    /**
     * ::制造 bean 及对应处理
     */
    public BeanWrap beanMake(Class<?> clz) {
        //增加条件检测
        if (ConditionUtil.test(this, clz) == false) {
            return null;
        }

        //包装
        BeanWrap bw = wrap(clz, null);

        tryCreateBean0(bw.clz(), (bb, a) -> {
            bb.doBuild(bw.clz(), bw, a);
        });

        //必然入库
        putWrap(clz, bw);

        return bw;
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
     * 尝试生成 bean
     */
    protected void tryCreateBeanOfMethod(BeanWrap bw, Method m, Bean ma) throws Throwable {
        Condition mc = m.getAnnotation(Condition.class);

        if (started == false && ConditionUtil.ifMissing(mc)) {
            lifecycle(-98, () -> tryCreateBeanOfMethod0(bw, m, ma, mc));
        } else {
            tryCreateBeanOfMethod0(bw, m, ma, mc);
        }
    }

    private void tryCreateBeanOfMethod0(BeanWrap bw, Method m, Bean ma, Condition mc) throws Throwable {
        //增加条件检测
        if (ConditionUtil.test(this, mc) == false) {
            return;
        }

        //支持非公有函数
        m.setAccessible(true);

        MethodWrap mWrap = methodGet(m);

        //有参数的bean，采用线程池处理；所以需要锁等待
        //
        tryBuildBean(ma, mWrap, bw);
    }

    /**
     * 尝试生成 bean，并注册
     */
    protected void tryCreateBeanOfClass(Class<?> clz) {
        Condition cc = clz.getAnnotation(Condition.class);

        if (started == false && ConditionUtil.ifMissing(cc)) {
            lifecycle(-98, () -> tryCreateBeanOfClass0(clz, cc));
        } else {
            tryCreateBeanOfClass0(clz, cc);
        }
    }

    private void tryCreateBeanOfClass0(Class<?> clz, Condition cc) {
        if (ConditionUtil.test(this, cc) == false) {
            return;
        }

        tryCreateBean0(clz, (bb, a) -> {
            //包装
            BeanWrap bw = this.wrap(clz);
            //执行构建
            bb.doBuild(clz, bw, a);
            //尝试入库
            this.putWrap(clz, bw);
        });
    }


    private final Set<Class<?>> beanCreatedCached = new HashSet<>();

    private void tryCreateBean0(Class<?> clz, BiConsumerEx<BeanBuilder, Annotation> consumer) {
        Annotation[] annS = clz.getDeclaredAnnotations();

        if (annS.length > 0) {
            //去重处理
            if (beanCreatedCached.contains(clz)) {
                return;
            } else {
                beanCreatedCached.add(clz);
            }

            for (Annotation a : annS) {
                BeanBuilder builder = beanBuilders.get(a.annotationType());

                if (builder != null) {
                    try {
                        consumer.accept(builder, a);
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
    }

    /**
     * 尝试构建 bean
     *
     * @param anno  bean 注解
     * @param mWrap 方法包装器
     * @param bw    bean 包装器
     */
    protected void tryBuildBean(Bean anno, MethodWrap mWrap, BeanWrap bw) throws Throwable {
        int size2 = mWrap.getParamWraps().length;

        if (size2 == 0) {
            //0.没有参数
            tryBuildBeanDo(anno, mWrap, bw, new Object[]{});
        } else {
            //1.构建参数
            VarGather gather = new VarGather(false, size2, (args2) -> {
                //变量收集完成后，会回调此处
                RunUtil.runOrThrow(() -> tryBuildBeanDo(anno, mWrap, bw, args2));
            });

            //1.1.登录到集合
            gatherSet.add(gather);

            //1.2.添加要收集的参数；并为参数注入（注入是异步的；全部完成后，VarGather 会回调）
            for (ParamWrap pw : mWrap.getParamWraps()) {
                VarHolder varH = new VarHolderOfParam(bw.context(), pw.getParameter(), gather);
                gather.add(varH);

                Annotation[] annoS = pw.getParameter().getDeclaredAnnotations();
                if (annoS.length == 0) {
                    beanInject(varH, null);
                } else {
                    tryInject(varH, annoS);
                }
            }
        }
    }

    protected void tryBuildBeanDo(Bean anno, MethodWrap mWrap, BeanWrap bw, Object[] args) throws Throwable {
        Object raw = mWrap.invoke(bw.raw(), args);
        tryBuildBean0(mWrap, anno, raw);
    }

    protected void tryBuildBean0(MethodWrap mWrap, Bean anno, Object raw) {
        if (raw != null) {
            Class<?> beanClz = mWrap.getReturnType();
            Type beanGtp = mWrap.getGenericReturnType();

            //产生的bean，不再支持二次注入

            BeanWrap m_bw = null;
            if (raw instanceof BeanWrap) {
                m_bw = (BeanWrap) raw;
            } else {
                //@Bean 动态构建的bean, 可通过事件广播进行扩展
                EventBus.push(raw);//@deprecated

                //动态构建的bean，都用新生成wrap（否则会类型混乱）
                m_bw = wrapCreate(beanClz, raw, null);
            }

            String beanName = Utils.annoAlias(anno.value(), anno.name());

            m_bw.nameSet(beanName);
            m_bw.tagSet(anno.tag());
            m_bw.typedSet(anno.typed());

            //确定顺序位
            m_bw.indexSet(anno.index());

            //添加bean形态处理
            addBeanShape(m_bw.clz(), m_bw, mWrap.getMethod());

            //注册到容器
            beanRegister(m_bw, beanName, anno.typed());

            //尝试泛型注册(通过 name 实现)
            if (beanGtp instanceof ParameterizedType) {
                putWrap(beanGtp.getTypeName(), m_bw);
            }

            //@Bean 动态产生的 beanWrap（含 name,tag,attrs），进行事件通知
            //EventBus.push(m_bw); //@deprecated
            wrapPublish(m_bw);
        }
    }

    /////////


    //::bean事件处理

    /**
     * 添加bean加载完成事件
     *
     * @deprecated 2.2
     */
    @Deprecated
    public void beanOnloaded(Consumer<AopContext> fun) {
        beanOnloaded(-1, fun::accept);
    }

    /**
     * 添加bean加载完成事件
     *
     * @deprecated 2.2
     */
    @Deprecated
    public void beanOnloaded(int index, Consumer<AopContext> fun) {
        try {
            lifecycle(index, () -> fun.accept(this));
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 完成加载时调用，会进行事件通知
     *
     * @deprecated 2.2
     */
    @Deprecated
    public void beanLoaded() {
        start();
    }

    /**
     * 添加生命周期 bean
     * */
    public void lifecycle(LifecycleBean lifecycle) {
        lifecycle(0, lifecycle);
    }

    /**
     * 添加生命周期 bean
     * */
    public void lifecycle(int index, LifecycleBean lifecycle) {
        lifecycleBeans.add(new RankEntity<>(lifecycle, index));

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
            //执行生命周期bean //支持排序
            List<RankEntity<LifecycleBean>> beans = new ArrayList<>(lifecycleBeans);
            beans.sort(Comparator.comparingInt(f -> f.index));

            //start
            for (RankEntity<LifecycleBean> b : beans) {
                b.target.start();
            }

            //全部跑完后，检查注入情况
            for(VarGather gather : gatherSet){
                gather.check();
            }
        } catch (Throwable e) {
            throw new IllegalStateException("AopContext start failed", e);
        }
    }

    /**
     * 停止（一般在插件停止之后，再执行）
     */
    public void stop() {
        started = false;

        //执行生命周期bean //支持排序
        List<RankEntity<LifecycleBean>> beans = new ArrayList<>(lifecycleBeans);
        beans.sort(Comparator.comparingInt(f -> f.index));

        for (RankEntity<LifecycleBean> b : beans) {
            try {
                b.target.stop();
            } catch (Throwable e) {
                //e.printStackTrace();
            }
        }
    }
}