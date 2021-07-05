package org.noear.solon.core;


import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.InterceptorEntity;
import org.noear.solon.core.handle.HandlerLoader;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.util.ConvertUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    //////////////////////////
    //
    // 容器存储
    //
    /////////////////////////
    /**
     * bean包装库
     */
    protected final Map<Class<?>, BeanWrap> beanWraps = new ConcurrentHashMap<>();
    /**
     * bean库
     */
    protected final Map<String, BeanWrap> beans = new ConcurrentHashMap<>();
    /**
     * clz mapping
     */
    protected final Map<Class<?>, Class<?>> clzMapping = new ConcurrentHashMap<>();

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
     * 容器制复能力
     * */
    public void copy(BeanContainer container){
        container.beanBuilders.forEach((k,v)->{
            beanBuilders.putIfAbsent(k,v);
        });

        //用于跨容器复制
        container.beanInjectors.forEach((k,v)->{
            beanInjectors.putIfAbsent(k,v);
        });

        //用于跨容器复制
        container.beanInterceptors.forEach((k, v) -> {
            beanInterceptors.putIfAbsent(k, v);
        });

        container.beanExtractors.forEach((k, v) -> {
            beanExtractors.putIfAbsent(k, v);
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
     * bean订阅者
     */
    protected final Set<SubscriberEntity> beanSubscribers = new LinkedHashSet<>();

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
    public void putWrap(String name, BeanWrap wrap) {
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
    public void putWrap(Class<?> type, BeanWrap wrap) {
        if (type != null && wrap.raw() != null) {
            //
            //wrap.raw()==null, 说明它是接口；等它完成代理再注册；以@Db为例，可以看一下
            //
            if (beanWraps.containsKey(type) == false) {
                beanWraps.put(type, wrap);
                beanNotice(type, wrap);
            }
        }
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

    /**
     * 包装
     */
    public BeanWrap wrap(Class<?> type, Object bean) {
        BeanWrap wrap = getWrap(type);
        if (wrap == null) {
            wrap = new BeanWrap(type, bean);
        }

        return wrap;
    }

    //////////////////////////
    //
    // bean 注册与注入
    //
    /////////////////////////

    /**
     * 尝试BEAN注册（按名字和类型存入容器；并进行类型印射）
     */
    public void beanRegister(BeanWrap bw, String name, boolean typed) {
        beanRegister0(bw, name, typed);

        //如果是remoting状态，同时加载到 Solon 路由器
        if (bw.remoting()) {
            HandlerLoader bww = new HandlerLoader(bw);
            if (bww.mapping() != null) {
                //
                //如果没有xmapping，则不进行web注册
                //
                bww.load(Solon.global());
            }
        }
    }

    private void beanRegister0(BeanWrap bw, String name, boolean typed) {
        if (Utils.isNotEmpty(name)) {
            //有name的，只用name注入
            //
            putWrap(name, bw);
            if (typed == false) {
                //如果非typed，则直接返回
                return;
            }
        }

        putWrap(bw.clz(), bw);
        putWrap(bw.clz().getName(), bw);

        //如果有父级接口，则建立关系映射
        Class<?>[] list = bw.clz().getInterfaces();
        for (Class<?> c : list) {
            if (c.getName().contains("java.") == false) {
                //建立关系映射
                clzMapping.putIfAbsent(c, bw.clz());
                putWrap(c, bw);
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
            //如果没有name,使用类型进行获取 bean
            getWrapAsyn(varH.getType(), (bw) -> {
                varH.setValue(bw.get());
            });
        } else if (name.startsWith("${classpath:")) {
            //
            //demo:${classpath:user.yml}
            //
            String url = name.substring(12, name.length() - 1);
            Properties val = Utils.loadProperties(Utils.getResource(url));

            if (val == null) {
                throw new RuntimeException(name + "  failed to load!");
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
                Object val2 = ClassWrap.get(varH.getType()).newBy(val);
                varH.setValue(val2);
            }

        } else if (name.startsWith("${")) {
            //配置 ${xxx} or ${xxx:def},只适合单值
            String name2 = name.substring(2, name.length() - 1).trim();

            beanInjectConfig(varH, name2);

            if (autoRefreshed && varH.isField()) {
                Solon.cfg().onChange((key, val) -> {
                    if(key.startsWith(name2)){
                        beanInjectConfig(varH, name2);
                    }
                });
            }
        } else {
            //使用name, 获取BEAN
            getWrapAsyn(name, (bw) -> {
                varH.setValue(bw.get());
            });
        }
    }

    private void beanInjectConfig(VarHolder varH, String name){
        if (Properties.class == varH.getType()) {
            //如果是 Properties
            Properties val = Solon.cfg().getProp(name);
            varH.setValue(val);
        } else if (Map.class == varH.getType()) {
            //如果是 Map
            Map val = Solon.cfg().getXmap(name);
            varH.setValue(val);
        } else {
            //2.然后尝试获取配置
            String def = null;
            if(name.contains(":")) {
                def = name.split(":")[1].trim();
                name = name.split(":")[0].trim();
            }

            String val = Solon.cfg().get(name);

            if(val == null){
                val = def;
            }

            if (val == null) {
                Class<?> pt = varH.getType();

                if (pt.getName().startsWith("java.") || pt.isArray() || pt.isPrimitive()) {
                    //如果是java基础类型，则不注入配置值
                } else {
                    //尝试转为实体
                    Properties val0 = Solon.cfg().getProp(name);
                    if (val0.size() > 0) {
                        //如果找到配置了
                        Object val2 = ClassWrap.get(pt).newBy(val0);
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
        beanWraps.forEach((k, bw) -> {
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
