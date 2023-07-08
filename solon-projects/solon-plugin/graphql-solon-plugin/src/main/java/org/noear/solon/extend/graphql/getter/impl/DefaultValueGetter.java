package org.noear.solon.extend.graphql.getter.impl;

import graphql.schema.DataFetchingEnvironment;
import java.lang.reflect.ParameterizedType;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.extend.graphql.getter.IValueGetter;

/**
 * TODO 目前使用 solon 提供的 @Param注解实现类似@Argument的功能
 * TODO 还要支持 类似 @Arguments 功能
 * TODO 其他类型转换需要支持
 *
 * @author fuzi1996
 * @since 2.3
 */
public class DefaultValueGetter implements IValueGetter {

    @Override
    public Object get(DataFetchingEnvironment environment,
            ParamWrap paramWrap) {

        String name = paramWrap.getName();
        Class<?> type = paramWrap.getType();
        ParameterizedType genericType = paramWrap.getGenericType();

        Object argument = environment.getArgument(name);

        if (type.isInstance(argument)) {
            return argument;
        }

        return null;
    }
}
