package org.noear.solon.core;


import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.InterceptorEntity;
import org.noear.solon.core.handle.HandlerLoader;
import org.noear.solon.core.util.ConvertUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Bean 容器，提供注册及关系映射管理（不直接使用；作为AopContext的基类）
 *
 * @author noear
 * @since 1.0
 * */
public abstract class BeanContainer {
    private final Props props;
    private final ClassLoader classLoader;
    private Map<Class<?>,Object> attrs = new HashMap<>();


    public BeanContainer(ClassLoader classLoader, Props props) {
        this.classLoader = classLoader;
        this.props = props;
    }

    public Props getProps() {
        if (props == null) {
            return Solon.cfg();
        } else {
            return props;
        }
    }

    public Map<Class<?>, Object> getAttrs() {
        return attrs;
    }

    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            return JarClassLoader.global();
        } else {
            return classLoader;
        }
    }

    //////////////////////////
    //
    // 容器存储
    //
    /////////////////////////
    /**
     * bean包装库
     */
    private final Map<Class<?>, BeanWrap> beanWraps = new HashMap<>();
    private final Set<BeanWrap> beanWrapSet = new HashSet<>();
    /**
     * bean库
     */
    private final Map<String, BeanWrap> beans = new HashMap<>();
    /**
     * clz mapping
     */
    private final Map<Class<?>, Class<?>> clzMapping = new HashMap<>();

    //启动时写入
    /**
     * bean 构建器
     */
    protected final Map<Class<?>, BeanBuilder<?>> beanBuilders = new HashMap<>();
    /**
     * bean 注入器
     */
    protected final Map<Class<?>, BeanInjector<?>> beanInjectors = new HashMap<>();
    /**
     * bean 提取器
     */
    protected final Map<Class<?>, BeanExtractor<?>> beanExtractors = new HashMap<>();
    /**
     * bean 拦截器
     * */
    protected final Map<Class<?>, InterceptorEntity> beanInterceptors = new HashMap<>();



    /**
     * bean订阅者
     */
    protected final Set<SubscriberEntity> beanSubscribers = new LinkedHashSet<>();


    public void clear(){
        beanWraps.clear();
        beanWrapSet.clear();
        beans.clear();
        clzMapping.clear();
        attrs.clear();

//        beanBuilders.clear();
//        beanInjectors.clear();
//        beanExtractors.clear();
//        beanInterceptors.clear();
//
//        beanSubscribers.clear();
    }

    /**
     * 容器能力制复到另一个容器
     * */
    public void copyTo(BeanContainer container) {
        beanBuilders.forEach((k, v) -> {
            container.beanBuilders.putIfAbsent(k, v);
        });

        //用于跨容器复制
        beanInjectors.forEach((k, v) -> {
            container.beanInjectors.putIfAbsent(k, v);
        });

        //用于跨容器复制
        beanInterceptors.forEach((k, v) -> {
            container.beanInterceptors.putIfAbsent(k, v);
        });

        beanExtractors.forEach((k, v) -> {
            container.beanExtractors.putIfAbsent(k, v);
        });
    }

    /**
     * 添加 bean builder, injector, extractor
     */
    public <T extends Annotation> void beanBuilderAdd(Class<T> anno, BeanBuilder<T> builder) {
        beanBuilders.put(anno, builder);
    }

    public <T extends Annotation> void beanInjectorAdd(Class<T> anno, BeanInjector<T> injector) {
        beanInjectors.put(anno, injector);
    }

    public <T extends Annotation> void beanExtractorAdd(Class<T> anno, BeanExtractor<T> extractor) {
        beanExtractors.put(anno, extractor);
    }

    /**
     * 添加环绕处理
     *
     * @param index 执行顺序
     * */
    public <T extends Annotation> void beanAroundAdd(Class<T> anno, Interceptor interceptor, int index) {
        beanInterceptors.put(anno, new InterceptorEntity(index, interceptor));
    }

    /**
     * 添加环绕处理
     * */
    public <T extends Annotation> void beanAroundAdd(Class<T> anno, Interceptor interceptor) {
        beanAroundAdd(anno, interceptor, 0);
    }

    /**
     * 获取环绕处理
     * */
    public <T extends Annotation> InterceptorEntity beanAroundGet(Class<T> anno){
        return beanInterceptors.get(anno);
    }


    //////////////////////////
    //
    // bean 对内通知体系
    //
    /////////////////////////
    /**
     * bean订阅
     */
    public void beanSubscribe(Object nameOrType, Consumer<BeanWrap> callback) {
        if (nameOrType != null) {
            beanSubscribers.add(new SubscriberEntity(nameOrType, callback));
        }
    }

    /**
     * bean通知
     */
    public void beanNotice(Object nameOrType, BeanWrap wrap) {
        if (wrap.raw() == null) {
            return;
        }

        //避免在forEach时，对它进行add
        new ArrayList<>(beanSubscribers).forEach(s1 -> {
            if (s1.key.equals(nameOrType)) {
                s1.callback.accept(wrap);
            }
        });
    }


    //public abstract BeanWrap wrap(Class<?> clz, Object raw);


    /**
     * 存入bean库（存入成功会进行通知）
     *
     * @param wrap 如果raw为null，拒绝注册
     */
    public synchronized void putWrap(String name, BeanWrap wrap) {
        if (Utils.isEmpty(name) == false && wrap.raw() != null) {
            if (beans.containsKey(name) == false) {
                beans.put(name, wrap);
                beanNotice(name, wrap);
            }
        }
    }

    /**
     * 存入到bean库（存入成功会进行通知）
     *
     * @param wrap 如果raw为null，拒绝注册
     */
    public synchronized void putWrap(Class<?> type, BeanWrap wrap) {
        if (type != null && wrap.raw() != null) {
            //
            //wrap.raw()==null, 说明它是接口；等它完成代理再注册；以@Db为例，可以看一下
            //
            if (beanWraps.containsKey(type) == false) {
                beanWraps.put(type, wrap);
                beanWrapSet.add(wrap);
                beanNotice(type, wrap);
            }
        }
    }

    public boolean hasWrap(Object nameOrType) {
        return getWrap(nameOrType) != null;
    }

    /**
     * 获取一个bean包装
     *
     * @param nameOrType bean name or type
     */
    public BeanWrap getWrap(Object nameOrType) {
        if (nameOrType instanceof String) {
            return beans.get(nameOrType);
        } else {
            return beanWraps.get(nameOrType);
        }
    }

    public void getWrapAsyn(Object nameOrType, Consumer<BeanWrap> callback) {
        BeanWrap bw = getWrap(nameOrType);

        if (bw == null || bw.raw() == null) {
            beanSubscribe(nameOrType, callback);
        } else {
            callback.accept(bw);
        }
    }

    public <T> T getBean(Object nameOrType) {
        BeanWrap bw = getWrap(nameOrType);
        return bw == null ? null : bw.get();
    }

    public <T> T getBeanOrNew(Class<T> type){
        return wrapAndPut(type).get();
    }

    public <T> void getBeanAsyn(Object nameOrType, Consumer<T> callback) {
        getWrapAsyn(nameOrType, (bw) -> {
            callback.accept(bw.get());
        });
    }

    /**
     * 包装
     */
    public BeanWrap wrap(Class<?> type, Object bean) {
        BeanWrap wrap = getWrap(type);
        if (wrap == null) {
            wrap = wrapCreate(type, bean);
        }

        return wrap;
    }

    /**
     * 包装并推入
     * */
    public BeanWrap wrapAndPut(Class<?> type) {
        return wrapAndPut(type, null);
    }

    /**
     * 包装并推入
     * */
    public BeanWrap wrapAndPut(Class<?> type, Object bean) {
        BeanWrap wrap = getWrap(type);
        if (wrap == null) {
            wrap = wrapCreate(type, bean);
            putWrap(type, wrap);
        }

        return wrap;
    }

    protected abstract BeanWrap wrapCreate(Class<?> type, Object bean);

    //////////////////////////
    //
    // bean 注册与注入
    //
    /////////////////////////

    /**
     * 尝试BEAN注册（按名字和类型存入容器；并进行类型印射）
     */
    public void beanRegister(BeanWrap bw, String name, boolean typed) {
        //按名字注册
        if (Utils.isNotEmpty(name)) {
            //有name的，只用name注入
            //
            putWrap(name, bw);
            if (typed == false) {
                //如果非typed，则直接返回
                return;
            }
        }

        //按类型注册
        putWrap(bw.clz(), bw);
        putWrap(bw.clz().getName(), bw);

        //尝试Bean的基类注册
        beanRegisterSup0(bw);

        //尝试Remoting处理。如果是，则加载到 Solon 路由器
        if (bw.remoting()) {
            HandlerLoader bww = new HandlerLoader(bw);
            if (bww.mapping() != null) {
                //
                //如果没有xmapping，则不进行web注册
                //
                bww.load(Solon.app());
            }
        }
    }

    /**
     * 尝试Bean的基类注册
     * */
    protected void beanRegisterSup0(BeanWrap bw) {
        //如果有父级接口，则建立关系映射
        Class<?>[] list = bw.clz().getInterfaces();
        for (Class<?> c : list) {
            if (c.getName().contains("java.") == false) {
                //建立关系映射
                clzMapping.putIfAbsent(c, bw.clz());
                putWrap(c, bw);
            }
        }

        Type[] list2 = bw.clz().getGenericInterfaces(); //有可能跟 getInterfaces() 一样
        for (Type t : list2) {
            if (t instanceof ParameterizedType) { //有可能不是 ParameterizedType
                putWrap(t.getTypeName(), bw);
            }
        }
    }


    /**
     * 尝试变量注入 字段或参数
     *
     * @param varH 变量包装器
     * @param name 名字（bean name || config ${name}）
     */
    public void beanInject(VarHolder varH, String name) {
        beanInject(varH, name, false);
    }

    protected void beanInject(VarHolder varH, String name, boolean autoRefreshed) {
        if (Utils.isEmpty(name)) {
            //
            // @Inject //使用 type, 注入BEAN
            //
            if(AopContext.class.isAssignableFrom(varH.getType())){
                varH.setValue(this);
                return;
            }

            if(varH.getGenericType() != null){
                getWrapAsyn(varH.getGenericType().getTypeName(), (bw) -> {
                    varH.setValue(bw.get());
                });
            }else{
                getWrapAsyn(varH.getType(), (bw) -> {
                    varH.setValue(bw.get());
                });
            }
        } else if (name.startsWith("${classpath:")) {
            //
            // @Inject("${classpath:user.yml}") //注入配置文件
            //
            String url = name.substring(12, name.length() - 1);
            Properties val = Utils.loadProperties(Utils.getResource(getClassLoader(),url));

            if (val == null) {
                throw new IllegalStateException(name + "  failed to load!");
            }

            if (Properties.class == varH.getType()) {
                varH.setValue(val);
            } else if (Map.class == varH.getType()) {
                Map<String, String> val2 = new HashMap<>();
                val.forEach((k, v) -> {
                    if (k instanceof String && v instanceof String) {
                        val2.put((String) k, (String) v);
                    }
                });
                varH.setValue(val2);
            } else {
                Object val2 = PropsConverter.global().convert(val, null, varH.getType(), varH.getGenericType());
                varH.setValue(val2);
            }
        } else if (name.startsWith("${")) {
            //
            // @Inject("${xxx}") //注入配置 ${xxx} or ${xxx:def},只适合单值
            //
            String name2 = name.substring(2, name.length() - 1).trim();

            beanInjectConfig(varH, name2);

            if (autoRefreshed && varH.isField()) {
                getProps().onChange((key, val) -> {
                    if(key.startsWith(name2)){
                        beanInjectConfig(varH, name2);
                    }
                });
            }
        } else {
            //
            // @Inject("xxx") //使用 name, 注入BEAN
            //
            getWrapAsyn(name, (bw) -> {
                if(BeanWrap.class.isAssignableFrom(varH.getType())){
                    varH.setValue(bw);
                }else{
                    varH.setValue(bw.get());
                }
            });
        }
    }

    protected void beanInjectProperties(Class<?> clz, Object obj){
        Inject typeInj = clz.getAnnotation(Inject.class);

        if (typeInj != null && Utils.isNotEmpty(typeInj.value())) {
            if (typeInj.value().startsWith("${")) {
                Utils.injectProperties(obj, getProps().getPropByExpr(typeInj.value()));
            }
        }
    }

    private void beanInjectConfig(VarHolder varH, String name){
        if (Properties.class == varH.getType()) {
            //如果是 Properties
            Properties val = getProps().getProp(name);
            varH.setValue(val);
        } else {
            //2.然后尝试获取配置
            String def = null;
            int defIdx = name.indexOf(":");
            if(defIdx > 0) {
                if (name.length() > defIdx + 1) {
                    def = name.substring(defIdx + 1).trim();
                } else {
                    def = "";
                }
                name = name.substring(0, defIdx).trim();
            }

            String val = getProps().get(name);

            if(def != null) {
                if (Utils.isEmpty(val)) {
                    val = def;
                }
            }

            if (val == null) {
                Class<?> pt = varH.getType();

                if (pt.getName().startsWith("java.lang.") || pt.isPrimitive()) {
                    //如果是java基础类型，则不注入配置值
                } else {
                    //尝试转为实体
                    Properties val0 = getProps().getProp(name);
                    if (val0.size() > 0) {
                        //如果找到配置了
                        Object val2 = PropsConverter.global().convert(val0, null, pt, varH.getGenericType());
                        varH.setValue(val2);
                    }
                }
            } else {
                Object val2 = ConvertUtil.to(varH.getType(), val);
                varH.setValue(val2);
            }
        }
    }

    //////////////////////////
    //
    // bean 遍历与查找
    //
    /////////////////////////

    /**
     * 遍历bean库 (拿到的是bean包装)
     */
    @Note("遍历bean库 (拿到的是bean包装)")
    public void beanForeach(BiConsumer<String, BeanWrap> action) {
        beans.forEach(action);
    }

    /**
     * 遍历bean包装库
     */
    @Note("遍历bean包装库")
    public void beanForeach(Consumer<BeanWrap> action) {
        //相关于 beanWraps ，不会出现重复的 // 复制一下，避免 ConcurrentModificationException
        new ArrayList<>(beanWrapSet).forEach(bw -> {
            action.accept(bw);
        });
    }

    /**
     * 查找Bean
     */
    @Note("查找bean库 (拿到的是bean包装)")
    public List<BeanWrap> beanFind(BiPredicate<String, BeanWrap> condition) {
        List<BeanWrap> list = new ArrayList<>();
        beanForeach((k, v) -> {
            if (condition.test(k, v)) {
                list.add(v);
            }
        });

        return list;
    }

    /**
     * 查找Bean
     */
    @Note("查找bean包装库")
    public List<BeanWrap> beanFind(Predicate<BeanWrap> condition) {
        List<BeanWrap> list = new ArrayList<>();
        beanForeach((v) -> {
            if (condition.test(v)) {
                list.add(v);
            }
        });

        return list;
    }

    /**
     * Bean 订阅者
     */
    class SubscriberEntity {
        public Object key; //第2优先
        public Consumer<BeanWrap> callback;

        public SubscriberEntity(Object key, Consumer<BeanWrap> callback) {
            this.key = key;
            this.callback = callback;
        }
    }
}
