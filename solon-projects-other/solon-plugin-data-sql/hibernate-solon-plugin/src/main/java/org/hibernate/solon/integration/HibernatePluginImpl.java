package org.hibernate.solon.integration;

import org.hibernate.solon.annotation.Db;
import org.hibernate.solon.annotation.HibernateTran;
import org.hibernate.solon.jpa.SolonPersistenceProvider;
import org.noear.solon.Solon;
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

        context.subWrapsOfType(DataSource.class, HibernateAdapterManager::register);

        context.beanInjectorAdd(Db.class, new DbBeanInjector());

        //添加增强事务控制支持
        if (Solon.app().enableTransaction()) {
            context.beanInterceptorAdd(HibernateTran.class, new TranAdapterInterceptor(), 121);
        }
    }
}
