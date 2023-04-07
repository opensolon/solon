package com.mybatisflex.solon.integration;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.core.VarHolder;
import org.apache.ibatis.solon.integration.MybatisAdapterDefault;

/**
 * 适配器 for mybatis-flex
 *
 * @author noear
 * @since 2.2
 */
public class MybatisAdapterFlex extends MybatisAdapterDefault {
    MybatisFlexBootstrap mybatisFlexBootstrap = new MybatisFlexBootstrap();

    protected MybatisAdapterFlex(BeanWrap dsWrap) {
        super(dsWrap);
    }

    protected MybatisAdapterFlex(BeanWrap dsWrap, Props dsProps) {
        super(dsWrap, dsProps);
    }

    @Override
    protected void initConfiguration(Environment environment) {
        //for configuration section
        //mybatisFlexBootstrap.setConfiguration()
        FlexConfiguration flexConfiguration = new FlexConfiguration(environment);

        Props cfgProps = dsProps.getProp("configuration");
        if (cfgProps.size() > 0) {
            Utils.injectProperties(config, cfgProps);
        }

        config = flexConfiguration;
        mybatisFlexBootstrap
                .setConfiguration(flexConfiguration)
                .setDataSource(dsWrap.get());
    }

    @Override
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            mybatisFlexBootstrap.start();
            factory = mybatisFlexBootstrap.getSqlSessionFactory();
        }

        return factory;
    }

    @Override
    public void injectTo(VarHolder varH) {
        super.injectTo(varH);

        //@Db("db1") SqlSessionFactory factory;
    }
}
