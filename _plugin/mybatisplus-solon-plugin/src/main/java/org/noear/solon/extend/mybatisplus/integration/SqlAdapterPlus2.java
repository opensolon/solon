package org.noear.solon.extend.mybatisplus.integration;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.mybatis.integration.SqlAdapterDefault;
import java.util.Properties;

/**
 * 适配器2
 * <p>
 * 1.提供 mapperScan 能力
 * 2.生成 factory 的能力
 *
 * @author noear, iYarnFog
 * @since 1.5
 */
class SqlAdapterPlus2 extends SqlAdapterDefault {

    /**
     * 构建Sql工厂适配器，使用默认的 typeAliases 和 mappers 配置
     */
    public SqlAdapterPlus2(BeanWrap dsWrap) {
        super(dsWrap);
    }

    /**
     * 构建Sql工厂适配器，使用属性配置
     */
    public SqlAdapterPlus2(BeanWrap dsWrap, Properties props) {
        super(dsWrap, props);
    }

    /**
     * 初始化配置
     * */
    @Override
    protected void initConfiguration(Environment environment) {
        config = new MybatisConfiguration(environment);
    }

    /**
     * 获取会话工厂
     */
    @Override
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            factory = new MybatisSqlSessionFactoryBuilder().build(config);
        }

        return factory;
    }
}
