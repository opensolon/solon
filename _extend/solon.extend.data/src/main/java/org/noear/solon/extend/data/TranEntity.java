package org.noear.solon.extend.data;

import org.noear.solon.data.annotation.Tran;

class TranEntity {
    protected TranNode tran;
    protected Tran meta;

    public TranEntity(TranNode tran, Tran meta){
        this.tran = tran;
        this.meta = meta;
    }
}
