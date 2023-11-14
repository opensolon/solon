package com.mybatisflex.solon.integration;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.mybatis.FlexSqlSessionFactoryBuilder;
import com.mybatisflex.core.row.RowMapperInvoker;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.solon.integration.MybatisAdapterDefault;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.event.EventBus;

import javax.sql.DataSource;

/**
 * MyBatis-Flex 适配器。
 *
 * @author noear
 * @since 2.2
 */
public class MybatisAdapterFlex extends MybatisAdapterDefault {

    FlexSqlSessionFactoryBuilder factoryBuilderPlus;

    FlexGlobalConfig globalConfig;

    RowMapperInvoker rowMapperInvoker;

    protected MybatisAdapterFlex(BeanWrap dsWrap) {
        super(dsWrap);

        factoryBuilderPlus = new FlexSqlSessionFactoryBuilder();
        initAfter(dsWrap);
    }

    protected MybatisAdapterFlex(BeanWrap dsWrap, Props dsProps) {
        super(dsWrap, dsProps);

        factoryBuilderPlus = new FlexSqlSessionFactoryBuilder();
        initAfter(dsWrap);
    }

    protected void initAfter(BeanWrap dsWrap) {
        globalConfig.setSqlSessionFactory(getFactory());
    }

    @Override
    protected DataSource getDataSource() {
        return new FlexDataSource(dsWrap.name(), dsWrap.raw());
    }

    @Override
    protected void initConfiguration(Environment environment) {
        //for configuration section
        config = new FlexConfiguration(environment);

        Props cfgProps = dsProps.getProp("configuration");
        if (cfgProps.size() > 0) {
            Utils.injectProperties(config, cfgProps);
        }


        //for globalConfig section
        globalConfig = new FlexGlobalConfig();
        globalConfig.setKeyConfig(new FlexGlobalConfig.KeyConfig());

        Props globalProps = dsProps.getProp("globalConfig");
        if (globalProps.size() > 0) {
            //尝试配置注入
            Utils.injectProperties(globalConfig, globalProps);
        }
        globalConfig.setConfiguration(config);

        FlexGlobalConfig.setConfig(environment.getId(), globalConfig, false);

        //增加事件扩展机制
        EventBus.push(globalConfig);


        if (dsWrap.typed()) {
            FlexGlobalConfig.setDefaultConfig(globalConfig);
        }
    }

    /**
     * 获取全局配置
     */
    public FlexGlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            factory = factoryBuilderPlus.build(getConfiguration());
        }

        return factory;
    }

    @Override
    public void injectTo(VarHolder varH) {
        super.injectTo(varH);

        // @Db("db1") FlexGlobalConfig globalConfig
        if (FlexGlobalConfig.class.isAssignableFrom(varH.getType())) {
            varH.setValue(this.getGlobalConfig());
            return;
        }

        // @Db("db1") RowMapperInvoker rowMapper
        if (RowMapperInvoker.class.equals(varH.getType())) {
            if (rowMapperInvoker == null) {
                rowMapperInvoker = new RowMapperInvoker(getFactory());
            }
            varH.setValue(rowMapperInvoker);
        }
    }

}
