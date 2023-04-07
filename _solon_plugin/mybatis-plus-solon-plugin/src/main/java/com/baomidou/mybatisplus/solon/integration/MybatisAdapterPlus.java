package com.baomidou.mybatisplus.solon.integration;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.apache.ibatis.solon.integration.MybatisAdapterDefault;

/**
 * 适配器 for mybatis-plus
 * <p>
 * 1.提供 mapperScan 能力
 * 2.生成 factory 的能力
 *
 * @author noear, iYarnFog
 * @since 1.5
 */
public class MybatisAdapterPlus extends MybatisAdapterDefault {

    MybatisSqlSessionFactoryBuilder factoryBuilderPlus;
    GlobalConfig globalConfig;

    /**
     * 构建Sql工厂适配器，使用默认的 typeAliases 和 mappers 配置
     */
    protected MybatisAdapterPlus(BeanWrap dsWrap) {
        super(dsWrap);

        this.factoryBuilderPlus = new MybatisSqlSessionFactoryBuilder();

        dsWrap.context().getBeanAsync(MybatisSqlSessionFactoryBuilder.class, bean -> {
            factoryBuilderPlus = bean;
        });
    }

    /**
     * 构建Sql工厂适配器，使用属性配置
     */
    protected MybatisAdapterPlus(BeanWrap dsWrap, Props dsProps) {
        super(dsWrap, dsProps);

        this.factoryBuilderPlus = new MybatisSqlSessionFactoryBuilder();

        dsWrap.context().getBeanAsync(MybatisSqlSessionFactoryBuilder.class, bean -> {
            factoryBuilderPlus = bean;
        });
    }

    /**
     * 初始化配置
     */
    @Override
    protected void initConfiguration(Environment environment) {
        //for configuration section
        config = new MybatisConfiguration(environment);

        Props cfgProps = dsProps.getProp("configuration");
        if (cfgProps.size() > 0) {
            Utils.injectProperties(config, cfgProps);
        }


        //for globalConfig section
        globalConfig = new GlobalConfig().setDbConfig(new GlobalConfig.DbConfig());

        Props globalProps = dsProps.getProp("globalConfig");
        if (globalProps.size() > 0) {
            //尝试配置注入
            Utils.injectProperties(globalConfig, globalProps);
        }

        GlobalConfigUtils.setGlobalConfig(config, globalConfig);
    }

    /**
     * 获取会话工厂
     */
    @Override
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            factory = factoryBuilderPlus.build(getConfiguration());
        }

        return factory;
    }

    /**
     * 获取全局配置
     * */
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public void injectTo(VarHolder varH) {
        super.injectTo(varH);

        //@Db("db1") SqlSessionFactory factory;
        if (GlobalConfig.class.isAssignableFrom(varH.getType())) {
            varH.setValue(this.getGlobalConfig());
            return;
        }
    }
}
