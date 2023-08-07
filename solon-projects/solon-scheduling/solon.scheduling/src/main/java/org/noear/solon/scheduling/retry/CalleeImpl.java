package org.noear.solon.scheduling.retry;

import org.noear.solon.core.aspect.Invocation;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 被调用者
 *
 * @author noear
 * @since 2.4
 */
public class CalleeImpl implements Callee{
    private Invocation inv;

    public CalleeImpl(Invocation inv) {
        this.inv = inv;
    }

    /**
     * 被调目标
     * */
    public Object target() {
        return inv.target();
    }

    /**
     * 被调函数
     * */
    public Method method() {
        return inv.method().getMethod();
    }

    /**
     * 参数
     * */
    public Object args() {
        return inv.args();
    }

    /**
     * 参数 map 形式
     * */
    public Map<String, Object> argsAsMap() {
        return inv.argsAsMap();
    }

    /**
     * 调用
     * */
    public Object call() throws Throwable {
        return inv.method().getMethod().invoke(inv.target(), inv.args());
    }
}
