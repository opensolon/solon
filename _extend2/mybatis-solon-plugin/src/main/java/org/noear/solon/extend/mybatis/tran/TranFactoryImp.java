package org.noear.solon.extend.mybatis.tran;

import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XTran;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Tran;
import org.noear.solon.core.TranFactory;
import org.noear.solon.core.TranPolicy;

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

    private Tran tranQueue = new TranQueueImp();
    private Tran tranExclude = new TranNotImp();

    @Override
    public Tran create(XTran tran) {
        if (tran.multisource()) {
            //事务队列
            return tranQueue;
        } else if (tran.policy() == TranPolicy.not_supported) {
            //事务排除
            return tranExclude;
        } else {
            //事务
            //
            SqlSessionFactory factory = null;

            if (XUtil.isEmpty(tran.value())) {
                //根据名字获取
                factory = Aop.get(SqlSessionFactory.class);
            } else {
                //根据类型获取
                factory = Aop.get(tran.value());
            }

            if (factory == null) {
                throw new RuntimeException("@XTran annotation failed");
            }

            if (tran.policy() == TranPolicy.required_new) {
                return new TranNewImp(factory);
            } else {
                return new TranImp(factory);
            }
        }
    }
}
