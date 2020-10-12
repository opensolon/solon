package net.hasor.solon.boot;

import net.hasor.core.TypeSupplier;
import org.noear.solon.core.Aop;

public class SolonTypeSupplier implements TypeSupplier {
    @Override
    public <T> T get(Class<? extends T> aClass) {
        return Aop.get(aClass);
    }

    @Override
    public <T> boolean test(Class<? extends T> targetType) {
        return Aop.context().getWrap(targetType) != null;
    }
}
