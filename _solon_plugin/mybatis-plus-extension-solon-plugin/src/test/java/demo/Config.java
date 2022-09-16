package demo;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.solon.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.solon.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear 2022/3/31 created
 */
@Configuration
public class Config {
    @Bean
    public Interceptor plusInterceptor() {
        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
        plusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return plusInterceptor;
    }
}
