package org.noear.solon.extend.mybatis.tran;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;

public class TranNewImp extends DbTran implements Tran {
    protected TranNewImp(SqlSessionFactory factory) {
        super(factory);
    }

    @Override
    public void apply(RunnableEx runnable) throws Throwable {
        //获取当前事务
        //
        SqlSession session = DbTranUtil.current();

        try {
            //移除事务
            DbTranUtil.currentRemove();

            super.execute((t) -> {
                runnable.run();
            });
        } finally {
            if (session != null) {
                //把事务重新放回去
                //
                DbTranUtil.currentSet(session);
            }
        }
    }
}
