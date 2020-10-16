package org.noear.solon.core;


import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XNote;
import org.noear.solon.ext.ConvertUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Bean 容器，提供注册及关系映射管理（不直接使用；作为AopContext的基类）
 *
 * @author noear
 * @since 1.0
 * */
public class BeanContainer {
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
     * 添加 bean builder, injector
     */
    public <T extends Annotation> void beanBuilderAdd(Class<T> anno, BeanBuilder<T> creater) {
        beanBuilders.put(anno, creater);
    }

    public <T extends Annotation> void beanInjectorAdd(Class<T> anno, BeanInjector<T> injector) {
        beanInjectors.put(anno, injector);
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
    public void beanSubscribe(Object key, Consumer<BeanWrap> callback) {
        if (key != null) {
            beanSubscribers.add(new SubscriberEntity(key, callback));
        }
    }

    /**
     * bean通知
     */
    public void beanNotice(Object key, BeanWrap wrap) {
        if (wrap.raw() == null) {
            return;
        }

        //避免在forEach时，对它进行add
        new ArrayList<>(beanSubscribers).forEach(s1 -> {
            if (s1.key.equals(key)) {
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
    public void putWrap(String key, BeanWrap wrap) {
        if (XUtil.isEmpty(key) == false && wrap.raw() != null) {
            if (beans.containsKey(key) == false) {
                beans.put(key, wrap);
                beanNotice(key, wrap);
            }
        }
    }

    /**
     * 存入到bean库（存入成功会进行通知）
     *
     * @param wrap 如果raw为null，拒绝注册
     */
    public void putWrap(Class<?> key, BeanWrap wrap) {
        if (key != null && wrap.raw() != null) {
            //
            //wrap.raw()==null, 说明它是接口；等它完成代理再注册；以@Db为例，可以看一下
            //
            if (beanWraps.containsKey(key) == false) {
                beanWraps.put(key, wrap);
                beanNotice(key, wrap);
            }
        }
    }

    /**
     * 获取一个bean包装
     *
     * @param key name or type
     */
    public BeanWrap getWrap(Object key) {
        if (key instanceof String) {
            return beans.get(key);
        } else {
            return beanWraps.get(key);
        }
    }

    public void getWrapAsyn(Object key, Consumer<BeanWrap> callback) {
        BeanWrap bw = getWrap(key);

        if (bw == null || bw.raw() == null) {
            beanSubscribe(key, callback);
        } else {
            callback.accept(bw);
        }
    }

    public <T> T getBean(Object key) {
        BeanWrap bw = getWrap(key);
        return bw == null ? null : bw.get();
    }

    /**
     * 包装
     */
    public BeanWrap wrap(Class<?> clz, Object bean) {
        BeanWrap wrap = getWrap(clz);
        if (wrap == null) {
            wrap = new BeanWrap(clz, bean);
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
        if (XUtil.isNotEmpty(name)) {
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
        if (XUtil.isEmpty(name)) {
            //如果没有name,使用类型进行获取 bean
            getWrapAsyn(varH.getType(), (bw) -> {
                varH.setValue(bw.get());
            });
        } else if (name.startsWith("${classpath:")) {
            //
            //demo:${classpath:user.yml}
            //
            String url = name.substring(12, name.length() - 1);
            Properties val = XUtil.loadProperties(XUtil.getResource(url));

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
                Object val2 = ClassWrap.get(varH.getType()).newBy(val::getProperty);
                varH.setValue(val2);
            }

        } else if (name.startsWith("${")) {
            //配置 ${xxx}
            name = name.substring(2, name.length() - 1);

            if (Properties.class == varH.getType()) {
                //如果是 Properties
                Properties val = XApp.cfg().getProp(name);
                varH.setValue(val);
            } else if (Map.class == varH.getType()) {
                //如果是 Map
                Map val = XApp.cfg().getXmap(name);
                varH.setValue(val);
            } else {
                //2.然后尝试获取配置
                String val = XApp.cfg().get(name);
                if (val == null) {
                    Class<?> pt = varH.getType();

                    if (pt.getName().startsWith("java.") || pt.isArray() || pt.isPrimitive()) {
                        //如果是java基础类型，则为null（后面统一地 isPrimitive 做处理）
                        //
                        varH.setValue(null); //暂时不支持数组注入
                    } else {
                        //尝试转为实体
                        Properties val0 = XApp.cfg().getProp(name);
                        Object val2 = ClassWrap.get(pt).newBy(val0::getProperty);
                        varH.setValue(val2);
                    }
                } else {
                    Object val2 = ConvertUtil.to(varH.getType(), val);
                    varH.setValue(val2);
                }
            }
        } else {
            //使用name, 获取BEAN
            getWrapAsyn(name, (bw) -> {
                varH.setValue(bw.get());
            });
        }
    }

    //////////////////////////
    //
    // bean 遍历
    //
    /////////////////////////

    /**
     * 遍历bean库 (拿到的是bean包装)
     */
    @XNote("遍历bean库 (拿到的是bean包装)")
    public void beanForeach(BiConsumer<String, BeanWrap> action) {
        beans.forEach(action);
    }

    /**
     * 遍历bean包装库
     */
    @XNote("遍历bean包装库")
    public void beanForeach(Consumer<BeanWrap> action) {
        beanWraps.forEach((k, bw) -> {
            action.accept(bw);
        });
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
