package org.noear.solon.core;


import org.noear.solon.XUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 为 AopFactory 提供存储 支持，并提供注册管理
 *
 *
 * */
public abstract class AopFactoryBase {
    //////////////////////////
    //
    // 基础存储
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
     * bean loaders
     */
    protected final Map<Class<?>, BeanCreator<?>> beanCreators = new HashMap<>();
    /**
     * bean injectors
     */
    protected final Map<Class<?>, BeanInjector<?>> beanInjectors = new HashMap<>();


    /**
     * 添加 bean creator, injector
     */
    public <T extends Annotation> void beanCreatorAdd(Class<T> anno, BeanCreator<T> creater) {
        beanCreators.put(anno, creater);
    }

    public <T extends Annotation> void beanInjectorAdd(Class<T> anno, BeanInjector<T> injector) {
        beanInjectors.put(anno, injector);
    }

    //////////////////////////
    //
    // bean 对外事件存储
    //
    /////////////////////////

    /**
     * bean 加载完成事件
     */
    protected final Set<Runnable> loadedEvent = new LinkedHashSet<>();

    //////////////////////////
    //
    // bean 对内通知体系
    //
    /////////////////////////

    /**
     * bean订阅者
     */
    protected final Set<BeanSubscriber> subSet = new LinkedHashSet<>();

    /**
     * bean订阅
     */
    public void beanSubscribe(Object key, Consumer<BeanWrap> callback) {
        if (key != null) {
            subSet.add(new BeanSubscriber(key, callback));
        }
    }

    /**
     * bean通知
     */
    public void beanNotice(Object key, BeanWrap wrap) {
        if (wrap.raw() == null) {
            return;
        }

        subSet.forEach(s1 -> {
            if (s1.key.equals(key)) {
                s1.callback.accept(wrap);
            }
        });
    }

    //public abstract BeanWrap wrap(Class<?> clz, Object raw);


    /**
     * 注册到bean库（注册成功会进行通知）
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
     * 注册到bean库（注册成功会进行通知）
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
     */
    public BeanWrap getWrap(Object key) {
        if (key instanceof String) {
            return beans.get(key);
        }

        if (key instanceof Class<?>) {
            return beanWraps.get(key);
        }

        return null;
    }


    /**
     * 尝试生成bean
     */
    protected void tryBeanCreate(Class<?> clz) {
        Annotation[] annS = clz.getDeclaredAnnotations();

        if (annS.length > 0) {
            try {
                for (Annotation a : annS) {
                    BeanCreator bc = beanCreators.get(a.annotationType());
                    if (bc != null) {
                        tryCreateBeanByAnno(clz, a, bc);
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 尝试为bean注入
     */
    protected void tryBeanInject(VarHolder varH, Annotation[] annS) {
        for (Annotation a : annS) {
            BeanInjector bi = beanInjectors.get(a.annotationType());
            if (bi != null) {
                bi.handler(varH, a);
            }
        }
    }

    /**
     * 尝试加载一个注解
     */
    protected <T extends Annotation> void tryCreateBeanByAnno(Class<?> clz, T anno, BeanCreator<T> loader) {
        try {
            //包装
            BeanWrap wrap = Aop.wrap(clz, null);
            loader.handler(clz, wrap, anno);
            //尝试入库
            Aop.factory().putWrap(clz,wrap);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
