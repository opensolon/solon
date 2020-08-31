package org.noear.solon.extend.data;


import org.noear.solon.annotation.XTran;
import org.noear.solon.core.*;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.tran.*;

public final class TranFactory {
    private static TranFactory _singleton;

    public static TranFactory singleton() {
        if (_singleton == null) {
            _singleton = new TranFactory();
        }

        return _singleton;
    }

    private TranFactory() {
    }

    private Tran tranNever = new TranNeverImp();
    private Tran tranMandatory = new TranMandatoryImp();
    private Tran tranNot = new TranNotImp();

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
            TranSessionFactory factory = XBridge.tranSessionFactory();

            if(factory == null){
                throw new RuntimeException("Final initialization of tranSessionFactory");
            }

            TranSession session = factory.create(anno.value());

            if (session == null) {
                throw new RuntimeException("@XTran annotation failed");
            }

            if (anno.policy() == TranPolicy.requires_new
                    || anno.policy() == TranPolicy.nested) {
                return new TranDbNewImp(session);
            } else {
                return new TranDbImp(session);
            }
        }
    }

    public void pending(RunnableEx runnable) throws Throwable{
        tranNot.apply(runnable);
    }
}
