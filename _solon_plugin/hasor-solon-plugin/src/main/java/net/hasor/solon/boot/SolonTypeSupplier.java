package net.hasor.solon.boot;

import net.hasor.core.TypeSupplier;
import org.noear.solon.Solon;

public class SolonTypeSupplier implements TypeSupplier {
    @Override
    public <T> T get(Class<? extends T> aClass) {
        return Solon.context().getBean(aClass);
    }

    @Override
    public <T> boolean test(Class<? extends T> targetType) {
        return Solon.context().getWrap(targetType) != null;
    }
}
