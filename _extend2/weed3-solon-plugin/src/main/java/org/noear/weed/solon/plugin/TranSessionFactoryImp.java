package org.noear.weed.solon.plugin;

import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.TranSession;
import org.noear.solon.core.TranSessionFactory;
import org.noear.weed.DbContext;

public class TranSessionFactoryImp implements TranSessionFactory {
    @Override
    public TranSession create(String name) {
        //事务
        //
        DbContext tmp = null;

        if (XUtil.isEmpty(name)) {
            //根据名字获取
            tmp = Aop.get(DbContext.class);
        } else {
            //根据类型获取
            tmp = Aop.get(name);
        }

        if (tmp == null) {
            return null;
        } else {
            return new TranSessionImp(tmp);
        }
    }
}
