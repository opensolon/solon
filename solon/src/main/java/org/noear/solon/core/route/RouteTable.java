package org.noear.solon.core.route;

import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.PathAnalyzer;

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
public class RouteTable<T> extends ArrayList<RouteTable.Route<T>> {
    public RouteTable() {
        super();
    }

    /**
     * 区配一个目标（根据上上文）
     */
    public T matchOne(String path, MethodType method) {
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
    public List<T> matchAll(String path, MethodType method) {
        return this.stream()
                .filter(l -> l.matches(method, path))
                .sorted(Comparator.comparingInt(l -> l.index))
                .map(l -> l.target)
                .collect(Collectors.toList());
    }

    /**
     * 路由记录
     * */
    public static class Route<T> {
        public Route(String path, MethodType method, int index, T target) {
            _p = path;
            _pr = new PathAnalyzer(path);
            _m = method;

            this.index = index;
            this.target = target;
        }

        public final int index; //顺序
        public final T target;//代理

        private final String _p; //path
        private final PathAnalyzer _pr; //path rule 规则
        private final MethodType _m; //方式


        /**
         * 是否匹配
         */
        public boolean matches(MethodType method2, String path2) {
            if (MethodType.ALL.code == _m.code) {
                return matches0(path2);
            } else if (MethodType.HTTP.code == _m.code) { //不是null时，不能用==
                if (method2.isHttpMethod()) {
                    return matches0(path2);
                }
            } else if (method2.code == _m.code) {
                return matches0(path2);
            }

            return false;
        }

        private boolean matches0(String path2) {
            if ("**".equals(_p)) {
                return true;
            }

            if (_p.equals(path2)) {
                return true;
            }

            return _pr.matches(path2);
        }
    }
}
