package org.noear.solon.extend.mybatis;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

public class SqlSessionFactoryBean {
    protected Configuration configuration;

    public SqlSessionFactoryBean(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("solon", transactionFactory, dataSource);
        configuration = new Configuration(environment);
    }

    public void addTypeAlias(String packageName) {
        //添加 typeAliases //例："webapp.model"
        configuration.getTypeAliasRegistry().registerAliases(packageName);
    }

    public void addMappers(String packageName) {
        //添加 mappers //例："webapp.dso.db"
        configuration.addMappers(packageName);
    }

    public SqlSessionFactory getObject() {
        return new SqlSessionFactoryBuilder().build(configuration);
    }
}
