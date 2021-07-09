package org.noear.solon.data.tran;

import org.noear.solon.data.annotation.Tran;

/**
 * 事物实体
 *
 * @author noear
 * @since 1.0
 * */
class TranEntity {
    protected TranNode tran;
    protected Tran meta;

    public TranEntity(TranNode tran, Tran meta){
        this.tran = tran;
        this.meta = meta;
    }
}
