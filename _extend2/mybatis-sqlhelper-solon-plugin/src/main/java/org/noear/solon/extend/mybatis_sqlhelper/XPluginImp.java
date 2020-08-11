package org.noear.solon.extend.mybatis_sqlhelper;

import org.noear.solon.XApp;
import org.noear.solon.core.*;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        //马上加载
        app.loadBean(SqlHelperMybatisAutoConfiguration.class);
    }
}
