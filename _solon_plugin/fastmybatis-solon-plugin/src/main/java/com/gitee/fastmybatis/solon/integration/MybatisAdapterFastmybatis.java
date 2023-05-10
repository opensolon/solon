package com.gitee.fastmybatis.solon.integration;

import com.gitee.fastmybatis.core.FastmybatisConfig;
import com.gitee.fastmybatis.core.ext.SqlSessionFactoryBuilderExt;
import com.gitee.fastmybatis.core.ext.SqlSessionFactoryInfo;
import com.gitee.fastmybatis.core.util.DbUtil;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.solon.integration.MybatisAdapterDefault;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * 适配器 for fastmybatis
 *
 * @author thc
 */
public class MybatisAdapterFastmybatis extends MybatisAdapterDefault {
    protected static final String CONFIG_LOCATION_DEFAULT = "mybatis/mybatis-config-default.xml";
    protected String configLocation = CONFIG_LOCATION_DEFAULT;
    protected SqlSessionFactoryBuilderExt sqlSessionFactoryBuilder;

    protected Properties properties;

    protected SqlSessionFactoryInfo sqlSessionFactoryInfo;

    protected FastmybatisConfig fastmybatisConfig;

    /**
     * 构建Sql工厂适配器，使用默认的 typeAliases 和 mappers 配置
     */
    protected MybatisAdapterFastmybatis(BeanWrap dsWrap) {
        super(dsWrap);
    }

    /**
     * 构建Sql工厂适配器，使用属性配置
     */
    protected MybatisAdapterFastmybatis(BeanWrap dsWrap, Props dsProps) {
        super(dsWrap, dsProps);
    }

    @Override
    protected void initConfiguration(Environment environment) {
        this.fastmybatisConfig = new FastmybatisConfig();
        Utils.injectProperties(fastmybatisConfig, dsProps);
        this.configLocation = dsProps.get("configLocation", CONFIG_LOCATION_DEFAULT);
        String basePackage = dsProps.get("basePackage");
        String[] mapperLocations = dsProps.getList("mappers").toArray(new String[0]);
        Objects.requireNonNull(configLocation);
        Objects.requireNonNull(basePackage);
        Objects.requireNonNull(fastmybatisConfig);
        this.fastmybatisConfig.setMapperLocations(mapperLocations);
        DataSource dataSource = this.getDataSource();
        String dialect = DbUtil.getDialect(dataSource);
        try {
            InputStream inputStream = Resources.getResourceAsStream(configLocation);
            SqlSessionFactoryBuilderExt sqlSessionFactoryBuilderExt = new SqlSessionFactoryBuilderExt(basePackage, fastmybatisConfig, dialect, environment);
            sqlSessionFactoryBuilder = sqlSessionFactoryBuilderExt;
            sqlSessionFactoryInfo = sqlSessionFactoryBuilderExt.buildSqlSessionFactoryInfo(inputStream, dsWrap.name(), properties);
            this.config = sqlSessionFactoryInfo.getConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("初始化mybatis失败", e);
        }
        Props cfgProps = this.dsProps.getProp("configuration");
        if (cfgProps.size() > 0) {
            Utils.injectProperties(this.config, cfgProps);
        }
        dsWrap.context().getBeanAsync(SqlSessionFactoryBuilderExt.class, bean -> {
            sqlSessionFactoryBuilder = bean;
        });
    }

    /**
     * 获取会话工厂
     */
    @Override
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            factory = sqlSessionFactoryInfo.getSqlSessionFactory();
        }
        return factory;
    }


}
