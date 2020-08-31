package org.noear.solon.extend.data;

import org.noear.solon.annotation.XTran;
import org.noear.solon.core.Tran;

class TranEntity {
    protected Tran tran;
    protected XTran anno;

    public TranEntity(Tran tran, XTran anno){
        this.tran = tran;
        this.anno = anno;
    }
}
