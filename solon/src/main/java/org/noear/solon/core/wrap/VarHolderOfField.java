package org.noear.solon.core.wrap;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanSupplier;
import org.noear.solon.core.VarHolder;
import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.function.Supplier;

/**
 * 字段变量容器 临时对象
 *
 * 为了稳藏 FieldWrap 的一些特性，并统一对外接口
 *
 * @author noear
 * @since 1.0
 * */
public class VarHolderOfField implements VarHolder {
    private final FieldWrap fw;
    private final Object obj;
    private final AppContext ctx;

    private Object val;
    private boolean required = false;
    private boolean done;
    private Runnable onDone;

    public VarHolderOfField(AppContext ctx, FieldWrap fw, Object obj, Runnable onDone) {
        this.ctx = ctx;
        this.fw = fw;
        this.obj = obj;

        this.onDone = onDone;
    }

    /**
     * 应用上下文
     * */
    @Override
    public AppContext context() {
        return ctx;
    }

    /**
     * 泛型（可能为null）
     * */
    @Override
    public @Nullable ParameterizedType getGenericType() {
        return fw.genericType;
    }

    /**
     * 是否为字段
     * */
    @Override
    public boolean isField() {
        return true;
    }

    /**
     * 获取字段类型
     * */
    @Override
    public Class<?> getType(){
        return fw.type;
    }

    /**
     * 获取所有注解
     * */
    @Override
    public Annotation[] getAnnoS(){
        return fw.annoS;
    }

    /**
     * 获取完整名字
     * */
    @Override
    public String getFullName() {
        return fw.entityClz.getName() + "::" + fw.field.getName();
    }


    /**
     * 设置值
     */
    @Override
    public void setValue(Object val) {
        if (val != null) {
            if (val instanceof BeanSupplier) {
                val = ((BeanSupplier) val).get();
            }

            fw.setValue(obj, val, true);

            ctx.aot().registerJdkProxyType(getType(), val);
        }

        this.val = val;
        this.done = true;

        if (onDone != null) {
            onDone.run();
        }
    }

    /**
     * 获取值
     * */
    @Override
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
