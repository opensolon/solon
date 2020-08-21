package org.noear.solon.extend.mybatis.tran;

import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.XUtil;
import org.noear.solon.annotation.XTran;
import org.noear.solon.core.*;
import org.noear.solon.ext.RunnableEx;

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
    private Tran tranMandatory = new TranMandatoryImp();
    private Tran tranNot = new TranNotImp();

    @Override
    public Tran create(XTran anno) {
        if (anno.group()) {
            //事务组
            return new TranGroupImp();
        } else if (anno.policy() == TranPolicy.not_supported) {
            //事务排除
            return tranNot;
        } else if (anno.policy() == TranPolicy.never) {
            //决不能有事务
            return tranNever;
        } else if (anno.policy() == TranPolicy.mandatory) {
            //必须要有当前事务
            return tranMandatory;
        } else {
            //事务
            //
            SqlSessionFactory tmp = null;

            if (XUtil.isEmpty(anno.value())) {
                //根据名字获取
                tmp = Aop.get(SqlSessionFactory.class);
            } else {
                //根据类型获取
                tmp = Aop.get(anno.value());
            }

            if (tmp == null) {
                throw new RuntimeException("@XTran annotation failed");
            }

            if (anno.policy() == TranPolicy.requires_new
                    || anno.policy() == TranPolicy.nested) {
                return new TranNewImp(tmp);
            } else {
                return new TranImp(tmp);
            }
        }
    }

    @Override
    public void pending(RunnableEx runnable) throws Throwable{
        tranNot.apply(runnable);
    }
}
