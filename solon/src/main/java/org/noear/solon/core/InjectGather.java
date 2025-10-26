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
package org.noear.solon.core;

import org.noear.solon.Utils;
import org.noear.solon.core.exception.InjectionException;
import org.noear.solon.core.util.ConsumerEx;

import java.util.ArrayList;
import java.util.List;

/**
 * 注入收集器，收集完成后会进行回调（主要为 Configuration 构建 method bean 时服务）
 *
 * @author noear
 * @since 1.0
 * */
public class InjectGather implements Runnable, Comparable<InjectGather> {
    //变量
    private List<VarHolder> vars;
    //变量数量
    private int varSize;
    //完成的
    private boolean done;
    //完成时
    private ConsumerEx<Object[]> onDone;
    //必须运行
    private boolean requireRun;
    //输出类型
    private Class<?> outType;
    //收集类型（0字段；1方法；2构造）
    private int label;
    //执行顺序位
    public int index;

    public InjectGather(int label, Class<?> outType, boolean requireRun, int varSize, ConsumerEx<Object[]> onDone) {
        this.requireRun = requireRun;
        this.label = label;
        this.onDone = onDone;
        this.varSize = varSize;
        this.vars = new ArrayList<>(varSize);
        this.outType = outType;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isMethod() {
        return label == 1;
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

            try {
                doneDo();
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Throwable ex) {
                throw new IllegalStateException(ex);
            }
        } finally {
            Utils.locker().unlock();
        }
    }

    /**
     * 提交
     */
    public void commit() {
        for (VarHolder p1 : vars) {
            p1.commit();
        }
    }

    /**
     * 检测
     */
    public void check() throws Throwable {
        Utils.locker().lock();

        try {
            if (done) {
                return;
            }

            if (vars.size() != varSize) {
                return;
            }

            for (VarHolder p1 : vars) {
                //尝试提交
                p1.commit();

                if (p1.isDone() == false && p1.required()) {
                    if (label == 1) {
                        throw new InjectionException("Method param injection failed: " + p1.getFullName());
                    } else if (label == 2) {
                        throw new InjectionException("Constructor param injection failed: " + p1.getFullName());
                    } else {
                        throw new InjectionException("Field injection failed: " + p1.getFullName());
                    }
                }
            }

            if (requireRun) {
                doneDo();
            }
        } finally {
            Utils.locker().unlock();
        }
    }

    private void doneDo() throws Throwable {
        if (done) {
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
    }

    @Override
    public int compareTo(InjectGather o) {
        if (this.index == o.index) {
            return 0;
        } else if (this.index < o.index) { //越小越前
            return -1;
        } else {
            return 1;
        }
    }
}
