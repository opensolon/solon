/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.wrap;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.InjectGather;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.NameUtil;
import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * 字段包装
 *
 * 用于缓存类的字段，并附加些功能（和 ClassWrap、MethodWrap 差不多意图）
 *
 * @author noear
 * @since 1.0
 * */
public class FieldWrap {
    //所有者类
    private final Class<?> ownerClz;
    //字段
    private final Field field;
    //是否只读
    private final boolean isFinal;
    //是否字段
    private final boolean isStatic;

    //值设置器
    private Method _setter;
    //值获取器
    private Method _getter;

    //自己申明的注解（懒加载）
    private Annotation[] annoS;

    protected FieldWrap(Class<?> clz, Field f1) {
        ownerClz = clz;
        field = f1;
        isFinal = Modifier.isFinal(f1.getModifiers());
        isStatic = Modifier.isStatic(f1.getModifiers());

        if (isStatic == false) {
            _setter = doFindSetter(clz, f1);
            _getter = doFindGetter(clz, f1);
        }
    }


    //变量申明（懒加载）
    private VarSpec __spec;

    /**
     * 变量申明
     *
     * @since 2.3
     */
    public VarSpec spec() {
        if (__spec == null) {
            __spec = new FieldWrapSpec(this);
        }
        return __spec;
    }

    /**
     * 获取所有者类
     */
    public Class<?> getOwnerClz() {
        return ownerClz;
    }

    /**
     * 获取字段名
     */
    public String getName() {
        return field.getName();
    }

    /**
     * 获取字段
     */
    public Field getField() {
        return field;
    }


    //字段类型包装（懒加载）
    private TypeWrap typeWrap;

    private TypeWrap getTypeWrap() {
        if (typeWrap == null) {
            typeWrap = new TypeWrap(ownerClz, field.getType(), field.getGenericType());

            if (typeWrap.isInvalid()) {
                throw new IllegalStateException("Field generic analysis failed: "
                        + field.getDeclaringClass().getName()
                        + "."
                        + field.getName());
            }
        }

        return typeWrap;
    }

    public TypeWrap typeWrapOf(Type genericInfo) {
        if (genericInfo == null || genericInfo == ownerClz) {
            return getTypeWrap();
        } else {
            return new TypeWrap(genericInfo, field.getType(), field.getGenericType());
        }
    }

    /**
     * 获取类型
     */
    public Class<?> getType() {
        return getTypeWrap().getType();
    }

    /**
     * 获取参数类型
     */
    public @Nullable ParameterizedType getGenericType() {
        return getTypeWrap().getGenericType();
    }


    /**
     * 获取所有注解
     */
    public Annotation[] getAnnoS() {
        if (annoS == null) {
            annoS = field.getAnnotations();
        }

        return annoS;
    }

    /**
     * 获取自身的临时对象
     */
    public VarHolder holder(AppContext ctx, Object obj, InjectGather gather) {
        return new VarHolderOfField(ctx, this, obj, gather);
    }

    /**
     * 获取字段的值
     */
    public Object getValue(Object tObj) {
        return getValue(tObj, false);
    }

    /**
     * 获取字段的值
     */
    public Object getValue(Object tObj, boolean disFun) {
        if (isStatic) {
            tObj = null;
        }

        try {
            if (_getter == null || disFun) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field.get(tObj);
            } else {
                return _getter.invoke(tObj);
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 设置字段的值
     */
    public void setValue(Object tObj, Object val) {
        setValue(tObj, val, false);
    }

    public void setValue(Object tObj, Object val, boolean disFun) {
        if (isFinal) {
            return;
        }

        if (isStatic) {
            tObj = null;
        }

        try {
            if (val == null) {
                return;
            }

            if (_setter == null || disFun) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
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

    private static Method doFindGetter(Class<?> tCls, Field field) {
        String getterName = NameUtil.getPropGetterName(field.getName());

        try {
            Method getFun = tCls.getMethod(getterName);
            if (getFun != null) {
                return getFun;
            }
        } catch (NoSuchMethodException e) {

        } catch (SecurityException e) {
            LogUtil.global().warn("FieldWrap doFindGetter failed!", e);
        }

        return null;
    }


    /**
     * 查找设置器
     */
    private static Method doFindSetter(Class<?> tCls, Field field) {
        String setterName = NameUtil.getPropSetterName(field.getName());

        try {
            Method setFun = tCls.getMethod(setterName, field.getType());
            if (setFun != null) {
                return setFun;
            }
        } catch (NoSuchMethodException e) {
            //正常情况，不用管
        } catch (SecurityException e) {
            LogUtil.global().warn("FieldWrap doFindSetter failed!", e);
        }

        return null;
    }
}