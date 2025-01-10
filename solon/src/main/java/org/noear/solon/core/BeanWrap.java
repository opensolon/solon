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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.noear.solon.annotation.Singleton;
import org.noear.solon.core.exception.ConstructionException;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;

/**
 * Bean 包装
 *
 * Bean 构建过程：Constructor(构造方法) -> @Inject(依赖注入) -> @Init(初始化，相当于 LifecycleBean)
 *
 * @author noear
 * @since 1.0
 * */
@SuppressWarnings("unchecked")
public class BeanWrap {
    // bean clz
    private Class<?> clz;
    // bean lifecycle
    private BeanWrapLifecycle lifecycle;
    // bean raw（初始实例）
    private Constructor rawCtor;
    private Object[] rawCtorArgs;
    private Object raw;
    private Object rawUnproxied;
    private Class<?> rawClz;

    // 是否为单例
    private boolean singleton;
    // 是否为远程服务
    private boolean remoting;
    // bean name
    private String name;
    // bean index
    private int index;
    // bean tag
    private String tag;
    // bean 是否按注册类型
    private boolean typed;
    // bean 代理（为ASM代理提供接口支持）
    private BeanWrap.Proxy proxy;
    // bean clz 的注解（算是缓存起来）
    private final Annotation[] annotations;

    private final AppContext context;
    private Set<String> genericList;

    protected Set<String> genericList() {
        if (genericList == null) {
            genericList = new HashSet<>();
        }

        return genericList;
    }


    public BeanWrap(AppContext context, Class<?> clz) {
        this(context, clz, null);
    }

    public BeanWrap(AppContext context, Class<?> clz, Object raw) {
        this(context, clz, raw, null);
    }

    /**
     * @since 1.10
     */
    public BeanWrap(AppContext context, Class<?> clz, Object raw, String name) {
        this(context, clz, raw, name, false, null, null);
    }

    /**
     * @since 2.9
     */
    public BeanWrap(AppContext context, Class<?> clz, Object raw, String name, boolean typed) {
        this(context, clz, raw, name, typed, null, null);
    }

    public BeanWrap(AppContext context, Class<?> clz, Object raw, String name, boolean typed, String initMethodName, String destroyMethodName) {
        this(context, clz, raw, name, typed, initMethodName, destroyMethodName, null, null);
    }

    public BeanWrap(AppContext context, Class<?> clz, Constructor rawCtor, Object[] rawCtorArgs) {
        this(context, clz, null, null, false, null, null, rawCtor, rawCtorArgs);
    }

    /**
     * @since 1.10
     */
    public BeanWrap(AppContext context, Class<?> clz, Object raw, String name, boolean typed, String initMethodName, String destroyMethodName, Constructor rawCtor, Object[] rawCtorArgs) {
        this.context = context;
        this.clz = clz;
        this.name = name;
        this.typed = typed;

        this.rawCtor = rawCtor;
        this.rawCtorArgs = rawCtorArgs;

        //不否为单例
        Singleton anoS = clz.getAnnotation(Singleton.class);
        singleton = (anoS == null || anoS.value()); //默认为单例

        annotations = clz.getAnnotations();

        //构建原生实例
        if (raw == null) {
            this.rawUnproxied = _new();
            this.raw = rawUnproxied;
        } else {
            this.rawUnproxied = raw;
            this.raw = rawUnproxied;
        }

        if (rawUnproxied != null) {
            rawClz = rawUnproxied.getClass();
        }

        //尝试初始化
        tryInit(initMethodName, destroyMethodName);
    }

    public AppContext context() {
        return context;
    }

    private boolean isDoned;

    /**
     * 包装已完成的（完成后，不能再修改元信息）
     */
    public boolean isDoned() {
        return isDoned;
    }

    /**
     * 完成（完成后，不能再修改元信息）
     */
    public void done() {
        isDoned = true;
    }

    /**
     * 获取代理
     */
    public Proxy proxy() {
        return proxy;
    }

    /**
     * 设置代理
     */
    public void proxySet(BeanWrap.Proxy proxy) {
        this.proxy = proxy;

        if (raw != null && raw == rawUnproxied) {
            //如果_raw存在，则进行代理转换 //如果相等，说明未代理（否则已代理）
            raw = proxy.getProxy(this, raw);
        }
    }

    /**
     * 是否为单例
     */
    public boolean singleton() {
        return singleton;
    }

    public void singletonSet(boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * is remoting()?
     */
    public boolean remoting() {
        return remoting;
    }

    public void remotingSet(boolean remoting) {
        this.remoting = remoting;
    }

    /**
     * bean 类
     */
    public Class<?> clz() {
        return clz;
    }

    /**
     * bean 类初始化函数
     */
    public Method clzInit() {
        return lifecycle.initMethod();
    }

    /**
     * bean 类注销函数
     */
    public Method clzDestroy() {
        return lifecycle.destroyMethod();
    }

    /**
     * bean 类构造函数
     */
    public Constructor clzCtor() {
        return rawCtor;
    }

    /**
     * bean 类构造函数参数
     */
    public Object[] clzCtorArgs() {
        return rawCtorArgs;
    }

    /**
     * bean 原始对象（未代理的）
     */
    public <T> T raw(boolean unproxied) {
        if (unproxied) {
            //未代理的
            return (T) rawUnproxied;
        } else {
            return (T) raw;
        }
    }

    /**
     * bean 原始对象（可能被代理的）
     */
    public <T> T raw() {
        return raw(false);
    }

    public void rawSet(Object raw) {
        if (this.raw == null) {
            this.raw = raw;
        }
    }

    public Class<?> rawClz() {
        if (rawClz == null) {
            return clz;
        } else {
            return rawClz;
        }
    }

    /**
     * bean 名字
     */
    public String name() {
        return name;
    }

    /**
     * bean 名字设置
     */
    public void nameSet(String name) {
        if (isDoned() == false) {
            this.name = name;
        }
    }

    /**
     * bean 序位
     */
    public int index() {
        return index;
    }

    /**
     * bean 序位设置
     */
    public void indexSet(int index) {
        if (isDoned() == false) {
            this.index = index;
        }
    }

    /**
     * bean 标签
     */
    public String tag() {
        return tag;
    }

    /**
     * bean 标签设置
     */
    public void tagSet(String tag) {
        if (isDoned() == false) {
            this.tag = tag;
        }
    }

    /**
     * bean 是否有类型化标识
     */
    public boolean typed() {
        return typed;
    }

    public void typedSet(boolean typed) {
        if (isDoned() == false) {
            this.typed = typed;
        }
    }

    /**
     * 注解
     */
    public Annotation[] annotations() {
        return annotations;
    }

    public <T extends Annotation> T annotationGet(Class<T> annClz) {
        return clz.getAnnotation(annClz);
    }

    /**
     * bean 获取对象（可能被代理的）
     */
    public <T> T get() {
        return get(false);
    }

    /**
     * bean 获取对象（未代理的）
     *
     * @param unproxied 未代理的
     */
    public <T> T get(boolean unproxied) {
        if (unproxied) {
            //未代理的
            if (singleton) {
                return (T) rawUnproxied;
            } else {
                return (T) _new();
            }
        } else {
            //可能代理的
            if (singleton) {
                return (T) raw;
            } else {
                return create();
            }
        }
    }

    public <T> T create() {
        Object bean = _new(); //如果是 interface ，则返回 _raw

        //3.尝试代理转换
        if (proxy != null) {
            bean = proxy.getProxy(this, bean);
        }

        return (T) bean;
    }


    /**
     * bean 新建对象
     */
    protected Object _new() throws ConstructionException {
        if (clz.isInterface() || clz.isAnonymousClass() || Modifier.isAbstract(clz.getModifiers())) {
            return raw;
        }

        try {
            //1.构造
            if (rawCtor == null) {
                rawCtor = rawClz().getDeclaredConstructor();
                rawCtorArgs = new Object[]{};
            }

            Object bean = ClassUtil.newInstance(rawCtor, rawCtorArgs);

            //2.完成注入动作
            context.beanInject(bean);

            //4.返回
            return bean;
        } catch (Throwable e) {
            if (e instanceof ConstructionException) {
                throw (ConstructionException) e;
            } else {
                throw new ConstructionException("Instantiation failure: " + clz.getTypeName(), e);
            }
        }
    }

    /**
     * 尝试初始化（仅对第一个实例有效）//保持与 LifecycleBean 相同策略
     *
     * @since 2.3
     */
    protected void tryInit(String initMethodName, String destroyMethodName) {
        if (lifecycle == null) {
            lifecycle = new BeanWrapLifecycle(this, initMethodName, destroyMethodName);
            if (lifecycle.check()) {
                context.lifecycle(lifecycle.index() + 1, lifecycle);

                if (singleton() == false) {
                    LogUtil.global().warn("Using lifecycle for non-singleton class is risky: " + rawClz().getName());
                }
            }
        }
    }

    /**
     * Bean 代理接口（为BeanWrap 提供切换代码的能力）
     *
     * @author noear
     * @since 1.0
     */
    @FunctionalInterface
    public interface Proxy {
        /**
         * 获取代理
         */
        Object getProxy(BeanWrap bw, Object bean);
    }
}