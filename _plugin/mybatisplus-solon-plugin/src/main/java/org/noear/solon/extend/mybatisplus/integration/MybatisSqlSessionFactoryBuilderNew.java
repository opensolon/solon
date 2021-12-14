package org.noear.solon.extend.mybatisplus.integration;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.SqlRunnerInjector;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @author noear
 * @since 1.6
 */
public class MybatisSqlSessionFactoryBuilderNew extends MybatisSqlSessionFactoryBuilder {
    @Override
    public SqlSessionFactory build(Configuration configuration) {
        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);
        Object identifierGenerator;
        if (null == globalConfig.getIdentifierGenerator()) {
            identifierGenerator = new DefaultIdentifierGenerator();
            globalConfig.setIdentifierGenerator((IdentifierGenerator)identifierGenerator);
        } else {
            identifierGenerator = globalConfig.getIdentifierGenerator();
        }

        IdWorker.setIdentifierGenerator((IdentifierGenerator)identifierGenerator);
        if (globalConfig.isEnableSqlRunner()) {
            (new SqlRunnerInjector()).inject(configuration);
        }

        SqlSessionFactory sqlSessionFactory = super.build(configuration);
        globalConfig.setSqlSessionFactory(sqlSessionFactory);

        //globalConfig.setMetaObjectHandler(new MetaObjectHandlerImpl());

        return sqlSessionFactory;
    }
}
