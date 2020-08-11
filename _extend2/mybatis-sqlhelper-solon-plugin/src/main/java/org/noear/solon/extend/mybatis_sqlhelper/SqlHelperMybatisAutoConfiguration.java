package org.noear.solon.extend.mybatis_sqlhelper;

import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.dialect.instrument.SQLInstrumentorConfig;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import com.jn.sqlhelper.mybatis.SqlHelperMybatisProperties;
import com.jn.sqlhelper.mybatis.plugins.CustomScriptLanguageDriver;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import com.jn.sqlhelper.mybatis.plugins.pagination.PaginationConfig;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.session.Configuration;
import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.annotation.XEvent;
import org.noear.solon.annotation.XInject;
import org.noear.solon.core.XEventListener;

@XEvent(Configuration.class)
@XConfiguration
public class SqlHelperMybatisAutoConfiguration implements XEventListener<Configuration> {

    @XBean
    public DatabaseIdProvider databaseIdProvider() {
        return MybatisUtils.vendorDatabaseIdProvider();
    }


    @XBean
    public SqlHelperMybatisProperties sqlHelperMybatisProperties(
            @XInject("${sqlhelper.mybatis.instrumentor}") SQLInstrumentorConfig sqlInstrumentConfig,
            @XInject("${sqlhelper.mybatis.pagination}") PaginationConfig paginationPluginConfig) {
        SqlHelperMybatisProperties p = new SqlHelperMybatisProperties();
        p.setInstrumentor(sqlInstrumentConfig);
        p.setPagination(paginationPluginConfig);
        return p;
    }

    private SqlHelperMybatisProperties sqlHelperMybatisProperties;

    @XBean
    public void setSqlHelperMybatisProperties(SqlHelperMybatisProperties sqlHelperMybatisProperties) {
        this.sqlHelperMybatisProperties = sqlHelperMybatisProperties;
    }

    @Override
    public void onEvent(Configuration configuration) {
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
