package org.noear.weed.solon.plugin;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XTran;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Tran;
import org.noear.solon.core.TranFactory;
import org.noear.solon.core.TranPolicy;
import org.noear.weed.DbContext;

public final class TranFactoryImp implements TranFactory {
    private static TranFactory _singleton;

    public static TranFactory singleton() {
        if (_singleton == null) {
            _singleton = new TranFactoryImp();
        }

        return _singleton;
    }

    private TranFactoryImp() {
    }

    private Tran tranQueue = new TranImpQueue();
    private Tran tranExclude = new TranImpExclude();

    @Override
    public Tran create(XTran tran) {
        if (tran.multisource()) {
            //队列事务
            return tranQueue;
        } else if (tran.policy() == TranPolicy.exclude) {
            //排除事务
            return tranExclude;
        } else {
            //jdbc事务
            //
            DbContext db = null;

            if (XUtil.isEmpty(tran.value())) {
                //根据名字获取
                db = Aop.get(DbContext.class);
            } else {
                //根据类型获取
                db = Aop.get(tran.value());
            }

            if (db == null) {
                throw new RuntimeException("@XTran annotation failed");
            }

            return new TranImp(db);
        }
    }
}
