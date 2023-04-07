package com.mybatisflex.solon.integration;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.solon.SolonRowSessionManager;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.apache.ibatis.solon.integration.MybatisAdapterManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author noear
 * @since 2.2
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        //
        // 此插件的 solon.plugin.priority 会大于 mybatis-solon-plugin 的值
        //
        MybatisAdapterManager.setAdapterFactory(new MybatisAdapterFactoryFlex());

        dbConfiguration();
    }

    private void dbConfiguration(){
        FlexGlobalConfig defaultConfig = FlexGlobalConfig.getDefaultConfig();
        if (defaultConfig == null){
            Logger.getLogger(Db.class.getName()).log(Level.WARNING,"Cannot get FlexGlobalConfig instance, Perhaps the dataSource config error.");
        }else {
            Db.invoker().setRowSessionManager(new SolonRowSessionManager());
        }
    }
}
