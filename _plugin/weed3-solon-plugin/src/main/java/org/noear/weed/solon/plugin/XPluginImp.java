package org.noear.weed.solon.plugin;

import org.noear.solon.SolonApp;
import org.noear.solon.core.*;
import org.noear.weed.WeedConfig;
import org.noear.weed.annotation.Db;
import org.noear.weed.xml.XmlSqlLoader;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        // 事件监听，用于时实初始化
        app.onEvent(BeanWrap.class, new DsEventListener());

        // 切换Weed的链接工厂，交于Solon托管这
        WeedConfig.connectionFactory = new DsConnectionFactoryImpl();

        // 添加响应器（构建、注入）
        DbBeanReactor beanReactor = new DbBeanReactor();

        Aop.context().beanBuilderAdd(Db.class, beanReactor);
        Aop.context().beanInjectorAdd(Db.class, beanReactor);

        // 加载xml sql
        XmlSqlLoader.tryLoad();
    }
}
