package org.noear.solon.extend.mybatis_sqlhelper;

import org.apache.ibatis.session.Configuration;
import org.noear.solon.XApp;
import org.noear.solon.core.*;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        //
        //加载bean到容器；
        //
        Aop.beanLoad(SqlHelperMybatisAutoConfiguration.class);

        //获取bean，进行事件订阅
        XEventBus.subscribe(Configuration.class, Aop.get(SqlHelperMybatisAutoConfiguration.class));
    }
}
