package org.noear.solon.core.wrap;

import org.noear.solon.core.VarHolder;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

/**
 * 字段变量容器 临时对象
 *
 * 为了稳藏 FieldWrap 的一些特性，并统一对外接口
 *
 * @author noear
 * @since 1.0
 * */
public class VarHolderOfField implements VarHolder {
    protected final FieldWrap fw;
    protected final Object obj;

    public VarHolderOfField(FieldWrap fw, Object obj) {
        this.fw = fw;
        this.obj = obj;
    }

    @Override
    public ParameterizedType getGenericType() {
        return fw.genericType;
    }

    @Override
    public boolean isField() {
        return true;
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
        fw.setValue(obj, val, true);
    }
}
