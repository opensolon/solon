package demo;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.sql.core.DefaultNameConversion;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.solon.annotation.Db;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class Config {
	/**
	 * 构建数据源
	 * */
    @Bean("db1")
    public DataSource db1(@Inject("${test.db1}") HikariDataSource dataSource) throws Exception{
       return dataSource;
    }

	/**
	 * 如果有需要，对 SQLManager 进行微调（一般，使用 yml 配置）
	 * */
	@Bean
	public void db1Init(@Db("db1")SQLManager sqlManager){
		sqlManager.setDbStyle(new MySqlStyle());
		sqlManager.setNc(new DefaultNameConversion());
	}
}
