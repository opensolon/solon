package demo.dso;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.session.Configuration;

/**
 * @author noear 2021/9/9 created
 */
public class PaginationConfig {
    public static void init(Configuration e){
        //添加 mybatis-plug 分页支持
        //
        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
        plusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        e.addInterceptor(plusInterceptor);
    }
}
