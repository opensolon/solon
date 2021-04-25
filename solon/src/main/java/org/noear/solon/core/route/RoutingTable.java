package org.noear.solon.core.route;

import org.noear.solon.core.handle.MethodType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 路由表
 *
 * @author noear
 * @since 1.0
 * */
public class RoutingTable<T> extends ArrayList<Routing<T>> {
    public RoutingTable() {
        super();
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public T matchOne(String path, MethodType method) {
        for (Routing<T> l : this) {
            if (l.matches(method, path)) {
                return l.target();
            }
        }

        return null;
    }

    /**
     * 区配多个目标（根据上上文）
     */
    public List<T> matchAll(String path, MethodType method) {
        return this.stream()
                .filter(l -> l.matches(method, path))
                .sorted(Comparator.comparingInt(l -> l.index()))
                .map(l -> l.target())
                .collect(Collectors.toList());
    }
}
