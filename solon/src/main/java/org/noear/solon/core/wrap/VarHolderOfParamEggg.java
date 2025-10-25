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

import org.noear.eggg.ParamEggg;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.InjectGather;
import org.noear.solon.core.VarHolder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * 参数变量容器 临时对象
 *
 * 为了稳藏 Parameter 的一些特性，并统一对外接口
 *
 * @author noear
 * @since 1.0
 * */
public class VarHolderOfParamEggg implements VarHolder {
    private final ParamEggg pw;
    private final AppContext ctx;
    private Class<?> dependencyType;

    private Object val;
    private Supplier valDef;
    private boolean done;
    private boolean required = false;

    private InjectGather gather;

    public VarHolderOfParamEggg(AppContext ctx, ParamEggg pw, InjectGather gather) {
        this.ctx = ctx;
        this.pw = pw;
        this.gather = gather;
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
        return false;
    }


    /**
     * 获取依赖类型
     */
    @Override
    public Class<?> getDependencyType() {
        if (dependencyType == null) {
            return getType();
        } else {
            return dependencyType;
        }
    }

    /**
     * 配置依赖类型
     */
    @Override
    public void setDependencyType(Class<?> dependencyType) {
        this.dependencyType = dependencyType;
    }

    /**
     * 类型
     */
    @Override
    public Class<?> getType() {
        return pw.getType();
    }

    /**
     * 泛型（可能为 null）
     */
    @Override
    public Type getGenericType() {
        return pw.getGenericType();
    }

    /**
     * 注解
     */
    @Override
    public Annotation[] getAnnoS() {
        return pw.getAnnotations();
    }

    /**
     * 获取完整名字
     */
    @Override
    public String getFullName() {
        Executable e = pw.getParam().getDeclaringExecutable();

        Class<?> declClz = e.getDeclaringClass();
        Class<?> fileClz = declClz;
        if(declClz.isMemberClass()){
            fileClz = declClz.getEnclosingClass();
        }

        if (e instanceof Method) {
            Method m = (Method) e;
            return "'" + pw.getParam().getName() + "'" + "\r\n\tat " + declClz.getName() + "." + m.getName() + "(" + fileClz.getSimpleName() + ".java:0)";
        } else {
            return "'" + pw.getParam().getName() + "'" + "\r\n\tat " + declClz.getName() + "(" + fileClz.getSimpleName() + ".java:10)";
        }
    }

    /**
     * 设置值
     */
    @Override
    public void setValue(Object val) {
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
        if (pw.getGenericType() == null) {
            return pw.getName() + ":" + pw.getType().getTypeName();
        } else {
            return pw.getName() + ":" + pw.getGenericType().getTypeName();
        }
    }
}