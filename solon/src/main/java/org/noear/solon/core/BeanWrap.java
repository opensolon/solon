package org.noear.solon.core;

import org.noear.solon.annotation.XSingleton;

/**
 * Bean 包装（可以被继承重写）
 * */
@SuppressWarnings("unchecked")
public class BeanWrap {
    protected Class<?> _clz;      // bean clz
    protected Object _raw;        // bean raw（初始实例）
    protected boolean _singleton; // 是否为单例
    protected boolean _remoting;  // 是否为远程服务

    public BeanWrap() {}

    public BeanWrap(Class<?> clz, Object raw){
        _clz = clz;

        XSingleton ano = clz.getAnnotation(XSingleton.class);
        _singleton = (ano == null || ano.value());

        if (raw == null) {
            _raw = _new();
        } else {
            _raw = raw;
        }
    }

    /** is remoting()? */
    public boolean remoting(){
        return _remoting;
    }

    public void remotingSet(boolean remoting){
        _remoting = remoting;
    }

    /** bean类 */
    public Class<?> clz(){return _clz;}
    /** bean原始对象 */
    public <T> T raw() {
        return (T)_raw;
    }
    protected void rawSet(Object raw){
        _raw = raw;
    }
    /** bean对象 */
    public <T> T get(){
        if(_singleton){
            return (T)_raw;
        }else{
            return (T)_new();
        }
    }

    /** bean 新建对象 */
    protected Object _new(){
        if(_clz.isInterface()){
            return null;
        }

        try{
            Object obj = _clz.newInstance();
            Aop.factory().inject(obj);
            return obj;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
