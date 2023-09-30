package org.hibernate.solon.integration;

import org.hibernate.solon.annotation.Db;
import org.hibernate.solon.jpa.SolonPersistenceProvider;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

import javax.persistence.spi.PersistenceProviderResolverHolder;
import javax.sql.DataSource;

/**
 * @author noear
 * @since 2.5
 */
public class HibernatePluginImpl implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        //支持 jpa
        PersistenceProviderResolverHolder
                .getPersistenceProviderResolver()
                .getPersistenceProviders()
                .add(new SolonPersistenceProvider());

        context.subWrapsOfType(DataSource.class, bw -> {
            HibernateAdapterManager.register(bw);
        });

        context.beanInjectorAdd(Db.class, new DbBeanInjector());
        context.beanBuilderAdd(Db.class, new DbBeanBuilder());
    }
}
