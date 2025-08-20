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

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Around;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.InterceptorEntity;
import org.noear.solon.core.exception.InjectionException;
import org.noear.solon.core.runtime.AotCollector;
import org.noear.solon.core.util.*;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.*;

/**
 * Bean 容器，提供注册及关系映射管理（不直接使用；作为AppContext的基类）
 *
 * @author noear
 * @since 1.0
 * @since 3.0
 * */
public abstract class BeanContainer {
    //应用
    private final SolonApp app;
    //属性
    private final Props props;
    //类加载器（热插拨时，会有独立的类加载器）
    private final ClassLoader classLoader;
    //附件
    private Map<Class<?>, Object> attachs = new ConcurrentHashMap<>();
    //AOT收集器
    private final AotCollector aot = new AotCollector();

    //同步锁
    protected final ReentrantLock SYNC_LOCK = new ReentrantLock();


    public BeanContainer(SolonApp app, ClassLoader classLoader, Props props) {
        this.app = app;
        this.classLoader = classLoader;
        this.props = props;
    }

    public SolonApp app() {
        return app;
    }

    /**
     * 获取属性（写法上，更接近 Solon.cfg()）
     */
    public Props cfg() {
        if (props == null) {
            return Solon.cfg();
        } else {
            return props;
        }
    }

    public AotCollector aot() {
        return aot;
    }


    /**
     * 附件获取
     *
     * @since 2.9
     */
    public <T> T attachGet(Class<T> clz) {
        return (T) attachs.get(clz);
    }

    /**
     * 附件设置
     *
     * @since 2.9
     */
    public <T> void attachSet(Class<T> clz, T val) {
        attachs.put(clz, val);
    }

    /**
     * 附件
     *
     * @since 2.9
     */
    public <T> T attachOf(Class<T> clz, Supplier<T> supplier) {
        return (T) attachs.computeIfAbsent(clz, k -> {
            //获取附件
            T tmp = supplier.get();
            //同时注册到容器
            wrapAndPut(k, tmp);
            return tmp;
        });
    }

    /**
     * 获取类加载器
     */
    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            return AppClassLoader.global();
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
    private final Map<Class<?>, BeanWrap> beanWrapsOfType = new HashMap<>();
    private final Map<String, BeanWrap> beanWrapsOfName = new HashMap<>();
    private final Set<BeanWrap> beanWrapSet = new HashSet<>();


    /**
     * for Aot
     */

    //启动时写入
    /**
     * bean 构建器
     */
    protected final Map<Class<?>, TypeMap<BeanBuilder<?>>> beanBuilders = new HashMap<>();
    /**
     * bean 注入器
     */
    protected final Map<Class<?>, TypeMap<BeanInjector<?>>> beanInjectors = new HashMap<>();
    /**
     * bean 提取器
     */
    protected final Map<Class<?>, BeanExtractor<?>> beanExtractors = new HashMap<>();
    /**
     * bean 拦截器
     */
    protected final Map<Class<?>, InterceptorEntity> beanInterceptors = new HashMap<>();


    /**
     * bean name hash 订阅者
     */
    private final Map<Object, Set<Consumer<BeanWrap>>> beanHashSubscribersOfName = new HashMap<>();
    /**
     * bean type hash 订阅者
     */
    private final Map<Object, Set<Consumer<BeanWrap>>> beanHashSubscribersOfType = new HashMap<>();
    /**
     * bean base type 订阅者
     */
    private final List<RankEntity<Consumer<BeanWrap>>> beanBaseSubscribersOfType = new ArrayList<>();


    public void clear() {
        beanWrapsOfType.clear();
        beanWrapsOfName.clear();
        beanWrapSet.clear();

        attachs.clear();
        aot.clear();


        //能力注册，不能清除
//        beanBuilders.clear();
//        beanInjectors.clear();
//        beanExtractors.clear();
//        beanInterceptors.clear();
        //订阅关系，不能清除
//        beanHashSubscribersOfName.clear();
//        beanHashSubscribersOfType.clear();
//        beanBaseSubscribersOfType.clear();
    }

    /**
     * 容器能力制复到另一个容器
     */
    public void copyTo(BeanContainer container) {
        //构建器
        beanBuilders.forEach((k, v) -> {
            container.beanBuilders.putIfAbsent(k, v);
        });

        //注入器
        beanInjectors.forEach((k, v) -> {
            container.beanInjectors.putIfAbsent(k, v);
        });

        //拦截器
        beanInterceptors.forEach((k, v) -> {
            container.beanInterceptors.putIfAbsent(k, v);
        });

        //提取器
        beanExtractors.forEach((k, v) -> {
            container.beanExtractors.putIfAbsent(k, v);
        });
    }

    //bean builder, injector, extractor


    /**
     * 添加构建处理
     *
     * @param annoClz 注解类型
     * @param builder 构建器
     */
    public <T extends Annotation> void beanBuilderAdd(Class<T> annoClz, BeanBuilder<T> builder) {
        TypeMap<BeanBuilder<?>> tmp = beanBuilders.computeIfAbsent(annoClz, k -> new TypeMap<>());
        tmp.def(builder);
    }

    /**
     * 添加分类构建处理
     *
     * @param annoClz   注解类型
     * @param targetClz 构建目标类型
     * @param builder   构建器
     */
    public <T extends Annotation> void beanBuilderAdd(Class<T> annoClz, Class<?> targetClz, BeanBuilder<T> builder) {
        TypeMap<BeanBuilder<?>> tmp = beanBuilders.computeIfAbsent(annoClz, k -> new TypeMap<>());
        tmp.put(targetClz, builder);
    }

    /**
     * 添加注入处理
     */
    public <T extends Annotation> void beanInjectorAdd(Class<T> annoClz, BeanInjector<T> injector) {
        TypeMap<BeanInjector<?>> tmp = beanInjectors.computeIfAbsent(annoClz, k -> new TypeMap<>());
        tmp.def(injector);
    }

    /**
     * 添加分类注入处理
     *
     * @param annoClz   注解类型
     * @param targetClz 注入目标类型
     * @param injector  注入器
     */
    public <T extends Annotation> void beanInjectorAdd(Class<T> annoClz, Class<?> targetClz, BeanInjector<T> injector) {
        TypeMap<BeanInjector<?>> tmp = beanInjectors.computeIfAbsent(annoClz, k -> new TypeMap<>());
        tmp.put(targetClz, injector);
    }

    /**
     * 添加提取处理
     */
    public <T extends Annotation> void beanExtractorAdd(Class<T> annoClz, BeanExtractor<T> extractor) {
        beanExtractors.put(annoClz, extractor);
    }

    /**
     * 是否有提取处理
     */
    public boolean beanExtractorHas(Class<? extends Annotation> annoClz) {
        return beanExtractors.containsKey(annoClz);
    }

    /**
     * 添加拦截处理
     *
     * @param index 执行顺序
     */
    public <T extends Annotation> void beanInterceptorAdd(Class<T> annoClz, Interceptor interceptor, int index) {
        beanInterceptors.put(annoClz, new InterceptorEntity(index, interceptor));
    }

    /**
     * 添加拦截处理
     */
    public <T extends Annotation> void beanInterceptorAdd(Class<T> annoClz, Interceptor interceptor) {
        beanInterceptorAdd(annoClz, interceptor, 0);
    }

    /**
     * 获取拦截处理
     */
    public <T extends Annotation> InterceptorEntity beanInterceptorGet(Class<T> annoClz) {
        return beanInterceptors.get(annoClz);
    }

    /**
     * 是否有拦截处理
     */
    public boolean beanInterceptorHas(AnnotatedElement ae) {
        for (Annotation a : ae.getAnnotations()) {
            if (beanInterceptorHas(a)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否有拦截处理
     */
    public boolean beanInterceptorHas(Annotation a) {
        return beanInterceptors.containsKey(a.annotationType())
                || a.annotationType().isAnnotationPresent(Around.class)
                || a.annotationType().equals(Around.class);
    }


    //////////////////////////
    //
    // bean 对内通知体系
    //

    /// //////////////////////

    private Map<Object, Set<Consumer<BeanWrap>>> getBeanHashSubscribers(Object nameOrType) {
        if (nameOrType instanceof String) {
            return beanHashSubscribersOfName;
        } else {
            return beanHashSubscribersOfType;
        }
    }

    /**
     * bean hash 订阅
     */
    protected void beanHashSubscribe(Object nameOrType, Consumer<BeanWrap> callback) {
        if (nameOrType != null) {
            SYNC_LOCK.lock();

            try {
                Set<Consumer<BeanWrap>> tmp = getBeanHashSubscribers(nameOrType)
                        .computeIfAbsent(nameOrType, k -> new LinkedHashSet<>());
                tmp.add(callback);
            } finally {
                SYNC_LOCK.unlock();
            }
        }
    }

    /**
     * bean base type 订阅
     */
    protected void beanBaseSubscribe(Consumer<BeanWrap> callback, int index) {
        SYNC_LOCK.lock();
        try {
            beanBaseSubscribersOfType.add(new RankEntity<>(callback, index));
            if (index < 0) {
                //减少排序
                Collections.sort(beanBaseSubscribersOfType);
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * bean hash 发布（通知外部订阅者）
     */
    protected void beanHashPublish(Object nameOrType, BeanWrap wrap) {
        if (wrap.raw() == null) {
            return;
        }

        SYNC_LOCK.lock();
        try {
            //避免在forEach时，对它进行add
            Set<Consumer<BeanWrap>> tmp = getBeanHashSubscribers(nameOrType).get(nameOrType);
            if (tmp != null) {
                for (Consumer<BeanWrap> s1 : tmp) {
                    s1.accept(wrap);
                }
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * bean base type 发布（通知外部订阅者）
     *
     * @since 3.0
     */
    protected void beanBasePublish(BeanWrap wrap) {
        //避免在forEach时，对它进行add
        SYNC_LOCK.lock();
        try {
            for (RankEntity<Consumer<BeanWrap>> s1 : beanBaseSubscribersOfType) {
                s1.target.accept(wrap);
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * bean 发布，触发基类订阅 （自动支持 @Managed, @Bean, @Component 的 wrap）
     *
     * @since 3.0
     */
    public void beanPublish(BeanWrap wrap) {
        beanBasePublish(wrap);
    }

    /**
     * wrap 发布，触发基类订阅 （自动支持 @Managed, @Bean, @Component 的 wrap）
     *
     * @deprecated 3.0 {@link #beanPublish(BeanWrap)}
     */
    @Deprecated
    public void wrapPublish(BeanWrap wrap) {
        beanBasePublish(wrap);
    }


    //public abstract BeanWrap wrap(Class<?> clz, Object raw);

    /**
     * 删除 bean 包装
     */
    public void removeWrap(String name) {
        if (Utils.isNotEmpty(name)) {
            SYNC_LOCK.lock();
            try {
                BeanWrap bw = beanWrapsOfName.remove(name);
                if (bw != null) {
                    beanWrapSet.remove(bw);
                }
            } finally {
                SYNC_LOCK.unlock();
            }
        }
    }

    /**
     * 删除 bean 包装
     */
    public void removeWrap(Class<?> type) {
        if (type != null) {
            SYNC_LOCK.lock();
            try {
                BeanWrap bw = beanWrapsOfType.remove(type);
                if (bw != null) {
                    beanWrapSet.remove(bw);
                }
            } finally {
                SYNC_LOCK.unlock();
            }
        }
    }

    /**
     * 存入bean库（存入成功会进行通知）
     *
     * @param wrap 如果raw为null，拒绝注册
     */
    public void putWrap(String name, BeanWrap wrap) {
        if (Utils.isNotEmpty(name) && wrap.raw() != null) {
            SYNC_LOCK.lock();
            try {
                if (beanWrapsOfName.containsKey(name) == false) {
                    beanWrapsOfName.put(name, wrap);
                    beanWrapSet.add(wrap);

                    beanHashPublish(name, wrap);
                }
            } finally {
                SYNC_LOCK.unlock();
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
            SYNC_LOCK.lock();
            try {
                if (beanWrapsOfType.containsKey(type) == false) {
                    beanWrapsOfType.put(type, wrap);
                    beanWrapSet.add(wrap);

                    beanHashPublish(type, wrap);
                }
            } finally {
                SYNC_LOCK.unlock();
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
            return beanWrapsOfName.get(nameOrType);
        } else {
            return beanWrapsOfType.get(nameOrType);
        }
    }

    /**
     * 获取一个bean包装列表
     *
     * @param baseType 基类
     */
    public List<BeanWrap> getWrapsOfType(Class<?> baseType) {
        return beanFind(bw -> baseType.isAssignableFrom(bw.rawClz()));
    }

    /**
     * 异步获取一个bean包装
     *
     * @param nameOrType bean name or type
     */
    public void getWrapAsync(Object nameOrType, Consumer<BeanWrap> callback) {
        BeanWrap bw = getWrap(nameOrType);

        if (bw == null || bw.raw() == null) {
            beanHashSubscribe(nameOrType, callback);
        } else {
            callback.accept(bw);
        }
    }

    /**
     * 订阅某类型的 bean 包装
     *
     * @param baseType 基类
     */
    public void subWrapsOfType(Class<?> baseType, Consumer<BeanWrap> callback) {
        subWrapsOfType(baseType, callback, 0);
    }

    /**
     * 订阅某类型的 bean 包装
     *
     * @param baseType 基类
     */
    public void subWrapsOfType(Class<?> baseType, Consumer<BeanWrap> callback, int index) {
        subWrapsOfType(baseType, null, callback, index);
    }

    /**
     * 订阅某类型的 bean 包装
     *
     * @param baseType 基类
     */
    public void subWrapsOfType(Class<?> baseType, ParameterizedType genericType, Consumer<BeanWrap> callback) {
        subWrapsOfType(baseType, genericType, callback, 0);
    }

    /**
     * 订阅某类型的 bean 包装
     *
     * @param baseType 基类
     */
    public void subWrapsOfType(Class<?> baseType, ParameterizedType genericType, Consumer<BeanWrap> callback, int index) {
        //获取现有的
        beanForeach(bw -> {
            if (baseType.isAssignableFrom(bw.rawClz()) && bw.isNullOrGenericFrom(genericType)) {
                callback.accept(bw);
            }
        });

        //获取未来的
        beanBaseSubscribe((bw) -> {
            if (baseType.isAssignableFrom(bw.rawClz()) && bw.isNullOrGenericFrom(genericType)) {
                callback.accept(bw);
            }
        }, index);
    }

    /**
     * 获取 Bean
     *
     * @param name 名字
     */
    public <T> T getBean(String name) {
        BeanWrap bw = getWrap(name);
        return bw == null ? null : bw.get();
    }

    /**
     * 获取 Bean
     *
     * @param type 类型
     */
    public <T> T getBean(ParameterizedType type) {
        return getBean(type.getTypeName());
    }

    /**
     * 获取 Bean
     *
     * @param typeReference 类型引用
     * @since 3.3
     */
    public <T> T getBean(TypeReference<T> typeReference) {
        Type type = typeReference.getType();

        if (type instanceof Class) {
            return getBean((Class<T>) type);
        } else if (type instanceof ParameterizedType) {
            return getBean((ParameterizedType) type);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    /**
     * 获取 Bean
     *
     * @param type 类型
     */
    public <T> T getBean(Class<T> type) {
        BeanWrap bw = getWrap(type);
        return bw == null ? null : bw.get();
    }


    /**
     * 获取 Bean 或默认
     *
     * @param nameOrType 名字或类型
     */
    public <T> T getBeanOrDefault(Object nameOrType, T def) {
        BeanWrap bw = getWrap(nameOrType);
        return bw == null ? def : bw.get();
    }

    /**
     * 获取 Bean 或默认
     *
     * @param nameOrType      名字或类型
     * @param defaultSupplier defaultSupplier
     * @param <T>             泛型标记
     */
    public <T> T getBeanOrDefault(Object nameOrType, Supplier<T> defaultSupplier) {
        BeanWrap bw = getWrap(nameOrType);
        return bw == null ? defaultSupplier.get() : bw.get();
    }

    /**
     * 获取某类型的 bean list
     *
     * @param baseType 基类
     */
    public <T> List<T> getBeansOfType(Class<T> baseType) {
        return getBeansOfType(baseType, null);
    }

    /**
     * 获取某类型的 bean list
     *
     * @param baseType    基类
     * @param genericType 泛型
     */
    public <T> List<T> getBeansOfType(Class<T> baseType, ParameterizedType genericType) {
        List<BeanWrap> beanWraps = beanFind(bw -> baseType.isAssignableFrom(bw.rawClz()) && bw.isNullOrGenericFrom(genericType));
        List<T> beans = new ArrayList<>();

        for (BeanWrap bw : beanWraps) {
            beans.add(bw.get());
        }

        return beans;
    }

    /**
     * 获取某类型的 bean list
     *
     * @param typeReference 类型引用
     * @since 3.3
     */
    public <T> List<T> getBeansOfType(TypeReference<T> typeReference) {
        Type type = typeReference.getType();

        if (type instanceof Class) {
            return getBeansOfType((Class<T>) type);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType type1 = (ParameterizedType) type;
            return getBeansOfType((Class<T>) type1.getRawType(), type1);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    /**
     * 获取某类型的 bean map
     *
     * @param baseType 基类
     */
    public <T> Map<String, T> getBeansMapOfType(Class<T> baseType) {
        return getBeansMapOfType(baseType, null);
    }

    /**
     * 获取某类型的 bean map
     *
     * @param baseType    基类
     * @param genericType 泛型
     */
    public <T> Map<String, T> getBeansMapOfType(Class<T> baseType, ParameterizedType genericType) {
        Map<String, T> beanMap = new HashMap<>();

        beanForeach(bw -> {
            if (baseType.isAssignableFrom(bw.rawClz()) && bw.isNullOrGenericFrom(genericType)) {
                if (Utils.isNotEmpty(bw.name())) {
                    beanMap.put(bw.name(), bw.get());
                }
            }
        });

        return beanMap;
    }

    /**
     * 获取某类型的 bean map
     *
     * @param typeReference 类型引用
     * @since 3.3
     */
    public <T> Map<String, T> getBeansMapOfType(TypeReference<T> typeReference) {
        Type type = typeReference.getType();

        if (type instanceof Class) {
            return getBeansMapOfType((Class<T>) type);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType type1 = (ParameterizedType) type;
            return getBeansMapOfType((Class<T>) type1.getRawType(), type1);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    /**
     * 获取 或创建 bean
     *
     * @param type 类型
     */
    public <T> T getBeanOrNew(Class<T> type) {
        return wrapAndPut(type).get();
    }

    /**
     * 异步获取 Bean
     *
     * @param name 名字
     */
    public <T> void getBeanAsync(String name, Consumer<T> callback) {
        getWrapAsync(name, (bw) -> {
            callback.accept(bw.get());
        });
    }

    /**
     * 异步获取 Bean
     *
     * @param type 类型
     */
    public <T> void getBeanAsync(Class<T> type, Consumer<T> callback) {
        getWrapAsync(type, (bw) -> {
            callback.accept(bw.get());
        });
    }

    /**
     * 订阅某类型的 Bean
     *
     * @param baseType 基类
     */
    public <T> void subBeansOfType(Class<T> baseType, Consumer<T> callback) {
        subBeansOfType(baseType, null, callback);
    }

    /**
     * 订阅某类型的 Bean
     *
     * @param baseType 基类
     */
    public <T> void subBeansOfType(Class<T> baseType, ParameterizedType genericType, Consumer<T> callback) {
        subWrapsOfType(baseType, genericType, (bw) -> {
            callback.accept(bw.get());
        });
    }

    /**
     * 包装
     */
    public BeanWrap wrap(Class<?> type) {
        return wrap(type, null);
    }

    /**
     * 包装
     */
    public BeanWrap wrap(Class<?> type, Object bean) {
        return wrap(type, bean, false);
    }

    /**
     * 包装
     */
    public BeanWrap wrap(Class<?> type, Object bean, boolean typed) {
        BeanWrap wrap = getWrap(type);
        if (wrap == null) {
            wrap = wrapCreate(type, bean, null, typed);
        }

        return wrap;
    }

    /**
     * 包装
     */
    public BeanWrap wrap(String name, Object bean) {
        return wrap(name, bean, false);
    }

    /**
     * 包装
     */
    public BeanWrap wrap(String name, Object bean, boolean typed) {
        BeanWrap wrap = getWrap(name);
        if (wrap == null) {
            wrap = wrapCreate(bean.getClass(), bean, name, typed);
        }

        return wrap;
    }

    /**
     * 包装
     */
    public BeanWrap wrap(String name, Class<?> clz) {
        return wrap(name, clz, false);
    }

    /**
     * 包装
     */
    public BeanWrap wrap(String name, Class<?> clz, boolean typed) {
        BeanWrap wrap = getWrap(name);
        if (wrap == null) {
            wrap = wrapCreate(clz, null, name, typed);
        }

        return wrap;
    }

    /**
     * 包装并推入
     */
    public BeanWrap wrapAndPut(Class<?> type) {
        return wrapAndPut(type, null);
    }

    /**
     * 包装并推入
     */
    public BeanWrap wrapAndPut(Class<?> type, Object bean) {
        return wrapAndPut(type, bean, false);
    }

    /**
     * 包装并推入
     */
    public BeanWrap wrapAndPut(Class<?> type, Object bean, boolean typed) {
        BeanWrap wrap = getWrap(type);
        if (wrap == null) {
            wrap = wrapCreate(type, bean, null, typed);
            putWrap(type, wrap);
        }

        return wrap;
    }

    /**
     * 包装并推入
     */
    public BeanWrap wrapAndPut(String name, Object bean) {
        return wrapAndPut(name, bean, false);
    }

    /**
     * 包装并推入
     */
    public BeanWrap wrapAndPut(String name, Object bean, boolean typed) {
        BeanWrap wrap = getWrap(name);
        if (wrap == null) {
            wrap = wrapCreate(bean.getClass(), bean, name, typed);
            putWrap(name, wrap);

            if (typed) {
                putWrap(bean.getClass(), wrap);
            }
        }

        return wrap;
    }

    protected abstract BeanWrap wrapCreate(Class<?> type, Object bean, String name, boolean typed);

    //////////////////////////
    //
    // bean 注册与注入
    //
    /////////////////////////

    /**
     * 尝试BEAN注册（按名字和类型存入容器；并进行类型映射）
     */
    public void beanRegister(BeanWrap bw, String name, boolean typed) {
        //按名字注册
        if (Utils.isNotEmpty(name)) {
            //有name的，只用name注入
            //
            putWrap(name, bw);
            if (typed == false) {
                //尝试绑定泛型（用于支持泛型集合注入）
                beanBindingSupT(bw.rawClz(), bw);
                if (bw.rawClz().equals(bw.clz()) == false) {
                    beanBindingSupT(bw.clz(), bw);
                }

                //如果非typed，则直接返回
                return;
            }
        }

        //按实例类型注册
        putWrap(bw.rawClz(), bw);
        putWrap(bw.rawClz().getName(), bw);
        beanRegisterSupI(bw.rawClz(), bw);

        if (bw.rawClz().equals(bw.clz()) == false) {
            //按返回类型注册
            putWrap(bw.clz(), bw);
            putWrap(bw.clz().getName(), bw);
            beanRegisterSupI(bw.clz(), bw);
        }
    }

    /**
     * 尝试Bean的实现接口类型注册
     */
    protected void beanRegisterSupI(Class<?> clz, BeanWrap bw) {
        //如果有父级接口，则建立关系映射
        Class<?>[] list = clz.getInterfaces();
        for (Class<?> c : list) {
            if (c.getName().startsWith("java.") == false) {
                putWrap(c, bw);
            }
        }

        Type[] list2 = clz.getGenericInterfaces(); //有可能跟 getInterfaces() 一样
        for (Type t : list2) {
            if (t instanceof ParameterizedType) { //有可能不是 ParameterizedType
                putWrap(t.getTypeName(), bw);
                bw.genericList().add((ParameterizedType) t);
            }
        }
    }

    /**
     * 尝试Bean的实现泛型接口类型绑定
     */
    protected void beanBindingSupT(Class<?> clz, BeanWrap bw) {
        Type[] list2 = clz.getGenericInterfaces(); //有可能跟 getInterfaces() 一样
        for (Type t : list2) {
            if (t instanceof ParameterizedType) { //有可能不是 ParameterizedType
                putWrap(t.getTypeName(), bw);
                bw.genericList().add((ParameterizedType) t);
            }
        }
    }


    /**
     * 尝试变量注入 字段或参数
     *
     * @param vh   变量包装器
     * @param name 名字（bean name || config ${name}）
     */
    public void beanInject(VarHolder vh, String name) {
        beanInject(vh, name, false, false);
    }

    protected void beanInject(VarHolder vh, String name, boolean required, boolean autoRefreshed) {
        try {
            vh.required(required);
            beanInjectDo(vh, name, required, autoRefreshed);
        } catch (InjectionException e) {
            throw e;
        } catch (Throwable e) {
            throw new InjectionException("Injection failed: " + vh.getFullName(), e);
        }
    }

    private void beanInjectDo(VarHolder vh, String name, boolean required, boolean autoRefreshed) {
        if (Utils.isEmpty(name)) {
            //
            // @Inject //使用 type, 注入BEAN
            //
            if (vh.getType() == null) { //检查类型问题
                if (required) {
                    throw new InjectionException("Unrecognized type，injection failed: " + vh.getFullName());
                } else {
                    return;
                }
            }

            if (AppContext.class.isAssignableFrom(vh.getType())) {
                vh.setValue(this);
                return;
            }

            if (SolonApp.class.isAssignableFrom(vh.getType())) {
                vh.setValue(app());
                return;
            }

            if (vh.getGenericType() != null) {
                //如果是泛型
                getWrapAsync(vh.getGenericType().getTypeName(), (bw) -> {
                    vh.setValue(bw.get());
                });
            } else {
                getWrapAsync(vh.getType(), (bw) -> {
                    vh.setValue(bw.get());
                });
            }
        } else if (name.startsWith("${classpath:")) {
            //
            // @Inject("${classpath:user.yml}") //注入配置文件
            //
            String url = name.substring(12, name.length() - 1);
            Properties val = Utils.loadProperties(ResourceUtil.getResource(getClassLoader(), url));

            if (val == null) {
                if (required) {
                    throw new IllegalStateException(name + "  failed to load!");
                }
            } else {
                if (Properties.class == vh.getType()) {
                    vh.setValue(val);
                } else if (Map.class == vh.getType()) {
                    Map<String, String> val2 = new HashMap<>();
                    val.forEach((k, v) -> {
                        if (k instanceof String && v instanceof String) {
                            val2.put((String) k, (String) v);
                        }
                    });
                    vh.setValue(val2);
                } else {
                    Object val2 = PropsConverter.global().convert(val, null, vh.getType(), vh.getGenericType());
                    vh.setValue(val2);
                    aot().registerEntityType(vh.getType(), vh.getGenericType());
                }
            }
        } else if (name.startsWith("${")) {
            //
            // @Inject("${xxx}") //注入配置 ${xxx} or ${xxx:def},只适合单值
            //
            String name2 = findConfigKey(name);

            beanInjectConfig(vh, name2, required);

            if (autoRefreshed && vh.isField()) {
                int defIdx = name2.indexOf(":");
                if (defIdx > 0) {
                    name2 = name2.substring(0, defIdx).trim();
                }
                String name3 = name2;

                cfg().onChange((key, val) -> {
                    if (key.startsWith(name3)) {
                        beanInjectConfig(vh, name3, required);
                    }
                });
            }
        } else {
            //
            // @Inject("xxx") //使用 name, 注入BEAN
            //
            getWrapAsync(name, (bw) -> {
                if (BeanWrap.class.isAssignableFrom(vh.getType())) {
                    vh.setValue(bw);
                } else {
                    vh.setValue(bw.get());
                }
            });
        }
    }

    protected void beanFillProperties(Object obj, Inject typeInj) {
        if (typeInj != null && Utils.isNotEmpty(typeInj.value())) {
            String name = typeInj.value();

            if (name.startsWith("${classpath:")) {
                //
                // @Inject("${classpath:user.yml}") //注入配置文件
                //
                String url = name.substring(12, name.length() - 1);
                Properties val = Utils.loadProperties(ResourceUtil.getResource(getClassLoader(), url));

                if (val == null) {
                    if (typeInj.required()) {
                        throw new IllegalStateException(name + "  failed to load!");
                    }
                } else {
                    Utils.injectProperties(obj, val);
                }
            } else if (typeInj.value().startsWith("${")) {
                //
                // @Inject("${xxx}") //注入配置 ${xxx} or ${xxx:def},只适合单值
                //
                String name2 = findConfigKey(name);

                beanFillPropertiesDo(name, obj, cfg().getProp(name2), typeInj.required());

                //支持自动刷新
                if (typeInj.autoRefreshed()) {
                    cfg().onChange((key, val) -> {
                        if (key.startsWith(name2)) {
                            beanFillPropertiesDo(name, obj, cfg().getProp(name2), typeInj.required());
                        }
                    });
                }
            }
        }
    }

    private void beanFillPropertiesDo(String name, Object obj, Properties val, boolean required) {
        if (required && val.size() == 0) {
            throw new InjectionException("Missing required property: '" + name + "', config injection failed: " + obj.getClass().getName());
        }

        Utils.injectProperties(obj, val);
    }

    /**
     * 找到真实的name
     *
     * @param name 原始name，${a:${b:3}}
     * @return 返回真实的name
     */
    protected String findConfigKey(String name) {
        String name2 = name.substring(2, name.length() - 1).trim();
        // 如果定义了默认值且左边没有配置右边是表达式，则找右边
        int index = name2.indexOf(':');
        if (index > 0) {
            String rawName = name2.substring(0, index);
            String nextName = name2.substring(index + 1);
            if (nextName.startsWith("${") && !cfg().containsKey(rawName)) {
                return findConfigKey(nextName);
            }
        }
        return name2;
    }


    private void beanInjectConfig(VarHolder vh, String name, boolean required) {
        if (Properties.class == vh.getType() || Props.class == vh.getType()) {
            //如果是 Properties
            Props val = cfg().getProp(name);

            if (required && val.size() == 0) {
                throw new InjectionException("Missing required property: '" + name + "', config injection failed: " + vh.getFullName());
            }

            vh.setValue(val);
        } else {
            //2.然后尝试获取配置（支持默认值获取）
            String val = cfg().getByExpr(name);

            if (val == null) {
                //尝试从"环镜变量"获取
                val = System.getenv(name);
            }

            if (val == null) {
                Class<?> pt = vh.getType();

                if (pt.getName().startsWith("java.lang.") || pt.isPrimitive()) {
                    //如果是java基础类型，则不注入配置值
                    if (required) {
                        throw new InjectionException("Missing required property: '" + name + "', config injection failed: " + vh.getFullName());
                    }

                    vh.setValue(null); //用于触发事件
                } else {
                    //尝试转为实体
                    Properties val0 = cfg().getProp(name);
                    if (val0.size() > 0) {
                        //如果找到配置了
                        Object val2 = PropsConverter.global().convert(val0, null, pt, vh.getGenericType());
                        vh.setValue(val2);
                        aot().registerEntityType(vh.getType(), vh.getGenericType());
                    } else {
                        if (required) {
                            throw new InjectionException("Missing required property: '" + name + "', config injection failed: " + vh.getFullName());
                        }

                        vh.setValue(null); //用于触发事件
                    }
                }
            } else {
                Object val2 = ConvertUtil.to(vh.getType(), vh.getGenericType(), val);
                vh.setValue(val2);
                aot().registerEntityType(vh.getType(), vh.getGenericType());
            }
        }
    }

    /**
     * 添加生命周期 bean
     */
    public abstract void lifecycle(Lifecycle lifecycle);

    /**
     * 添加生命周期 bean
     *
     * @param index 顺序
     */
    public abstract void lifecycle(int index, Lifecycle lifecycle);

    /**
     * 添加生命周期 bean
     *
     * @param index    顺序
     * @param priority 优先级（此处，相当于二级顺序）
     */
    public abstract void lifecycle(int index, int priority, Lifecycle lifecycle);

    //////////////////////////
    //
    // bean 遍历与查找
    //
    /////////////////////////

    /**
     * 遍历bean库 (拿到的是bean包装)
     */
    public void beanForeach(BiConsumer<String, BeanWrap> action) {
        beanWrapsOfName.forEach(action);
    }


    /**
     * 遍历bean包装库
     */
    public void beanForeach(Consumer<BeanWrap> action) {
        //相关于 beanWraps ，不会出现重复的 // toArray，避免 ConcurrentModificationException
        Object[] array = beanWrapSet.toArray();
        for (Object bw : array) {
            action.accept((BeanWrap) bw);
        }
    }

    /**
     * 查找bean包装
     */
    public List<BeanWrap> beanFind(BiPredicate<String, BeanWrap> condition) {
        List<BeanWrap> list = new ArrayList<>();
        beanForeach((k, v) -> {
            if (condition.test(k, v)) {
                list.add(v);
            }
        });

        //支持 @Managed(index), @Bean(index), @Component(index) 排序
        if (list.size() > 0) {
            list.sort(Comparator.comparingInt(bw -> bw.index()));
        }

        return list;
    }

    /**
     * 查找bean包装
     */
    public List<BeanWrap> beanFind(Predicate<BeanWrap> condition) {
        List<BeanWrap> list = new ArrayList<>();
        beanForeach((v) -> {
            if (condition.test(v)) {
                list.add(v);
            }
        });

        //支持 @Managed(index), @Bean(index), @Component(index) 排序
        if (list.size() > 0) {
            list.sort(Comparator.comparingInt(bw -> bw.index()));
        }

        return list;
    }

    /**
     * bean 停止（if Closeable）
     */
    protected void beanStop0() {
        for (BeanWrap bw : beanWrapSet) {
            if (bw.raw() instanceof Closeable) {
                try {
                    ((Closeable) bw.raw()).close();
                } catch (Throwable e) {
                    //e.printStackTrace();
                }
            }
        }
    }
}