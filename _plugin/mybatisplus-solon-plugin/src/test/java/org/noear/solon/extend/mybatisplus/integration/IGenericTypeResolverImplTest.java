package org.noear.solon.extend.mybatisplus.integration;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

class IGenericTypeResolverImplTest {

    private final IGenericTypeResolverImpl genericTypeResolver = new IGenericTypeResolverImpl();

    @Test
    void resolveTypeArguments() {
        System.out.println(Arrays.toString(this.genericTypeResolver.resolveTypeArguments(DemoImpl.class, Map.class)));
        System.out.println(Arrays.toString(this.genericTypeResolver.resolveTypeArguments(DemoImpl.class, Demo.class)));
    }

    private interface Demo<T> {}
    private abstract static class DemoImpl implements Map<Integer, String>, IGenericTypeResolverImplTest.Demo<Double> {

    }
}