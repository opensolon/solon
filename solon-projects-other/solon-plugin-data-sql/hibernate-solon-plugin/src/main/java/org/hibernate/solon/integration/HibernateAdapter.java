package org.hibernate.solon.integration;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.noear.solon.Solon;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.util.ResourceUtil;

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
        this(dsWrap, Solon.cfg().getProp("jpa"));
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
        if (sessionFactory == null) {
            sessionFactory = getConfiguration().buildSessionFactory();
        }

        return sessionFactory;
    }

    public HibernateConfiguration getConfiguration() {
        return configuration;
    }


    /**
     * @author bai
     * */
    protected void initConfiguration() {
        // 默认兼容 hibernate.cfg.xml
        if (ResourceUtil.hasResource(null, StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME)){
            configuration.configure(StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME );
        }
        // 加载hibernate常规设置
        getConfiguration().setProperties(this.dsProps.getProp("properties"));
    }

    protected void initDo() {
        //for mappers section
        dsProps.forEach((k, v) -> {
            if (k instanceof String && v instanceof String) {
                String key = (String) k;
                String valStr = (String) v;

                if (key.startsWith("mappings[") || key.equals("mappings")) {
                    for (String val : valStr.split(",")) {
                        val = val.trim();
                        if (val.length() == 0) {
                            continue;
                        }
                        getConfiguration().addMapping(val);
                    }
                }
            }
        });

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