package org.noear.solon.core;

import org.noear.solon.annotation.XTran;

public class TranEntity {
    protected Tran tran;
    protected XTran anno;

    public TranEntity(Tran tran, XTran anno){
        this.tran = tran;
        this.anno = anno;
    }
}
