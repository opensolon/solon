package org.noear.solon.core.wrap;

import org.noear.solon.core.VarHolder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;

/**
 * 参数变量容器 临时对象
 *
 * 为了稳藏 Parameter 的一些特性，并统一对外接口
 *
 * @author noear
 * @since 1.0
 * */
public class VarHolderOfParam implements VarHolder {
    protected Parameter p;
    protected Object val;
    protected boolean done;
    protected Runnable onDone;

    public VarHolderOfParam(Parameter p, Runnable onDone){
        this.p = p;
        this.onDone = onDone;
    }

    @Override
    public ParameterizedType getGenericType() {
        return (ParameterizedType)p.getParameterizedType();
    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public Class<?> getType() {
        return p.getType();
    }

    @Override
    public Annotation[] getAnnoS() {
        return p.getAnnotations();
    }

    @Override
    public void setValue(Object val) {
        this.val = val;
        this.done = true;

        if(onDone != null){
            onDone.run();
        }
    }

    public Object getValue(){
        return val;
    }

    public boolean isDone() {
        return done;
    }
}
