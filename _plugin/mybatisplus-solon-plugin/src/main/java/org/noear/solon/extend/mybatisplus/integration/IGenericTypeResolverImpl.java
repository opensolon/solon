package org.noear.solon.extend.mybatisplus.integration;

import com.baomidou.mybatisplus.core.toolkit.reflect.IGenericTypeResolver;

/**
 * @author noear
 * @since 1.5
 */
public class IGenericTypeResolverImpl implements IGenericTypeResolver {
    @Override
    public Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        return null;
    }
}
