package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class FieldWrap {
    public final Class<?> entityClz;
    public final Field field;
    public final Annotation[] annoS;
    public final Class<?> type;
    public final ParameterizedType genericType;

    public FieldWrap(Class<?> clz, Field f1) {
        entityClz = clz;
        field = f1;
        annoS = f1.getAnnotations();

        type = f1.getType();
        Type tmp = f1.getGenericType();
        if(tmp instanceof ParameterizedType){
            genericType = (ParameterizedType)tmp;
        }else{
            genericType = null;
        }
    }
}
