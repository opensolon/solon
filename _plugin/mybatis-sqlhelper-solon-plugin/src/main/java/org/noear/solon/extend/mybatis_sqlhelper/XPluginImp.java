package org.noear.solon.extend.mybatis_sqlhelper;

import org.noear.solon.SolonApp;
import org.noear.solon.core.*;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.beanMake(SqlHelperConfiguration.class);
    }
}
