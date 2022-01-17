package org.noear.solon.core.wrap;

import org.noear.solon.core.VarHolder;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.GenericUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Map;

/**
 * 字段包装
 *
 * 用于缓存类的字段，并附加些功能（和 ClassWrap、MethodWrap 差不多意图）
 *
 * @author noear
 * @since 1.0
 * */
public class FieldWrap {
    /**
     * 实体类型
     */
    public final Class<?> entityClz;
    /**
     * 字段
     */
    public final Field field;
    /**
     * 自己申明的注解
     */
    public final Annotation[] annoS;
    /**
     * 字段类型
     */
    public final Class<?> type;
    /**
     * 字段泛型类型（可能为null）
     */
    public final ParameterizedType genericType;
    /**
     * 字段是否只读
     * */
    public final boolean readonly;

    /**
     * 值设置器
     */
    private Method _setter;
    /**
     * 值获取器
     */
    private Method _getter;

    public FieldWrap(Class<?> clz, Field f1, boolean isFinal) {
        entityClz = clz;
        field = f1;
        annoS = f1.getDeclaredAnnotations();
        readonly = isFinal;


        Type tmp = f1.getGenericType();
        if (tmp instanceof TypeVariable) {
            genericType = null;
            //如果是类型变量，则重新构建类型

            Map<TypeVariable, Type> gMap = GenericUtil.getGenericInfo(clz);
            type = (Class<?>) gMap.get(tmp);
        } else {
            type = f1.getType();

            if (tmp instanceof ParameterizedType) {
                genericType = (ParameterizedType) tmp;
            } else {
                genericType = null;
            }
        }

        field.setAccessible(true);
        _setter = doFindSetter(clz, f1);
        _getter = dofindGetter(clz, f1);
    }

    /**
     * 获取自身的临时对象
     */
    public VarHolder holder(Object obj) {
        return new VarHolderOfField(this, obj);
    }

    /**
     * 获取字段的值
     */
    public Object getValue(Object tObj) throws ReflectiveOperationException {
        if (_getter == null) {
            return field.get(tObj);
        } else {
            return _getter.invoke(tObj);
        }
    }

    /**
     * 设置字段的值
     */
    public void setValue(Object tObj, Object val) {
        if (readonly){
            return;
        }

        try {
            if (val == null) {
                return;
            }

            if (_setter == null) {
                field.set(tObj, val);
            } else {
                _setter.invoke(tObj, new Object[]{val});
            }
        } catch (IllegalArgumentException ex) {
            if (val == null) {
                throw new IllegalArgumentException(field.getName() + "(" + field.getType().getSimpleName() + ") Type receive failur!", ex);
            }

            throw new IllegalArgumentException(
                    field.getName() + "(" + field.getType().getSimpleName() +
                            ") Type receive failure ：val(" + val.getClass().getSimpleName() + ")", ex);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Method dofindGetter(Class<?> tCls, Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "get" + firstLetter + fieldName.substring(1);

        try {
            Method getFun = tCls.getMethod(setMethodName);
            if (getFun != null) {
                return getFun;
            }
        } catch (NoSuchMethodException ex) {

        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }


    /**
     * 查找设置器
     */
    private static Method doFindSetter(Class<?> tCls, Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "set" + firstLetter + fieldName.substring(1);

        try {
            Method setFun = tCls.getMethod(setMethodName, new Class[]{field.getType()});
            if (setFun != null) {
                return setFun;
            }
        } catch (NoSuchMethodException ex) {
            //正常情况，不用管
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
        return null;
    }
}
