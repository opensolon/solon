package org.noear.solon.core.wrap;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.VarHolder;
import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * 参数变量容器 临时对象
 *
 * 为了稳藏 Parameter 的一些特性，并统一对外接口
 *
 * @author noear
 * @since 1.0
 * */
public class VarHolderOfParam implements VarHolder {
    private final Parameter p;
    private final ParameterizedType genericType;
    private final AppContext ctx;

    protected Object val;
    protected boolean done;
    protected boolean required = false;
    protected Runnable onDone;

    public VarHolderOfParam(AppContext ctx, Parameter p, Runnable onDone) {
        this.ctx = ctx;
        this.p = p;
        this.onDone = onDone;

        //简化处理 //只在 @Bean 时有用；不会有复杂的泛型
        Type tmp = p.getParameterizedType();
        if (tmp instanceof ParameterizedType) {
            genericType = (ParameterizedType) tmp;
        } else {
            genericType = null;
        }
    }

    /**
     * 应用上下文
     * */
    @Override
    public AppContext context() {
        return ctx;
    }

    /**
     * 泛型（可能为 null）
     * */
    @Override
    public @Nullable ParameterizedType getGenericType() {
        return genericType;
    }

    /**
     * 是否为字段
     * */
    @Override
    public boolean isField() {
        return false;
    }

    /**
     * 类型
     */
    @Override
    public Class<?> getType() {
        return p.getType();
    }

    /**
     * 注解
     */
    @Override
    public Annotation[] getAnnoS() {
        return p.getAnnotations();
    }

    /**
     * 获取完整名字
     * */
    @Override
    public String getFullName() {
        Executable e = p.getDeclaringExecutable();
        return e.getDeclaringClass().getName() + "::" + e.getName();
    }

    /**
     * 设置值
     */
    @Override
    public void setValue(Object val) {
        this.val = val;
        this.done = true;

        if (onDone != null) {
            onDone.run();
        }
    }

    /**
     * 获取值
     * */
    public Object getValue() {
        return val;
    }

    /**
     * 是否为完成的（设置值后即为完成态）
     * */
    public boolean isDone() {
        return done;
    }

    /**
     * 是否必须
     * */
    @Override
    public boolean required() {
        return required;
    }

    /**
     * 设定必须
     * */
    @Override
    public void required(boolean required) {
        this.required = required;
    }
}