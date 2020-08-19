package org.noear.weed.solon.plugin.tran;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XTran;
import org.noear.solon.core.*;
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
    public Tran create(XTran anno) {
        if (anno.group()) {
            //事务队列
            return new TranGroupImp();
        } else if (anno.policy() == TranPolicy.exclude) {
            //事务排除
            return tranNot;
        } else if (anno.policy() == TranPolicy.never) {
            //事务排除
            return tranNever;
        } else {
            //事务
            //
            DbContext db = null;

            if (XUtil.isEmpty(anno.value())) {
                //根据名字获取
                db = Aop.get(DbContext.class);
            } else {
                //根据类型获取
                db = Aop.get(anno.value());
            }

            if (db == null) {
                throw new RuntimeException("@XTran annotation failed");
            }

            if (anno.policy() == TranPolicy.requires_new
                    || anno.policy() == TranPolicy.nested) {
                return new TranNewImp(db);
            } else {
                return new TranImp(db);
            }
        }
    }
}
