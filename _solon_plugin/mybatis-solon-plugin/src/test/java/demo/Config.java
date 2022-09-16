package demo;

import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.BeanWrap;

import javax.sql.DataSource;

@Configuration
public class Config {
    @Bean("db1")
    public DataSource db1(@Inject("db1") DataSource dataSource) throws Exception {
        return dataSource;
    }

    @Bean("db1")
    public void db1Ds(@Inject("db1") BeanWrap bw) {
        if(bw == null){
            return;
        }
    }

    @Bean("db1")
    public void db1Adapter(@Db("db1") org.apache.ibatis.session.Configuration cfg) {
        cfg.addInterceptor(null);
    }
}
