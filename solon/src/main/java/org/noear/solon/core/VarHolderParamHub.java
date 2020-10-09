package org.noear.solon.core;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VarHolderParamHub implements Runnable {
    List<VarHolderParam> vars;
    Consumer<Object[]> done;

    public VarHolderParamHub(int varSize, Consumer<Object[]> done) {
        this.done = done;
        this.vars = new ArrayList<>(varSize);
    }

    public VarHolder add(Parameter p) {
        VarHolderParam p2 = new VarHolderParam(p, this);
        vars.add(p2);
        return p2;
    }

    @Override
    public void run() {
        for(VarHolderParam p1 : vars){
            if(p1.isDone() == false){
                return;
            }
        }

        List<Object> args = new ArrayList<>(vars.size());
        for(VarHolderParam p1 : vars){
            args.add(p1.getValue());
        }

        done.accept(args.toArray());
    }
}
