package org.noear.solon.extend.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;

public class SqlSessionFactoryBean {
    protected Configuration config;
    protected SqlSessionFactory factory;

    public SqlSessionFactoryBean(DataSource dataSource) {
        TransactionFactory tf = new JdbcTransactionFactory();
        Environment envi = new Environment("solon", tf, dataSource);
        config = new Configuration(envi);
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

    /**
     * @param resource 例："mybatis.xml"
     * */
    public static SqlSessionFactory getObjectByXml(String resource) throws IOException {
        Reader reader = Resources.getResourceAsReader(resource);
        return new SqlSessionFactoryBuilder().build(reader);
    }

}
