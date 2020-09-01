package org.noear.solon.extend.data;

import org.noear.solon.core.Tran;

class TranEntity {
    protected Tran tran;
    protected TranMeta meta;

    public TranEntity(Tran tran, TranMeta meta){
        this.tran = tran;
        this.meta = meta;
    }
}
