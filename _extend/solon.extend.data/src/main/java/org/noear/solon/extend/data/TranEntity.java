package org.noear.solon.extend.data;

import org.noear.solon.annotation.XTran;

class TranEntity {
    protected TranNode tran;
    protected XTran meta;

    public TranEntity(TranNode tran, XTran meta){
        this.tran = tran;
        this.meta = meta;
    }
}
