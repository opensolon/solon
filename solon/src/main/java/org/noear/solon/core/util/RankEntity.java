package org.noear.solon.core.util;

import java.util.Objects;

/**
 * @author noear
 * @since 1.6
 */
public class RankEntity<T> {
    public final T target;
    public final int index;

    public RankEntity(T t, int i) {
        target = t;
        index = i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RankEntity)) return false;
        RankEntity that = (RankEntity) o;
        return Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target);
    }
}
