package org.noear.solon.core;

import org.noear.solon.core.exception.InjectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 注入收集器，收集完成后会进行回调（主要为 Configuration 构建 method bean 时服务）
 *
 * @see AppContext#tryBuildBean
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
    //执行顺序位
    public int index;

    public InjectGather(Class<?> outType, boolean requireRun, int varSize, Consumer<Object[]> onDone) {
        this.requireRun = requireRun;
        this.onDone = onDone;
        this.varSize = varSize;
        this.vars = new ArrayList<>(varSize);
        this.outType = outType;
    }

    public boolean isDone() {
        return done;
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
    public synchronized void run() {
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
    }

    /**
     * 检测
     */
    public synchronized void check() throws Exception {
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
