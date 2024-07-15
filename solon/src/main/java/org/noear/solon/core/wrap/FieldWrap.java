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
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.NameUtil;
import org.noear.solon.core.util.ParameterizedTypeImpl;
import org.noear.solon.lang.Nullable;

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
    public final @Nullable ParameterizedType genericType;
    /**
     * 字段是否只读
     */
    public final boolean readonly;

    /**
     * 值设置器
     */
    private Method _setter;
    /**
     * 值获取器
     */
    private Method _getter;

    protected FieldWrap(Class<?> clz, Field f1, boolean isFinal) {
        entityClz = clz;
        field = f1;
        annoS = f1.getAnnotations();
        readonly = isFinal;

        Type tmp = f1.getGenericType();
        if (tmp instanceof TypeVariable) {
            //如果是类型变量，则重新构建类型
            Map<String, Type> gMap = GenericUtil.getGenericInfo(clz);
            Type typeH = gMap.get(tmp.getTypeName());

            if (typeH instanceof ParameterizedType) {
                //以防外万一
                genericType = (ParameterizedType) typeH;
                type = (Class<?>) ((ParameterizedType) typeH).getRawType();
            } else {
                genericType = null;
                type = (Class<?>) typeH;
            }
        } else {
            type = f1.getType();

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
                    genericType = new ParameterizedTypeImpl((Class<?>) gt0.getRawType(), gArgs, gt0.getOwnerType());
                } else {
                    genericType = gt0;
                }
            } else {
                genericType = null;
            }
        }

        _setter = doFindSetter(clz, f1);
        _getter = doFindGetter(clz, f1);
    }


    private VarDescriptor descriptor;

    /**
     * 变量申明者
     *
     * @since 2.3
     */
    public VarDescriptor getDescriptor() {
        if (descriptor == null) {
            //采用懒加载，不浪费
            descriptor = new FieldWrapDescriptor(this);
        }
        return descriptor;
    }

    public String getName() {
        return field.getName();
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
    public Object getValue(Object tObj) throws ReflectiveOperationException {
        if (_getter == null) {
            return get(tObj);
        } else {
            return _getter.invoke(tObj);
        }
    }

    public Object get(Object tObj) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        return field.get(tObj);
    }

    /**
     * 设置字段的值
     */
    public void setValue(Object tObj, Object val) {
        setValue(tObj, val, false);
    }

    public void setValue(Object tObj, Object val, boolean disFun) {
        if (readonly) {
            return;
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
        String setterName = NameUtil.getPropSetterName(field.getName());

        try {
            Method setFun = tCls.getMethod(setterName, new Class[]{field.getType()});
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
