package org.noear.solon.extend.mybatis;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class SqlSessionFactoryBean {
    protected Configuration config;
    protected SqlSessionFactory factory;

    public SqlSessionFactoryBean(DataSource dataSource) {
        this(dataSource,null);
    }

    public SqlSessionFactoryBean(DataSource dataSource, Properties props) {
        TransactionFactory tf = new JdbcTransactionFactory();
        Environment envi = new Environment("solon", tf, dataSource);
        config = new Configuration(envi);

        if (props != null) {
            String tmp = props.getProperty("typeAliases.package");
            if (tmp != null) {
                cfg().getTypeAliasRegistry().registerAliases(tmp);
            }

            tmp = props.getProperty("mappers.package");
            if (tmp != null) {
                cfg().addMappers(tmp);
            }
        }
    }

    public Configuration cfg() {
        return config;
    }

    public SqlSessionFactory getObject() {
        if (factory == null) {
            factory = new SqlSessionFactoryBuilder().build(config);
        }

        return factory;
    }
}
