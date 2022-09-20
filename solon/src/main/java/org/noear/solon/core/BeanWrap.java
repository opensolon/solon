package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.core.util.IndexBuilder;
import org.noear.solon.core.wrap.ClassWrap;

/**
 * Bean 包装
 *
 * Bean 构建过程：Constructor(构造方法) -> @Inject(依赖注入) -> @Init(初始化，相当于 PostConstruct)
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
    // bean clz init method delay
    private boolean clzInitDelay;
    // bean raw（初始实例）
    private Object raw;
    // 是否为单例
    private boolean singleton;
    // 是否为远程服务
    private boolean remoting;
    // bean name
    private String name;
    // bean tag
    private String tag;
    // bean 申明的属性
    @Deprecated
    private String[] attrs;
    // bean 申明的属性Map形态
    @Deprecated
    private Map<String, String> attrMap;
    // bean 是否按注册类型
    private boolean typed;
    // bean 代理（为ASM代理提供接口支持）
    private BeanWrap.Proxy proxy;
    // bean clz 的注解（算是缓存起来）
    private final Annotation[] annotations;

    private final AopContext context;


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
        this.context = context;
        this.clz = clz;
        this.name = name;
        this.typed = typed;

        Singleton ano = clz.getAnnotation(Singleton.class);
        singleton = (ano == null || ano.value()); //默认为单例
        annotations = clz.getAnnotations();

        tryBuildInit();

        if (raw == null) {
            this.raw = _new();
        } else {
            this.raw = raw;
        }
    }

//    @Deprecated
//    public BeanWrap(AopContext context,Class<?> clz, Object raw, String name, String[] attrs) {
//        this(context,clz, raw, name);
//        attrsSet(attrs);
//    }

    public AopContext context() {
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
     * bean 原始对象
     */
    public <T> T raw() {
        return (T) raw;
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
     * bean 特性
     */
    @Deprecated
    public String[] attrs() {
        return attrs;
    }

    @Deprecated
    protected void attrsSet(String[] attrs) {
        this.attrs = attrs;
    }

    @Deprecated
    public String attrGet(String name) {
        if (attrs == null) {
            return null;
        }

        if (attrMap == null) {
            attrMap = new HashMap<>();

            for (String kv : attrs) {
                String[] ss = kv.split("=");
                if (ss.length == 2) {
                    attrMap.put(ss[0], ss[1]);
                }
            }
        }

        return attrMap.get(name);
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
     * bean 获取对象
     */
    public <T> T get() {
        if (singleton) {
            return (T) raw;
        } else {
            return (T) _new(); //如果是 interface ，则返回 _raw
        }
    }

    /**
     * bean 初始化
     */
    protected void init(Object bean) {
        //a.注入
        context.beanInject(bean);

        //b.调用初始化函数
        if (clzInit != null) {
            if (clzInitDelay) {
                int clzInitIndex = IndexBuilder.buildIndex(clz);
                context.beanOnloaded(clzInitIndex, (ctx) -> {
                    initInvokeDo(bean);
                });
            } else {
                initInvokeDo(bean);
            }
        }
    }


    protected void initInvokeDo(Object bean) {
        try {
            clzInit.invoke(bean);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
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
            Object bean = clz.newInstance();

            //2.初始化
            init(bean);

            if (proxy != null) {
                bean = proxy.getProxy(context(), bean);
            }

            return bean;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalArgumentException("Instantiation failure: " + clz.getTypeName(), ex);
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
                    clzInitDelay = initAnno.delay();
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
        Object getProxy(AopContext ctx, Object bean);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeanWrap)) return false;
        BeanWrap beanWrap = (BeanWrap) o;
        return clz.equals(beanWrap.clz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clz);
    }
}