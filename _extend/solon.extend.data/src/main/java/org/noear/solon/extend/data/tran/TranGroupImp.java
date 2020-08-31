package org.noear.solon.extend.data.tran;


import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;

import java.util.ArrayList;
import java.util.List;

public class TranGroupImp implements Tran {
    List<Tran> nodes = new ArrayList<>();

    public TranGroupImp() {

    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public void add(Tran node) {
        if (node instanceof DbTran) {
            nodes.add(node);
            //((Tran) node).join(this);
        }
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
//        super.execute((tq) -> {
//            runnable.run();
//        });
    }
}