package org.noear.solon.extend.data.tran;

import org.noear.solon.core.TranSession;
import org.noear.solon.ext.RunnableEx;

public class DbTran {
    private TranSession session;

    public DbTran(TranSession session){
        this.session = session;
    }

    public void execute(RunnableEx runnable){
        try {
            session.open();

            DbTranUtil.currentSet(this);
            runnable.run();

            session.commit();
        } catch (Throwable ex) {
            session.rollback();
        } finally {
            DbTranUtil.currentRemove();
            session.close();
        }
    }
}
