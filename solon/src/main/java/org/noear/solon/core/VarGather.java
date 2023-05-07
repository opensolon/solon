package org.noear.solon.core;

import org.noear.solon.core.exception.InjectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 变量收集器（主要为 Configuration 构建 method bean 时服务）
 *
 * @see AopContext#tryBuildBean
 * @author noear
 * @since 1.0
 * */
public class VarGather implements Runnable {
    //变量
    private List<VarHolder> vars;
    //变量数量
    private int varSize;
    //完成的
    private boolean done;
    //完成时
    private Consumer<Object[]> onDone;
    private boolean requireRun;

    public VarGather(boolean requireRun, int varSize, Consumer<Object[]> onDone) {
        this.requireRun = requireRun;
        this.onDone = onDone;
        this.varSize = varSize;
        this.vars = new ArrayList<>(varSize);
    }

    public void add(VarHolder p) {
        //VarHolderOfParam p2 = new VarHolderOfParam(bw.context(), p, this);
        vars.add(p);
    }

    @Override
    public void run() {
        for (VarHolder p1 : vars) {
            if (p1.isDone() == false) {
                return;
            }
        }

        if (vars.size() != varSize) {
            return;
        }

        done = true;
        if(onDone != null) {
            List<Object> args = new ArrayList<>(vars.size());
            for (VarHolder p1 : vars) {
                args.add(p1.getValue());
            }

            onDone.accept(args.toArray());
        }
    }

    /**
     * 检测
     */
    public void check() throws Exception {
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
    }
}
