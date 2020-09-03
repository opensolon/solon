package org.noear.solon.extend.data;

import org.noear.solon.annotation.XTran;

class TranEntity {
    protected Tran tran;
    protected XTran meta;

    public TranEntity(Tran tran, XTran meta){
        this.tran = tran;
        this.meta = meta;
    }
}
