package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.IndexBuilder;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.wrap.ClassWrap;

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
    // bean clz init method
    private Method clzInit;
    // bean clz init method index
    private int clzInitIndex;
    // bean raw（初始实例）
    private Object raw;
    private Object rawUnproxied;
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


    public BeanWrap(AopContext context, Class<?> clz) {
        this(context, clz, null);
    }

    public BeanWrap(AopContext context, Class<?> clz, Object raw) {
        this(context, clz, raw, null);
    }

    /**
     * @since 1.10
     */
    public BeanWrap(AopContext context, Class<?> clz, Object raw, String name) {
        this(context, clz, raw, name, false);
    }

    /**
     * @since 1.10
     */
    public BeanWrap(AopContext context, Class<?> clz, Object raw, String name, boolean typed) {
        this.context = (AppContext) context;
        this.clz = clz;
        this.name = name;
        this.typed = typed;

        //不否为单例
        Singleton anoS = clz.getAnnotation(Singleton.class);
        singleton = (anoS == null || anoS.value()); //默认为单例

        annotations = clz.getAnnotations();

        //构建初始化函数
        tryBuildInit();

        //构建原生实例
        if (raw == null) {
            this.rawUnproxied = _new();
            this.raw = rawUnproxied;
        } else {
            this.rawUnproxied = raw;
            this.raw = raw;
        }

        //尝试初始化
        tryInit(this.raw);
    }

    public AppContext context() {
        return context;
    }

    public Proxy proxy() {
        return proxy;
    }

    //设置代理
    public void proxySet(BeanWrap.Proxy proxy) {
        this.proxy = proxy;

        if (raw != null) {
            //如果_raw存在，则进行代理转换
            raw = proxy.getProxy(context(), raw);
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
     * 初始化bean的方法
     */
    public Method clzInit() {
        return clzInit;
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

    protected void rawSet(Object raw) {
        this.raw = raw;
    }

    /**
     * bean 标签
     */
    public String name() {
        return name;
    }

    protected void nameSet(String name) {
        this.name = name;
    }

    public int index() {
        return index;
    }

    protected void indexSet(int index) {
        this.index = index;
    }

    /**
     * bean 标签
     */
    public String tag() {
        return tag;
    }

    protected void tagSet(String tag) {
        this.tag = tag;
    }

    /**
     * bean 是否有类型化标识
     */
    public boolean typed() {
        return typed;
    }

    protected void typedSet(boolean typed) {
        this.typed = typed;
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
                Object tmp = _new(); //如果是 interface ，则返回 _raw

                //3.尝试代理转换
                if (proxy != null) {
                    tmp = proxy.getProxy(context(), tmp);
                }

                return (T)tmp;
            }
        }
    }


    /**
     * bean 新建对象
     */
    protected Object _new() {
        if (clz.isInterface()) {
            return raw;
        }

        try {
            //1.构造
            Object bean = ClassUtil.newInstance(clz);

            //2.完成注入动作
            context.beanInject(bean);

            //4.返回
            return bean;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalArgumentException("Instantiation failure: " + clz.getTypeName(), ex);
        }
    }

    /**
     * 尝试初始化（仅对第一个实例有效）//保持与 LifecycleBean 相同策略
     *
     * @since 2.3
     */
    protected void tryInit(Object bean) {
        if(bean == null){
            return;
        }

        if (clzInit != null) {
            if (clzInitIndex == 0) {
                //如果为0，则自动识别
                clzInitIndex = new IndexBuilder().buildIndex(clz);
            }

            //保持与 LifecycleBean 相同策略：+1
            context.lifecycle(clzInitIndex + 1, () -> {
                try {
                    clzInit.invoke(bean);
                } catch (InvocationTargetException e) {
                    Throwable e2 = e.getTargetException();
                    throw Utils.throwableUnwrap(e2);
                }
            });
        }
    }

    /**
     * 尝试构建初始化函数
     */
    protected void tryBuildInit() {
        if (clzInit != null) {
            return;
        }

        if (clz.isInterface()) {
            return;
        }

        ClassWrap clzWrap = ClassWrap.get(clz);

        //查找初始化函数
        for (Method m : clzWrap.getMethods()) {
            Init initAnno = m.getAnnotation(Init.class);
            if (initAnno != null) {
                if (m.getParameters().length == 0) {
                    //只接收没有参数的，支持非公有函数
                    clzInit = m;
                    clzInit.setAccessible(true);
                    clzInitIndex = initAnno.index();

                    if (Solon.cfg().isDebugMode()) {
                        LogUtil.global().warn("@Init will be discarded, suggested use 'LifecycleBean' interface");
                    }
                }
                break;
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
        Object getProxy(AppContext ctx, Object bean);
    }
}