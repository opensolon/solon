package org.noear.solon.extend.data.tran;

import org.noear.solon.core.TranSessionFactory;
import org.noear.solon.ext.RunnableEx;

public class DbTran {
    private TranSessionFactory factory;
    public DbTran(TranSessionFactory factory){
        this.factory = factory;
    }

    public void execute(RunnableEx runnable){

    }
}
