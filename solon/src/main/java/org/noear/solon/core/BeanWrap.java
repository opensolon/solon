package org.noear.solon.core;

import org.noear.solon.annotation.XInit;
import org.noear.solon.annotation.XSingleton;
import sun.util.resources.cldr.ss.CurrencyNames_ss;

/**
 * Bean 包装（可以被继承重写）
 *
 * Bean 构建过程：Constructor(构造方法) -> @XInject(依赖注入) -> @XInit(初始化，相当于 PostConstruct)
 * */
@SuppressWarnings("unchecked")
public class BeanWrap {
    protected Class<?> _clz;      // bean clz
    protected MethodWrap _clz_init;
    protected Object _raw;        // bean raw（初始实例）
    protected boolean _singleton; // 是否为单例
    protected boolean _remoting;  // 是否为远程服务
    protected String _tag;

    public BeanWrap() {
    }

    public BeanWrap(Class<?> clz, Object raw) {
        _clz = clz;

        XSingleton ano = clz.getAnnotation(XSingleton.class);
        if (ano != null) {
            //JSR330，默认为非单例
            _singleton = ano.value();
        }

        _buildInit();

        if (raw == null) {
            _raw = _new();
        } else {
            _raw = raw;
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
     * bean tag
     * */
    public String tag(){ return  _tag; }
    protected void tagSet(String tag){ _tag = tag; }

    /**
     * bean对象
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
            if(_clz_init != null){
                _clz_init.getMethod().invoke(obj);
            }

            return obj;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void _buildInit() {
        if (_clz_init != null) {
            return;
        }

        if (_clz.isInterface()) {
            return;
        }

        ClassWrap clzWrap = ClassWrap.get(_clz);

        //查找初始化函数
        for (MethodWrap mw : clzWrap.methodWraps) {
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
