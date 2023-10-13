package org.hibernate.solon.integration;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.internal.ThreadLocalSessionContext;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

/**
 * @author lingkang
 * @since 2.5
 */
public class HibernateConfiguration extends Configuration {

    public HibernateConfiguration(){

    }

    /**
     * 添加实体包扫描，有hibernate的@Table、@Entity
     */
    public HibernateConfiguration addScanPackage(String basePackage) {
        if (Utils.isNotEmpty(basePackage)) {
            Collection<Class<?>> classes = ResourceUtil.scanClasses(basePackage);
            for (Class<?> clazz : classes)
                addAnnotatedClass(clazz);
        }
        return this;
    }

    public HibernateConfiguration setDataSource(DataSource dataSource) {
        if (dataSource != null) {
            this.getProperties().put(AvailableSettings.DATASOURCE, dataSource);
        }
        return this;
    }

    public HibernateConfiguration setProperties(Properties properties) {
        if (properties != null) {
            properties.entrySet().forEach(obj -> {
                getProperties().put(obj.getKey(), obj.getValue());
            });
        }
        return this;
    }

    @Override
    public SessionFactory buildSessionFactory() throws HibernateException {
        getProperties().put(AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jdbc");
        getProperties().put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, ThreadLocalSessionContext.class.getName());

        SessionFactory sessionFactory = super.buildSessionFactory();
        return sessionFactory;
    }
}
