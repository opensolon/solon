package org.noear.solon.data.ds;

import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanInjector;
import org.noear.solon.data.annotation.Ds;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author noear
 * @since 1.5
 */
public final class DsAop {
    protected static final Set<BeanBuilder<Ds>> builderSet = new TreeSet<>();
    protected static final Set<BeanInjector<Ds>> injectorSet = new TreeSet<>();

    public static void beanBuilderAdd(BeanBuilder<Ds> builder) {
        builderSet.add(builder);
    }

    public static void beanInjectorAdd(BeanInjector<Ds> injector) {
        injectorSet.add(injector);
    }
}
