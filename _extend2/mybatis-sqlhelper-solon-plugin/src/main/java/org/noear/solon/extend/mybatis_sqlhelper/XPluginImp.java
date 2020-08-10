package org.noear.solon.extend.mybatis_sqlhelper;

import org.apache.ibatis.session.Configuration;
import org.noear.solon.XApp;
import org.noear.solon.core.*;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        //
        //在插件里loadBean，会比业务代码里的 bean 优先加载
        //
        Aop.beanLoad(SqlHelperMybatisAutoConfiguration.class);

        XEventBus.subscribe(Configuration.class, Aop.get(SqlHelperMybatisAutoConfiguration.class));

    }
}
