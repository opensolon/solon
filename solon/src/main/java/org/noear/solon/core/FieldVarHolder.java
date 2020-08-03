package org.noear.solon.core;

import java.lang.annotation.Annotation;

/**
 * 字段包装 临时对象
 *
 * 为了稳藏 FieldWrap 的一些特性
 * */
public class FieldVarHolder implements VarHolder{
    public final FieldWrap fw;
    protected final Object obj;

    public FieldVarHolder(FieldWrap fw, Object obj) {
        this.fw = fw;
        this.obj = obj;
    }

    /**
     * 获取字段类型
     * */
    @Override
    public Class<?> getType(){
        return fw.type;
    }

    /**
     * 获取所有注解
     * */
    @Override
    public Annotation[] getAnnoS(){
        return fw.annoS;
    }

    /**
     * 设置字段的值
     */
    @Override
    public void setValue(Object val) {
        fw.setValue(obj, val);
    }
}
