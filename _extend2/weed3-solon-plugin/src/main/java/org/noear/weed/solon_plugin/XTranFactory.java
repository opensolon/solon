package org.noear.weed.solon_plugin;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XTran;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Tran;
import org.noear.weed.DbContext;

import java.util.function.Function;

public class XTranFactory implements Function<XTran, Tran> {
    @Override
    public Tran apply(XTran tran) {
        if (tran.multisource()) {
            return new XTranGroupImp();
        } else {
            if(XUtil.isEmpty(tran.value())){
                throw  new RuntimeException("Please configure @XTran value");
            }

            DbContext ctx = Aop.get(tran.value());
            return new XTranImp(ctx);
        }
    }
}
