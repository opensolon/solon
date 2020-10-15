package org.noear.solon.core;

import org.noear.solon.annotation.XInit;
import org.noear.solon.annotation.XSingleton;

import java.lang.annotation.Annotation;

/**
 * Bean 包装
 *
 * Bean 构建过程：Constructor(构造方法) -> @XInject(依赖注入) -> @XInit(初始化，相当于 PostConstruct)
 *
 * @author noear
 * @since 1.0
 * */
@SuppressWarnings("unchecked")
public class BeanWrap {
    // bean clz
    protected Class<?> _clz;
    // bean clz init method
    protected MethodWrap _clz_init;
    // bean raw（初始实例）
    protected Object _raw;
    // 是否为单例
    protected boolean _singleton;
    // 是否为远程服务
    protected boolean _remoting;
    // bean name
    protected String _name;
    // bean tag
    protected String _tag;
    // bean 申明的属性
    protected String _attrs;
    // bean 是否按注册类型
    protected boolean _typed;
    // bean 代理（为ASM代理提供接口支持）
    protected BeanProxy _proxy;
    // bean clz 的注解（算是缓存起来）
    protected final Annotation[] _annotations;


    public BeanWrap(Class<?> clz){
        this(clz, null);
    }

    public BeanWrap(Class<?> clz, Object raw) {
        _clz = clz;

        XSingleton ano = clz.getAnnotation(XSingleton.class);
        _singleton = (ano == null || ano.value()); //默认为单例
        _annotations = clz.getAnnotations();

        _tryBuildInit();

        if (raw == null) {
            _raw = _new();
        } else {
            _raw = raw;
        }
    }

    public BeanWrap(Class<?> clz, Object raw, String attrs) {
        this(clz, raw);
        attrsSet(attrs);
    }

    //设置代理
    public void proxySet(BeanProxy proxy){
        _proxy = proxy;

        if(_raw != null){
            //如果_raw存在，则进行代理转换
            _raw = proxy.getProxy(_raw);
        }
    }

    /**
     * 是否为单例
     * */
    public boolean singleton(){
        return _singleton;
    }

    public void singletonSet(boolean singleton){
        _singleton = singleton;
    }

    /**
     * is remoting()?
     */
    public boolean remoting() {
        return _remoting;
    }

    public void remotingSet(boolean remoting) {
        _remoting = remoting;
    }

    /**
     * bean 类
     */
    public Class<?> clz() {
        return _clz;
    }

    /**
     * bean 原始对象
     */
    public <T> T raw() {
        return (T) _raw;
    }
    protected void rawSet(Object raw) {
        _raw = raw;
    }
    /**
     * bean 标签
     * */
    public String name(){ return _name; }
    protected void nameSet(String name){ _name = name; }

    /**
     * bean 标签
     * */
    public String tag(){ return _tag; }
    protected void tagSet(String tag){ _tag = tag; }

    /**
     * bean 特性
     * */
    public String attrs(){ return _attrs; }
    protected void attrsSet(String attrs){ _attrs = attrs; }

    /**
     * bean 是否有类型化标识
     * */
    public boolean typed(){return _typed;}
    protected void typedSet(boolean typed){_typed = typed; }

    /**
     * 注解
     * */
    public Annotation[] annotations() {
        return _annotations;
    }
    public <T extends Annotation> T annotationGet(Class<T> clz){
        return clz.getAnnotation(clz);
    }

    /**
     * bean 获取对象
     */
    public <T> T get() {
        if (_singleton) {
            return (T) _raw;
        } else {
            return (T) _new(); //如果是 interface ，则返回 _raw
        }
    }

    /**
     * bean 新建对象
     */
    protected Object _new() {
        if (_clz.isInterface()) {
            return _raw;
        }

        try {
            //1.构造
            Object obj = _clz.newInstance();

            //2.注入
            Aop.inject(obj);

            //3.初始化
            if (_clz_init != null) {
                _clz_init.getMethod().invoke(obj);
            }

            if (_proxy != null) {
                obj = _proxy.getProxy(obj);
            }

            return obj;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 尝试构建初始化函数
     * */
    protected void _tryBuildInit() {
        if (_clz_init != null) {
            return;
        }

        if (_clz.isInterface()) {
            return;
        }

        ClassWrap clzWrap = ClassWrap.get(_clz);

        //查找初始化函数
        for (MethodWrap mw : clzWrap.getMethodWraps()) {
            if (mw.getMethod().getAnnotation(XInit.class) != null) {
                if (mw.getParameters().length == 0) {
                    //只接收没有参数的
                    _clz_init = mw;
                }
                break;
            }
        }
    }
}
