package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.data.annotation.Ds;

import javax.sql.DataSource;


/**
 * @author noear 2025/8/27 created
 */
@Configuration
public class DsConfig {
    @Bean
    public void dsCfg(@Ds("ds1") DataSource ds) {
        //ds.setUserName(xxx); //修改
    }
}
