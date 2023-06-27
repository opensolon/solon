package org.noear.nami.common;

import org.noear.nami.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * 函数包装器（预处理并缓存）
 *
 * @author noear
 * @since 1.2
 */
public class MethodWrap {
    private static final Map<Method, MethodWrap> cached = new HashMap<>();

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

    protected void resolveMappingAnno(Method m){
        mappingAnno = m.getAnnotation(NamiMapping.class);
        if(mappingAnno == null){
            Mapping anno = m.getAnnotation(Mapping.class);
            if(anno != null){
                mappingAnno = new NamiMappingAnno(anno);
            }
        }
    }

    protected void resolveBodyAnno(Parameter p1) {
        bodyAnno = p1.getAnnotation(NamiBody.class);
        if (bodyAnno == null) {
            Body anno = p1.getAnnotation(Body.class);
            if (anno != null) {
                bodyAnno = new NamiBodyAnno(anno);
            }
        }
    }


    protected MethodWrap(Method m) {
        this.method = m;
        this.parameters = m.getParameters();
        resolveMappingAnno(m);

        for (Parameter p1 : parameters) {
            resolveBodyAnno(p1);
            if (bodyAnno != null) {
                bodyName = p1.getName();
                break;
            }
        }

        if (mappingAnno != null) {
            //格式1: GET
            //格式2: GET user/a.0.1
            if (mappingAnno.value().length() > 0) {
                String val = mappingAnno.value().trim();

                if (val.indexOf(" ") > 0) {
                    act = val.split(" ")[0];
                    fun = val.split(" ")[1];
                } else {
                    act = val;
                }
            }

            if (mappingAnno.headers().length > 0) {
                mappingHeaders = new HashMap<>();

                for (String h : mappingAnno.headers()) {
                    String[] ss = h.split("=");
                    if (ss.length == 2) {
                        mappingHeaders.put(ss[0].trim(), ss[1].trim());
                    }
                }
            }
        }
    }

    private Method method;
    private Parameter[] parameters;
    private String bodyName;
    private NamiBody bodyAnno;
    private NamiMapping mappingAnno;
    private Map<String,String> mappingHeaders;
    private String act;
    private String fun;

    public Method getMethod() {
        return method;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public String getBodyName() {
        return bodyName;
    }

    public NamiBody getBodyAnno() {
        return bodyAnno;
    }

    public NamiMapping getMappingAnno() {
        return mappingAnno;
    }

    public Map<String, String> getMappingHeaders() {
        return mappingHeaders;
    }

    public String getAct() {
        return act;
    }

    public String getFun() {
        return fun;
    }
}
