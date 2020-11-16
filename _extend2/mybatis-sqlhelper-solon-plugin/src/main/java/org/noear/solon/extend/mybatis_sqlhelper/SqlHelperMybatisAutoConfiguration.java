package org.noear.solon.extend.mybatis_sqlhelper;

import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.dialect.instrument.SQLInstrumentorConfig;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import com.jn.sqlhelper.mybatis.SqlHelperMybatisProperties;
import com.jn.sqlhelper.mybatis.plugins.CustomScriptLanguageDriver;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import com.jn.sqlhelper.mybatis.plugins.pagination.PaginationConfig;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.EventListener;

@Configuration
public class SqlHelperMybatisAutoConfiguration implements EventListener<org.apache.ibatis.session.Configuration> {

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        return MybatisUtils.vendorDatabaseIdProvider();
    }


    @Bean
    public SqlHelperMybatisProperties sqlHelperMybatisProperties(
            @Inject("${sqlhelper.mybatis.instrumentor}") SQLInstrumentorConfig sqlInstrumentConfig,
            @Inject("${sqlhelper.mybatis.pagination}") PaginationConfig paginationPluginConfig) {
        SqlHelperMybatisProperties p = new SqlHelperMybatisProperties();
        p.setInstrumentor(sqlInstrumentConfig);
        p.setPagination(paginationPluginConfig);
        return p;
    }

    private SqlHelperMybatisProperties sqlHelperMybatisProperties;

    @Bean
    public void setSqlHelperMybatisProperties(SqlHelperMybatisProperties sqlHelperMybatisProperties) {
        this.sqlHelperMybatisProperties = sqlHelperMybatisProperties;
    }

    @Override
    public void onEvent(org.apache.ibatis.session.Configuration configuration) {
        if(sqlHelperMybatisProperties == null){
            return;
        }

        System.out.println("Start to customize mybatis configuration with mybatis-sqlhelper-solon-plugin");
        configuration.setDefaultScriptingLanguage(CustomScriptLanguageDriver.class);

        SqlHelperMybatisPlugin plugin = new SqlHelperMybatisPlugin();
        plugin.setPaginationConfig(sqlHelperMybatisProperties.getPagination());
        plugin.setInstrumentorConfig(sqlHelperMybatisProperties.getInstrumentor());
        plugin.init();

        System.out.println(String.format("Add interceptor {} to mybatis configuration", plugin));
        System.out.println(String.format("The properties of the mybatis plugin [{}] is: {}", Reflects.getFQNClassName(SqlHelperMybatisPlugin.class), sqlHelperMybatisProperties));
        configuration.addInterceptor(plugin);
    }
}
