package org.noear.solon.core;

import org.noear.solon.annotation.XInit;
import org.noear.solon.annotation.XSingleton;

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
        _singleton = (ano == null || ano.value());

        _buildInit();

        if (raw == null) {
            _raw = _new();
        } else {
            _raw = raw;
        }
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
     * bean类
     */
    public Class<?> clz() {
        return _clz;
    }

    /**
     * bean原始对象
     */
    public <T> T raw() {
        return (T) _raw;
    }
    protected void rawSet(Object raw) {
        _raw = raw;
    }

    public String tag(){ return  _tag; }
    protected void tagSet(String tag){ _tag = tag; }

    /**
     * bean对象
     */
    public <T> T get() {
        if (_singleton) {
            return (T) _raw;
        } else {
            return (T) _new();
        }
    }

    /**
     * bean 新建对象
     */
    protected Object _new() {
        if (_clz.isInterface()) {
            return null;
        }

        try {
            //1.构造
            Object obj = _clz.newInstance();

            //2.注入
            Aop.factory().inject(obj);

            //3.初始化
            if(_clz_init != null){
                _clz_init.method.invoke(obj);
            }

            return obj;
        } catch (Exception ex) {
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

        for (MethodWrap mw : clzWrap.methodWraps) {
            if (mw.method.getAnnotation(XInit.class) != null) {
                if (mw.parameters.length == 0) {
                    //只接收没有参数的
                    _clz_init = mw;
                }

                break;
            }
        }
    }
}
