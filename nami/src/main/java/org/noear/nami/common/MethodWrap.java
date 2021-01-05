package org.noear.nami.common;

import org.noear.nami.annotation.Body;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author noear 2021/1/5 created
 */
public class MethodWrap {
    private static Map<Method, MethodWrap> cached = new HashMap<>();

    public static MethodWrap get(Method method) {
        MethodWrap mw = cached.get(method);
        if (mw == null) {
            synchronized (method) {
                mw = cached.get(method);
                if (mw == null) {
                    mw = new MethodWrap(method);
                    cached.put(method, mw);
                }
            }
        }

        return mw;
    }


    protected MethodWrap(Method m) {
        this.method = m;

        for (Parameter p1 : m.getParameters()) {
            if (p1.getAnnotation(Body.class) != null) {
                hasBody = true;
                return;
            }
        }
    }

    private final Method method;
    private boolean hasBody;

    public Method getMethod() {
        return method;
    }

    public boolean hasBody() {
        return hasBody;
    }
}
