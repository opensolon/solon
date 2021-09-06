package org.noear.solon.extend.mybatisplus.integration;

import com.baomidou.mybatisplus.core.toolkit.reflect.IGenericTypeResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

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
                if (type.getRawType() == genericIfc) {
                    return Arrays.stream(type.getActualTypeArguments())
                            .map(item -> (Class<?>) item)
                            .toArray(Class[]::new);
                }
            }
        }
        return null;
    }
}
