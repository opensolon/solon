package org.noear.solon.extend.data;

import org.noear.solon.annotation.XTran;
import org.noear.solon.core.*;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.trans.*;

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

    private TranNode tranNever = new TranNeverImp();
    private TranNode tranMandatory = new TranMandatoryImp();
    private TranNode tranNot = new TranNotImp();


    public TranNode create(XTran meta) {
        if (meta.policy() == TranPolicy.not_supported) {
            //事务排除
            return tranNot;
        } else if (meta.policy() == TranPolicy.never) {
            //决不能有事务
            return tranNever;
        } else if (meta.policy() == TranPolicy.mandatory) {
            //必须要有当前事务
            return tranMandatory;
        } else {
            //事务 required || (requires_new || nested)
            //
            if (meta.policy() == TranPolicy.requires_new
                    || meta.policy() == TranPolicy.nested) {
                return new TranDbNewImp(meta);
            } else {
                return new TranDbImp(meta);
            }

        }
    }

    public void pending(RunnableEx runnable) throws Throwable {
        tranNot.apply(runnable);
    }
}
