package org.beetl.sql.ext.solon.test.masterslave;


import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XConfiguration;
import org.noear.solon.annotation.XInject;

import javax.sql.DataSource;

@XConfiguration
public class MasterDataSourceConfig {

    @XBean(tags = "slaveDs1,slaveDs2")
    public DataSource master(@XInject("${db1}") HikariDataSource ds) {
        return ds;
    }

    @XBean(value = "slaveDs1")
    public DataSource slaveDs1(@XInject("${db1}") HikariDataSource ds) {
        return ds;
    }

    @XBean(value = "slaveDs2")
    public DataSource slaveDs2(@XInject("${db1}") HikariDataSource ds) {
        return ds;
    }
}

