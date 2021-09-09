package org.noear.solon.extend.mybatisplus.integration;


import com.baomidou.mybatisplus.core.mapper.Mapper;
import demo.mappers.UserMapper;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

public class IGenericTypeResolverImplTest {

    private final IGenericTypeResolverImpl resolver = new IGenericTypeResolverImpl();

    @Test
    public void resolveTypeArguments() {
        System.out.println(Arrays.toString(resolver.resolveTypeArguments(DemoImpl.class, Map.class)));
        System.out.println(Arrays.toString(resolver.resolveTypeArguments(DemoImpl.class, Demo.class)));

        System.out.println(Arrays.toString(resolver.resolveTypeArguments(UserMapper.class, Mapper.class)));
    }

    private interface Demo<T> {}
    private abstract static class DemoImpl implements Map<Integer, String>, IGenericTypeResolverImplTest.Demo<Double> {

    }
}