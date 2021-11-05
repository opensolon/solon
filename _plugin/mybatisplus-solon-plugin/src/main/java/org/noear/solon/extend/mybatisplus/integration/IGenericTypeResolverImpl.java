package org.noear.solon.extend.mybatisplus.integration;

import com.baomidou.mybatisplus.core.toolkit.reflect.IGenericTypeResolver;
import org.noear.solon.core.util.GenericUtil;

/**
 * 泛型类型分解器
 *
 * @author iYarnFog
 * @since 1.5
 */
public class IGenericTypeResolverImpl implements IGenericTypeResolver {
    @Override
    public Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        return GenericUtil.resolveTypeArguments(clazz, genericIfc);
    }
}
