package org.noear.solon.extend.mybatisplus.integration;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.mybatis.integration.MybatisAdapterDefault;
import java.util.Properties;

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
    }

    /**
     * 构建Sql工厂适配器，使用属性配置
     */
    protected MybatisAdapterPlus(BeanWrap dsWrap, Properties props) {
        super(dsWrap, props);

        this.factoryBuilderPlus = new MybatisSqlSessionFactoryBuilder();

        Aop.getAsyn(MybatisSqlSessionFactoryBuilder.class, bw -> {
            factoryBuilderPlus = bw.raw();
        });
    }

    /**
     * 初始化配置
     */
    @Override
    protected void initConfiguration(Environment environment) {
        config = new MybatisConfiguration(environment);
    }

    /**
     * 获取会话工厂
     */
    @Override
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            factory = factoryBuilderPlus.build(getConfiguration());
            globalConfig = GlobalConfigUtils.getGlobalConfig(getConfiguration());
        }

        return factory;
    }

    /**
     * 获取全局配置
     * */
    public GlobalConfig getGlobalConfig() {
        if (globalConfig == null) {
            getFactory();
        }

        return globalConfig;
    }
}
