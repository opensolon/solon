/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.core;

import org.noear.solon.Utils;
import org.noear.solon.core.exception.InjectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 注入收集器，收集完成后会进行回调（主要为 Configuration 构建 method bean 时服务）
 *
 * @see AppContext#tryBuildBeanOfMethod
 * @author noear
 * @since 1.0
 * */
public class InjectGather implements Runnable {
    //变量
    private List<VarHolder> vars;
    //变量数量
    private int varSize;
    //完成的
    private boolean done;
    //完成时
    private Consumer<Object[]> onDone;
    //必须运行
    private boolean requireRun;
    //输出类型
    private Class<?> outType;
    //是否方法
    private boolean isMethod;
    //执行顺序位
    public int index;

    public InjectGather(boolean isMethod, Class<?> outType, boolean requireRun, int varSize, Consumer<Object[]> onDone) {
        this.requireRun = requireRun;
        this.isMethod = isMethod;
        this.onDone = onDone;
        this.varSize = varSize;
        this.vars = new ArrayList<>(varSize);
        this.outType = outType;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isMethod() {
        return this.isMethod;
    }

    public Class<?> getOutType() {
        return outType;
    }

    public List<VarHolder> getVars() {
        return vars;
    }

    public void add(VarHolder p) {
        //VarHolderOfParam p2 = new VarHolderOfParam(bw.context(), p, this);
        vars.add(p);
    }

    /**
     * 运行（变量收集完成后，回调运行）
     */
    @Override
    public void run() {
        Utils.locker().lock();

        try {
            if (done) {
                return;
            }

            for (VarHolder p1 : vars) {
                if (p1.isDone() == false) {
                    return;
                }
            }

            if (vars.size() != varSize) {
                return;
            }

            done = true;
            if (onDone != null) {
                List<Object> args = new ArrayList<>(vars.size());
                for (VarHolder p1 : vars) {
                    args.add(p1.getValue());
                }

                onDone.accept(args.toArray());
            }
        } finally {
            Utils.locker().unlock();
        }
    }

    /**
     * 检测
     */
    public void check() throws Exception {
        Utils.locker().lock();

        try {
            if (done) {
                return;
            }

            if (vars.size() != varSize) {
                return;
            }

            for (VarHolder p1 : vars) {
                if (p1.isDone() == false && p1.required()) {
                    throw new InjectionException("Required injection failed: " + p1.getFullName());
                }
            }

            if (onDone != null && requireRun) {
                //补触 onDone
                List<Object> args = new ArrayList<>(vars.size());
                for (VarHolder p1 : vars) {
                    args.add(p1.getValue());
                }

                done = true;
                onDone.accept(args.toArray());
            }
        } finally {
            Utils.locker().unlock();
        }
    }
}
