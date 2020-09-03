package org.noear.solon.extend.data;

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

    public Tran createGroup() {
        return new TranGroupImp();
    }

    public Tran createTran(TranMeta meta, boolean is_new) {
        //事务 required || (requires_new || nested)
        //
        if (is_new) {
            return new TranDbNewImp(meta);
        } else {
            return new TranDbImp(meta);
        }
    }

    public Tran create(TranMeta meta) {
        if (meta.group()) {
            //事务组
            return createGroup();
        } else if (meta.policy() == TranPolicy.not_supported) {
            //事务排除
            return tranNot;
        } else if (meta.policy() == TranPolicy.never) {
            //决不能有事务
            return tranNever;
        } else if (meta.policy() == TranPolicy.mandatory) {
            //必须要有当前事务
            return tranMandatory;
        } else {
            //事务
            //
            return createTran(meta, meta.policy() == TranPolicy.requires_new
                    || meta.policy() == TranPolicy.nested);

        }
    }

    public void pending(RunnableEx runnable) throws Throwable {
        tranNot.apply(runnable);
    }
}
