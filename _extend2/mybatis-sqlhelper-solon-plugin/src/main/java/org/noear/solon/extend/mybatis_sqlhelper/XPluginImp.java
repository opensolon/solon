package org.noear.solon.extend.mybatis_sqlhelper;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.*;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        XEventBus.subscribe(Configuration.class, (configuration -> {

        }));

    }
}
