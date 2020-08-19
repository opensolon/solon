package org.noear.solon.extend.mybatis.tran;

import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XTran;
import org.noear.solon.core.*;

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
    public Tran create(TranAnno anno) {
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
            SqlSessionFactory factory = null;

            if (XUtil.isEmpty(anno.value())) {
                //根据名字获取
                factory = Aop.get(SqlSessionFactory.class);
            } else {
                //根据类型获取
                factory = Aop.get(anno.value());
            }

            if (factory == null) {
                throw new RuntimeException("@XTran annotation failed");
            }

            if (anno.policy() == TranPolicy.requires_new) {
                return new TranNewImp(factory);
            } else {
                return new TranImp(factory);
            }
        }
    }
}
