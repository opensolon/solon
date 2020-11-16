package org.noear.solon.extend.mybatis_sqlhelper;

import org.noear.solon.Solon;
import org.noear.solon.core.*;

public class XPluginImp implements Plugin {
    @Override
    public void start(Solon app) {
        //马上加载
        app.beanMake(SqlHelperMybatisAutoConfiguration.class);
    }
}
