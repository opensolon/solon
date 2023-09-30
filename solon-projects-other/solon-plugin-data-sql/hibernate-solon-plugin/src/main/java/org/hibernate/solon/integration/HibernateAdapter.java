package org.hibernate.solon.integration;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.solon.jpa.RepositoryProxy;
import org.noear.solon.Solon;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.core.VarHolder;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * @author lingkang
 * @since 2.5
 */
public class HibernateAdapter {
    protected BeanWrap dsWrap;
    protected Props dsProps;

    protected HibernateConfiguration configuration;

    public HibernateAdapter(BeanWrap dsWrap) {
        this(dsWrap, Solon.cfg().getProp("hibernate"));
    }

    public HibernateAdapter(BeanWrap dsWrap, Props dsProps) {
        this.dsWrap = dsWrap;
        this.dsProps = dsProps;

        DataSource dataSource = getDataSource();

        configuration = new HibernateConfiguration();
        configuration.setDataSource(dataSource);

        initConfiguration();

        initDo();
    }

    protected DataSource getDataSource() {
        return dsWrap.raw();
    }

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory = getConfiguration().buildSessionFactory();
        }

        return sessionFactory;
    }

    public HibernateConfiguration getConfiguration() {
        return configuration;
    }


    public Object getMapper(Class<?> repositoryInterface) {
        return RepositoryProxy.newProxyInstance(getSessionFactory(), repositoryInterface);
    }

    protected void initConfiguration() {

    }

    protected void initDo() {
        //for mappers section
        dsProps.forEach((k, v) -> {
            if (k instanceof String && v instanceof String) {
                String key = (String) k;
                String valStr = (String) v;

                if (key.startsWith("scanPackages[") || key.equals("scanPackages")) {
                    for (String val : valStr.split(",")) {
                        val = val.trim();
                        if (val.length() == 0) {
                            continue;
                        }

                        getConfiguration().addScanPackage(val);
                    }
                }
            }
        });

        Props cfgProps = dsProps.getProp("config");
        getConfiguration().setProperties(cfgProps);
    }

    protected void injectTo(VarHolder varH) {
        if (SessionFactory.class.isAssignableFrom(varH.getType())) {
            varH.setValue(getSessionFactory());
        }

        if (Configuration.class.isAssignableFrom(varH.getType())) {
            varH.setValue(getConfiguration());
        }

        if (EntityManagerFactory.class.isAssignableFrom(varH.getType())) {
            varH.setValue(getSessionFactory());
        }
    }
}