package com.jn.sqlhelper.mybatis.solon.integration;

import com.jn.sqlhelper.dialect.instrument.SQLInstrumentorConfig;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import com.jn.sqlhelper.mybatis.plugins.CustomScriptLanguageDriver;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import com.jn.sqlhelper.mybatis.plugins.pagination.PaginationConfig;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.session.Configuration;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.util.LogUtil;

/**
 * SqlHelper 分布插件配置器（添加拦截器）
 *
 * @author noear
 * @since 1.1
 * */
@org.noear.solon.annotation.Configuration
public class SqlHelperConfiguration implements EventListener<Configuration> {

    @Inject("${sqlhelper.mybatis.instrumentor}")
    SQLInstrumentorConfig sqlInstrumentConfig;

    @Inject("${sqlhelper.mybatis.pagination}")
    PaginationConfig paginationPluginConfig;

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        return MybatisUtils.vendorDatabaseIdProvider();
    }

    @Override
    public void onEvent(Configuration configuration) {
        if (sqlInstrumentConfig == null || paginationPluginConfig == null) {
            return;
        }

        LogUtil.global().info("Mybatis: Start to customize mybatis configuration with mybatis-sqlhelper-solon-plugin");
        configuration.setDefaultScriptingLanguage(CustomScriptLanguageDriver.class);

        SqlHelperMybatisPlugin plugin = new SqlHelperMybatisPlugin();
        plugin.setPaginationConfig(paginationPluginConfig);
        plugin.setInstrumentorConfig(sqlInstrumentConfig);
        plugin.init();

        LogUtil.global().info("Mybatis: The interceptor has been added: " + SqlHelperMybatisPlugin.class);
        configuration.addInterceptor(plugin);
    }
}
