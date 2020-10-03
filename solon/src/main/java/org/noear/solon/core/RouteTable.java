package org.noear.solon.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 路由表
 * */
public class RouteTable<T> extends ArrayList<Route<T>> {
    public RouteTable() {
        super();
    }

    public RouteTable(Collection<Route<T>> coll) {
        super(coll);
    }


    /**
     * 区配一个目标（根据上上文）
     */
    public T matchOne(String path, XMethod method) {
        for (Route<T> l : this) {
            if (l.matches(method, path)) {
                return l.target;
            }
        }

        return null;
    }

    /**
     * 区配多个目标（根据上上文）
     */
    public List<T> matchAll(String path, XMethod method) {
        return this.stream()
                .filter(l -> l.matches(method, path))
                .sorted(Comparator.comparingInt(l -> l.index))
                .map(l -> l.target)
                .collect(Collectors.toList());
    }
}
