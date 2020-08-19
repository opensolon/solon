package org.noear.solon.extend.mybatis.tran;

import org.apache.ibatis.session.SqlSession;
import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;

public class TranNotImp implements Tran {
    protected TranNotImp() {

    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        SqlSession session = DbTranUtil.current();

        try {
            //移除事务
            DbTranUtil.currentRemove();

            runnable.run();
        } finally {
            if (session != null) {
                //把事务重新放回去
                //
                DbTranUtil.currentSet(session);
            }
        }
    }
}
