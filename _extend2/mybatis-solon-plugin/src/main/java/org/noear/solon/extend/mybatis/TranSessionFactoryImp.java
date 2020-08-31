package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.TranSession;
import org.noear.solon.core.TranSessionFactory;

public class TranSessionFactoryImp implements TranSessionFactory {
    @Override
    public TranSession create(String name) {
        //事务
        //
        SqlSessionFactory tmp = null;

        if (XUtil.isEmpty(name)) {
            //根据名字获取
            tmp = Aop.get(SqlSessionFactory.class);
        } else {
            //根据类型获取
            tmp = Aop.get(name);
        }

        if (tmp == null) {
           return null;
        }else{
            return new TranSessionImp(tmp);
        }
    }
}
