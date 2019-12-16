package org.noear.solon.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public class FieldWrap {
    public final Field field;
    public final String name;

    private Method _getter;
    private Method _setter;

    protected FieldWrap(Class<?> clz, Field f1) {
        field = f1;
        name = f1.getName();

        _getter = findGetter(clz, f1);
        _setter = findSetter(clz, f1);
    }

    public Object getValue(Object tObj) throws ReflectiveOperationException{
        if(_getter == null){
            return field.get(tObj);
        }else{
            return _getter.invoke(tObj);
        }
    }

    public void setValue(Object tObj, Object val) throws ReflectiveOperationException{
        val = typeChange(val,field.getType());

        if(_setter == null){
            field.set(tObj,val);
        }else{
            _setter.invoke(tObj, new Object[]{val});
        }
    }

    private static Method findGetter(Class<?> tCls,Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "get" + firstLetter + fieldName.substring(1);

        try {
            Method getFun = tCls.getMethod(setMethodName);
            if(getFun !=null) {
                return getFun;
            }
        }catch (Throwable ex){
            ex.printStackTrace();
        }

        return null;
    }

    private static Method findSetter(Class<?> tCls, Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "set" + firstLetter + fieldName.substring(1);

        try {
            Method setFun = tCls.getMethod(setMethodName, new Class[]{field.getType()});
            if(setFun !=null) {
                return setFun;
            }
        }
        catch (Throwable ex){
            ex.printStackTrace();
        }
        return null;
    }

    private static Object typeChange(Object val, Class<?> type){
        if(val instanceof BigDecimal){
            if(Long.class == type || Long.TYPE == type){
                return ((BigDecimal)val).longValue();
            }

            if(Integer.class == type || Integer.TYPE == type){
                return ((BigDecimal)val).intValue();
            }

            if(Double.class == type || Double.TYPE == type){
                return ((BigDecimal)val).doubleValue();
            }
        }

        return val;
    }
}
