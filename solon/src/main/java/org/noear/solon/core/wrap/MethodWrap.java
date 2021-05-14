package org.noear.solon.core.wrap;

import org.noear.solon.annotation.*;
import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.InterceptorChain;
import org.noear.solon.core.handle.InterceptorChainNode;
import org.noear.solon.core.handle.InterceptorEntity;
import org.noear.solon.core.handle.Invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 方法包装
 *
 * 用于缓存类的方法，并附加些功能（和 ClassWrap、FieldWrap 意图相同）
 *
 * @author noear
 * @since 1.0
 * */
public class MethodWrap implements InterceptorChain, MethodHolder {
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
        entityClz = m.getDeclaringClass();

        method = m;
        parameters = paramsWrap(m.getParameters());
        annotations = m.getAnnotations();
        arounds = new ArrayList<>();

        //scan cless @Around
        for (Annotation anno : entityClz.getAnnotations()) {
            if (anno instanceof Around) {
                doAroundAdd((Around) anno);
            } else {
                InterceptorEntity ie = Aop.context().beanInterceptorGet(anno.annotationType());
                if (ie != null) {
                    doAroundAdd(ie);
                } else {
                    doAroundAdd(anno.annotationType().getAnnotation(Around.class));
                }
            }
        }

        //scan method @Around
        for (Annotation anno : annotations) {
            if (anno instanceof Around) {
                doAroundAdd((Around) anno);
            } else {
                InterceptorEntity ie = Aop.context().beanInterceptorGet(anno.annotationType());
                if (ie != null) {
                    doAroundAdd(ie);
                } else {
                    doAroundAdd(anno.annotationType().getAnnotation(Around.class));
                }
            }
        }

        if (arounds.size() > 0) {
            //排序（顺排）
            arounds.sort(Comparator.comparing(x -> x.index));

            //生成调用链(先入先出)
            InterceptorChainNode node = arounds.get(0);
            for (int i = 1, len = arounds.size(); i < len; i++) {
                node.next = arounds.get(i);
                node = arounds.get(i);
            }
            node.next = this;

            //设定根节点
            invokeChain = arounds.get(0);
        } else {
            invokeChain = this;
        }
    }

    private ParamWrap[] paramsWrap(Parameter[] pAry) {
        ParamWrap[] tmp = new ParamWrap[pAry.length];
        for (int i = 0, len = pAry.length; i < len; i++) {
            tmp[i] = new ParamWrap(pAry[i]);
        }

        return tmp;
    }


    private void doAroundAdd(Around a) {
        if (a != null) {
            arounds.add(new InterceptorChainNode(a.index(), Aop.get(a.value())));
        }
    }

    private void doAroundAdd(InterceptorEntity i) {
        if (i != null) {
            arounds.add(new InterceptorChainNode(i.index, i.interceptor));
        }
    }

    //实体类型
    private final Class<?> entityClz;
    //函数
    private final Method method;
    //函数参数
    private final ParamWrap[] parameters;
    //函数注解
    private final Annotation[] annotations;
    //函数包围列表（扩展切点）
    private final List<InterceptorChainNode> arounds;
    //函数调用链
    private final InterceptorChain invokeChain;


    /**
     * 获取函数名
     */
    public String getName() {
        return method.getName();
    }

    /**
     * 获取函数本身
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取函数反回类型
     */
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    /**
     * 获取函数参数
     */
    public ParamWrap[] getParamWraps() {
        return parameters;
    }

    /**
     * 获取函数所有注解
     */
    public Annotation[] getAnnotations() {
        return annotations;
    }

    /**
     * 获取函数某种注解
     */
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return method.getAnnotation(type);
    }


    //::XInterceptorChain
    @Override
    public Object doIntercept(Invocation inv) throws Exception {
        return invoke(inv.target(), inv.args());
    }

    /**
     * 执行
     */
    public Object invoke(Object obj, Object[] args) throws Exception {
        return method.invoke(obj, args);
    }

    /**
     * 执行切面
     */
    public Object invokeByAspect(Object obj, Object[] args) throws Throwable {
        Invocation inv = new Invocation(obj, args, this);
        return invokeChain.doIntercept(inv);
    }
}