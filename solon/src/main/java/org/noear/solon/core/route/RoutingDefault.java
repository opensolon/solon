package org.noear.solon.core.route;

import org.noear.solon.core.SignalType;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.PathAnalyzer;

/**
 * 路由默认实现
 *
 * @author noear
 * @since 1.3
 */
public class RoutingDefault<T> implements Routing<T> {

    public RoutingDefault(String path, MethodType method, T target) {
        this(path, method, 0, target);
    }

    public RoutingDefault(String path, MethodType method, int index, T target) {
        this.rule = PathAnalyzer.get(path);

        this.method = method;
        this.path = path;
        this.index = index;
        this.target = target;
    }

    private final PathAnalyzer rule; //path rule 规则

    private final int index; //顺序
    private final String path; //path
    private final T target;//代理
    private final MethodType method; //方式

    @Override
    public int index() {
        return index;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public T target() {
        return target;
    }

    @Override
    public MethodType method() {
        return method;
    }

    /**
     * 是否匹配
     */
    @Override
    public boolean matches(MethodType method2, String path2) {
        if (MethodType.ALL == method || MethodType.ALL == method2) {
            return matches0(path2);
        } else if (MethodType.HTTP == method) { //不是null时，不能用==
            if (method2.signal == SignalType.HTTP) {
                return matches0(path2);
            }
        } else if (method2 == method) {
            return matches0(path2);
        }

        return false;
    }

    private boolean matches0(String path2) {
        //1.如果当前为**，任何路径都可命中
        if ("**".equals(path) || "/**".equals(path)) {
            return true;
        }

        //2.如果与当前路径相关
        if (path.equals(path2)) {
            return true;
        }

        //3.正则检测
        return rule.matches(path2);
    }
}