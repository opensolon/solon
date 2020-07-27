package org.noear.solon.core;

import org.noear.solon.ext.Act1;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

/** 为 AopFactory 提供存储 支持 */
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
    private final Set<BeanSubscriber> subscribers = new ConcurrentSkipListSet<>();

    /**
     * bean订阅
     */
    public void beanSubscribe(Object key, Consumer<BeanWrap> callback) {
        subscribers.add(new BeanSubscriber(key, callback));
    }

    /**
     * bean订阅
     */
    public void beanSubscribe(Consumer<BeanWrap> callback) {
        subscribers.add(new BeanSubscriber(callback));
    }

    /**
     * bean通知
     */
    public void beanNotice(Object key, BeanWrap wrap) {
        subscribers.forEach(s1 -> {
            if (s1.key != null) {
                if (s1.key.equals(key)) {
                    s1.callback.accept(wrap);
                }
            } else if (s1.tag != null) {
                if (s1.tag.equals(wrap.tag())) {
                    s1.callback.accept(wrap);
                }
            } else {
                s1.callback.accept(wrap);
            }
        });
    }

    public abstract BeanWrap wrap(Class<?> clz, Object raw);


    /**
     * 尝试为bean注入
     */
    protected void tryBeanInject(FieldWrapTmp fwT, Annotation[] annS) {
        for (Annotation a : annS) {
            BeanInjector bi = beanInjectors.get(a.annotationType());
            if (bi != null) {
                bi.handler(fwT, a);
            }
        }
    }

    /**
     * 尝试生成bean
     */
    protected void tryBeanCreate(Class<?> clz, Annotation[] annS) {
        for (Annotation a : annS) {
            BeanCreator bc = beanCreators.get(a.annotationType());
            if (bc != null) {
                tryCreateBeanByAnno(clz, a, bc);
            }
        }
    }

    /**
     * 尝试加载一个注解
     */
    protected <T extends Annotation> void tryCreateBeanByAnno(Class<?> clz, T anno, BeanCreator<T> loader) {
        try {
            BeanWrap wrap = wrap(clz, null);
            loader.handler(clz, wrap, anno);
            beanNotice(clz, wrap);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
