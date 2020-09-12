package org.beetl.sql.ext.solon.test.dynamic;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.annotation.XInject;

import javax.sql.DataSource;

@XConfiguration
public class DataSourceConfig {

    @XBean(value = "ds1", typed =true)
    public DataSource ds1(@XInject("${db1}") HikariDataSource ds) {
        return ds;
    }

    @XBean("ds2")
    public DataSource ds2(@XInject("${db1}") HikariDataSource ds) {
        return ds;
    }
}
