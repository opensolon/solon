package org.noear.wood.solon.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.ClassUtil;
import org.noear.wood.WoodConfig;
import org.noear.wood.annotation.Db;
import org.noear.wood.xml.XmlSqlLoader;

import javax.sql.DataSource;

/**
 * @author noear
 * @since 1.10
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        // 事件监听，用于时实初始化
        context.subWrapsOfType(DataSource.class, bw -> {
            DbManager.global().reg(bw);
        });

        // 切换Weed的链接工厂，交于Solon托管这
        WoodConfig.connectionFactory = new DsConnectionFactoryImpl();

        // 添加响应器（构建、注入）
        DbBeanReactor beanReactor = new DbBeanReactor();

        context.beanBuilderAdd(Db.class, beanReactor);
        context.beanInjectorAdd(Db.class, beanReactor);

        // 加载xml sql
        if (ClassUtil.hasClass(() -> XmlSqlLoader.class)) {
            XmlSqlLoader.tryLoad();
        }
    }
}
