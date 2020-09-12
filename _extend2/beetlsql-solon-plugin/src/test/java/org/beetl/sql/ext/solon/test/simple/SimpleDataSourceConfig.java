package org.beetl.sql.ext.solon.test.simple;


import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.annotation.XInject;

import javax.sql.DataSource;

@XConfiguration
public class SimpleDataSourceConfig {

    @XBean
    public DataSource datasource(@XInject("${db1}") HikariDataSource ds) {
        return ds;
    }
}