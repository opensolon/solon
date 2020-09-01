package org.noear.solon.extend.data.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;

public class TranGroupImp extends DbTranNode {

    public TranGroupImp() {

    }


    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        try {
            runnable.run();

            if (parent == null) {
                commit();
            }
        } catch (Throwable ex) {
            if (parent == null) {
                rollback();
            }

            throw ex;
        } finally {
            if (parent == null) {
                close();
            }
        }
    }
}