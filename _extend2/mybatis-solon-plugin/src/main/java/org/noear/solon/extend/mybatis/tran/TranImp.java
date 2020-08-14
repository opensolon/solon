package org.noear.solon.extend.mybatis.tran;

import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.core.Tran;
import org.noear.solon.ext.RunnableEx;

public class TranImp extends DbTran implements Tran {
    protected TranImp(SqlSessionFactory factory) {
        super(factory);
    }

    @Override
    public void execute(RunnableEx runnable) throws Throwable {
        super.execute((t) -> {
            runnable.run();
        });
    }
}
