package org.noear.solon.core;

import org.noear.solon.annotation.XSingleton;

/**
 * Bean 包装（可以被继承重写）
 * */
public class BeanWrap {
    protected Class<?> _clz;
    protected Object _raw;
    protected boolean _singleton;

    public BeanWrap() {}

    public BeanWrap build(Class<?> clz, Object raw){
        _clz = clz;

        XSingleton ano = clz.getAnnotation(XSingleton.class);
        _singleton = (ano == null || ano.value());

        if (raw == null) {
            _raw = _new();
        } else {
            _raw = raw;
        }

        return this;
    }

    /** bean类 */
    public Class<?> clz(){return _clz;}
    /** bean原始对象 */
    public <T> T raw() {
        return (T)_raw;
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
        try{
            Object obj = _clz.newInstance();
            Aop.factory().inject(obj);
            return obj;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
