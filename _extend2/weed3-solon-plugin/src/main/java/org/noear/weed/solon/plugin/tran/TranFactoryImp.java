package org.noear.weed.solon.plugin.tran;

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

    private Tran tranNever = new TranNeverImp();
    private Tran tranNot = new TranNotImp();

    @Override
    public Tran create(XTran tran) {
        if (tran.group()) {
            //事务队列
            return new TranGroupImp();
        } else if (tran.policy() == TranPolicy.exclude) {
            //事务排除
            return tranNot;
        } else if (tran.policy() == TranPolicy.never) {
            //事务排除
            return tranNever;
        } else {
            //事务
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

            if (tran.policy() == TranPolicy.requires_new) {
                return new TranNewImp(db);
            } else {
                return new TranImp(db);
            }
        }
    }
}
