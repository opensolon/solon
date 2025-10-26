/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.wrap;

import org.noear.eggg.FieldEggg;
import org.noear.eggg.TypeEggg;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.InjectGather;
import org.noear.solon.core.VarHolder;

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
    private final FieldEggg fe;
    private final ParameterizedType pType;

    private final Object obj;
    private final AppContext ctx;
    private Class<?> dependencyType;

    private Object val;
    private Supplier valDef;
    private boolean required = false;
    private boolean done;
    private InjectGather gather;

    public VarHolderOfField(AppContext ctx, FieldEggg fe, Object obj, InjectGather gather) {
        this.ctx = ctx;
        this.fe = fe;
        this.obj = obj;

        this.gather = gather;

        if (fe.getTypeEggg().isParameterizedType()) {
            pType = fe.getTypeEggg().getParameterizedType();
        } else {
            pType = null;
        }
    }

    /**
     * 应用上下文
     */
    @Override
    public AppContext context() {
        return ctx;
    }

    /**
     * 是否为字段
     */
    @Override
    public boolean isField() {
        return true;
    }

    @Override
    public TypeEggg getTypeEggg() {
        return fe.getTypeEggg();
    }

    /**
     * 获取字段类型
     */
    @Override
    public Class<?> getType() {
        return fe.getTypeEggg().getType();
    }

    /**
     * 泛型（可能为null）
     */
    @Override
    public ParameterizedType getGenericType() {
        return pType;
    }


    @Override
    public Class<?> getDependencyType() {
        if (dependencyType == null) {
            return getType();
        } else {
            return dependencyType;
        }
    }

    @Override
    public void setDependencyType(Class<?> dependencyType) {
        this.dependencyType = dependencyType;
    }

    /**
     * 获取所有注解
     */
    @Override
    public Annotation[] getAnnoS() {
        return fe.getAnnotations();
    }

    /**
     * 获取完整名字
     */
    @Override
    public String getFullName() {
        Class<?> declClz = fe.getField().getDeclaringClass();
        Class<?> fileClz = declClz;
        if (declClz.isMemberClass()) {
            fileClz = declClz.getEnclosingClass();
        }

        StringBuilder buf = new StringBuilder();
        buf.append("'").append(fe.getName()).append("'");


        buf.append("\r\n\tat ").append(declClz.getName())
                .append(".").append(fe.getName())
                .append("(").append(fileClz.getSimpleName()).append(".java:0)");


        if (declClz != fe.getOwnerEggg().getType()) {
            buf.append("\r\n\tat ").append(fe.getOwnerEggg().getType().getName());
        }

        return buf.toString();
    }


    /**
     * 设置值
     */
    @Override
    public void setValue(Object val) {
        if (val != null) {
            fe.setValue(obj, val, false);

            ctx.aot().registerJdkProxyType(getType(), val);
        }

        this.val = val;
        this.done = true;

        if (gather != null) {
            gather.run();
        }
    }

    @Override
    public void setValueDefault(Supplier supplier) {
        this.valDef = supplier;
    }

    /**
     * 获取值
     */
    @Override
    public Object getValue() {
        if (val == null) {
            if (valDef != null) {
                return valDef.get();
            }
        }
        return val;
    }

    public void commit() {
        if (isDone()) {
            return;
        }

        if (valDef != null) {
            Object tmp = valDef.get();
            if (tmp != null) {
                setValue(tmp);
            }
        }
    }


    /**
     * 是否为完成的（设置值后即为完成态）
     */
    public boolean isDone() {
        return done;
    }

    /**
     * 是否必须
     */
    @Override
    public boolean required() {
        return required;
    }

    /**
     * 设定必须
     */
    @Override
    public void required(boolean required) {
        this.required = required;
    }

    @Override
    public String toString() {
        if (fe.getGenericType() == null) {
            return fe.getName() + ":" + fe.getType().getTypeName();
        } else {
            return fe.getName() + ":" + fe.getGenericType().getTypeName();
        }
    }
}