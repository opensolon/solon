/*
 * Copyright 2017-2025 noear.org and authors
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
package org.noear.solon.data.sqlink.base.toBean.beancreator;

import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import sun.misc.Unsafe;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * 默认对象创建器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class DefaultBeanCreator<T> extends AbsBeanCreator<T> {
    protected static final Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            ClassUtil.accessibleAsTrue(field);
            unsafe = (Unsafe) field.get(null);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public DefaultBeanCreator(Class<T> target) {
        super(target);
    }

    @Override
    protected Supplier<T> initBeanCreator(Class<T> target) {
        if (target.isAnonymousClass()) {
            return unsafeCreator(target);
        }
        else {
            return methodHandleCreator(target);
        }
    }

    protected Supplier<T> unsafeCreator(Class<T> target) {
        return () ->
        {
            try {
                return (T) unsafe.allocateInstance(target);
            }
            catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        };
    }

    protected Supplier<T> methodHandleCreator(Class<T> target) {
        try {
            MethodType constructorType = MethodType.methodType(void.class);
            MethodHandles.Lookup caller = MethodHandles.lookup();
            MethodHandle constructorHandle = caller.findConstructor(target, constructorType);

            CallSite site = LambdaMetafactory.altMetafactory(caller,
                    "get",
                    MethodType.methodType(Supplier.class),
                    constructorHandle.type().generic(),
                    constructorHandle,
                    constructorHandle.type(), 1);
            return (Supplier<T>) site.getTarget().invokeExact();
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected ISetterCaller<T> initBeanSetter(String property) {
        if (target.isAnonymousClass()) {
            return methodBeanSetter(property);
        }
        else {
            return methodHandleBeanSetter(property);
        }
    }

    protected ISetterCaller<T> methodBeanSetter(String property) {
        FieldMetaData fieldMetaData = MetaDataCache.getMetaData(target).getFieldMetaDataByFieldName(property);
        Method setter = fieldMetaData.getSetter();
        return (t, v) -> setter.invoke(t, v);
    }

    protected ISetterCaller<T> methodHandleBeanSetter(String property) {
        FieldMetaData fieldMetaData = MetaDataCache.getMetaData(target).getFieldMetaDataByFieldName(property);
        Class<?> propertyType = fieldMetaData.getType();

        MethodHandles.Lookup caller = MethodHandles.lookup();
        Method writeMethod = fieldMetaData.getSetter();
        MethodType setter = MethodType.methodType(writeMethod.getReturnType(), propertyType);

        Class<?> lambdaPropertyType = upperClass(propertyType);
        String getFunName = writeMethod.getName();
        try {
            MethodType instantiatedMethodType = MethodType.methodType(void.class, target, lambdaPropertyType);
            MethodHandle targetHandle = caller.findVirtual(target, getFunName, setter);
            MethodType samMethodType = MethodType.methodType(void.class, Object.class, Object.class);
            CallSite site = LambdaMetafactory.metafactory(
                    caller,
                    "apply",
                    MethodType.methodType(IVoidSetter.class),
                    samMethodType,
                    targetHandle,
                    instantiatedMethodType
            );

            IVoidSetter<Object, Object> objectPropertyVoidSetter = (IVoidSetter<Object, Object>) site.getTarget().invokeExact();
            return objectPropertyVoidSetter::apply;
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> upperClass(Class<?> c) {
        if (c.isPrimitive()) {
            if (c == Character.TYPE) {
                return Character.class;
            }
            if (c == Byte.TYPE) {
                return Byte.class;
            }
            else if (c == Short.TYPE) {
                return Short.class;
            }
            else if (c == Integer.TYPE) {
                return Integer.class;
            }
            else if (c == Long.TYPE) {
                return Long.class;
            }
            else if (c == Float.TYPE) {
                return Float.class;
            }
            else if (c == Double.TYPE) {
                return Double.class;
            }
            else if (c == Boolean.TYPE) {
                return Boolean.class;
            }
            else {
                return Void.class;
            }
        }
        else {
            return c;
        }
    }
}
