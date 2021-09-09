package org.noear.solon.extend.mybatisplus.integration;

import com.baomidou.mybatisplus.core.toolkit.reflect.IGenericTypeResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author noear, iYarnFog
 * @since 1.5
 */
public class IGenericTypeResolverImpl implements IGenericTypeResolver {
    @Override
    public Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        for (Type type0 : clazz.getGenericInterfaces()) {
            if (type0 instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) type0;
                Class<?> rawType = (Class<?>) type.getRawType();

                if (rawType == genericIfc || this.getGenericInterfaces(rawType).contains(genericIfc)) {
                    return Arrays.stream(type.getActualTypeArguments())
                            .map(item -> (Class<?>) item)
                            .toArray(Class[]::new);
                }
            }
        }
        return null;
    }

    /**
     * 获取指定类的所有父类
     * @param clazz: 要获取的类
     * @return 所有父类
     */
    private List<Class<?>> getGenericInterfaces(Class<?> clazz){
        return getGenericInterfaces(clazz, new ArrayList<>());
    }

    private List<Class<?>> getGenericInterfaces(Class<?> clazz, List<Class<?>> classes) {
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType) {
                Class<?> aClass = (Class<?>) ((ParameterizedType) type).getRawType();
                classes.add(aClass);
                for (Type type0 : aClass.getGenericInterfaces()) {
                    if (type0 instanceof ParameterizedType) {
                        this.getGenericInterfaces((Class<?>) ((ParameterizedType) type0).getRawType(), classes);
                    }
                }
            }
        }
        return classes;
    }
}
