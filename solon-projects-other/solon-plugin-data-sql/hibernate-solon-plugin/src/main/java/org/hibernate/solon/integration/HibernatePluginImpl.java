package org.hibernate.solon.integration;

import org.hibernate.solon.BaseMapperDefinition;
import org.hibernate.solon.annotation.Db;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

import javax.sql.DataSource;

/**
 * @author noear
 * @since 2.5
 */
public class HibernatePluginImpl implements Plugin {
    DbBeanBuilder dbBeanBuilder = new DbBeanBuilder();



    @Override
    public void start(AppContext context) throws Throwable {
        context.subWrapsOfType(DataSource.class, bw -> {
            HibernateAdapterManager.register(bw);
        });

        context.subBeansOfType(BaseMapperDefinition.class, b -> {
            dbBeanBuilder.register(b);
        });

        context.beanInjectorAdd(Db.class, new DbBeanInjector());
    }
}
