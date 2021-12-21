package org.noear.solon.core.util;

import java.util.Objects;

/**
 * @author noear
 * @since 1.6
 */
public class RunnableEntity {
    public final Runnable runnable;
    public final int index;

    public RunnableEntity(Runnable r, int i) {
        runnable = r;
        index = i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunnableEntity)) return false;
        RunnableEntity that = (RunnableEntity) o;
        return Objects.equals(runnable, that.runnable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runnable);
    }
}
