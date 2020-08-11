package org.noear.solon.extend.mybatis_sqlhelper;

import org.noear.solon.XApp;
import org.noear.solon.core.*;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        //
        //添加到加载源里
        //
        app.sourceAdd(SqlHelperMybatisAutoConfiguration.class);
    }
}
