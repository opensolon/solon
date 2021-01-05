package org.noear.nami.common;

import org.noear.nami.annotation.Body;
import org.noear.nami.annotation.Mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

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
        this.parameters = m.getParameters();
        this.mappingAnno = m.getAnnotation(Mapping.class);

        for (Parameter p1 : parameters) {
            bodyAnno = p1.getAnnotation(Body.class);
            if (bodyAnno != null) {
                bodyName = p1.getName();
                break;
            }
        }
    }

    private Method method;
    private Parameter[] parameters;
    private String bodyName;
    private Body bodyAnno;
    private Mapping mappingAnno;

    public Method getMethod() {
        return method;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public String getBodyName() {
        return bodyName;
    }

    public Body getBodyAnno() {
        return bodyAnno;
    }

    public Mapping getMappingAnno() {
        return mappingAnno;
    }
}
