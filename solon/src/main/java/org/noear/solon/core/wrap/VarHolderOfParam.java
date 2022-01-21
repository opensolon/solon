package org.noear.solon.core.wrap;

import org.noear.solon.core.VarHolder;
import org.noear.solon.core.util.GenericUtil;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * 参数变量容器 临时对象
 *
 * 为了稳藏 Parameter 的一些特性，并统一对外接口
 *
 * @author noear
 * @since 1.0
 * */
public class VarHolderOfParam implements VarHolder {
    private final Parameter p;
    private final Class<?> type;
    private final   ParameterizedType genericType;

    protected Object val;
    protected boolean done;
    protected Runnable onDone;

    public VarHolderOfParam(Class<?> clz, Parameter p, Runnable onDone){
        this.p = p;
        this.onDone = onDone;

        Type tmp = p.getParameterizedType();
        if (tmp instanceof TypeVariable) {
            genericType = null;
            //如果是类型变量，则重新构建类型

            Map<String, Type> gMap = GenericUtil.getGenericInfo(clz);
            type = (Class<?>) gMap.get(tmp.getTypeName());
        } else {
            type = p.getType();

            if (tmp instanceof ParameterizedType) {
                ParameterizedType gt0 = (ParameterizedType) tmp;

                Map<String, Type> gMap = GenericUtil.getGenericInfo(clz);
                Type[] gArgs = gt0.getActualTypeArguments();
                boolean gChanged = false;

                for (int i = 0; i < gArgs.length; i++) {
                    Type t1 = gArgs[i];
                    if (t1 instanceof TypeVariable) {
                        //尝试转换参数类型
                        gArgs[i] = gMap.get(t1.getTypeName());
                        gChanged = true;
                    }
                }

                if (gChanged) {
                    genericType = ParameterizedTypeImpl.make((Class<?>) gt0.getRawType(), gArgs, gt0.getOwnerType());
                } else {
                    genericType = gt0;
                }
            } else {
                genericType = null;
            }
        }
    }

    @Override
    public ParameterizedType getGenericType() {
        return genericType;
    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public Class<?> getType() {
        return type;
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
