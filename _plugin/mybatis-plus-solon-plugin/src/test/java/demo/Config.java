package demo;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.ext.solon.Db;
import org.apache.ibatis.reflection.MetaObject;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.extend.mybatisplus.integration.MybatisAdapterPlus;

import javax.sql.DataSource;

@Configuration
public class Config {
    @Bean("db1")
    public DataSource db1(@Inject("dataSource.db1") DataSource dataSource) {
        return dataSource;
    }

//    @Bean
//    public Interceptor plusInterceptor() {
//        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
//        plusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        return plusInterceptor;
//    }

    @Bean
    public void db1_ext(@Db("db1") MybatisAdapterPlus adapter) {
        adapter.getGlobalConfig()
                .setMetaObjectHandler(new MetaObjectHandler() {
                    @Override
                    public void insertFill(MetaObject metaObject) {

                    }

                    @Override
                    public void updateFill(MetaObject metaObject) {

                    }
                });
    }
}
