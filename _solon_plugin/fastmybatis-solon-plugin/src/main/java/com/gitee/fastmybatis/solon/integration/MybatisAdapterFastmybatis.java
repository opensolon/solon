package com.gitee.fastmybatis.solon.integration;

import com.gitee.fastmybatis.core.FastmybatisConfig;
import com.gitee.fastmybatis.core.ext.MapperLocationsBuilder;
import com.gitee.fastmybatis.core.ext.MyBatisResource;
import com.gitee.fastmybatis.core.util.DbUtil;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.solon.integration.MybatisAdapterDefault;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * 适配器 for fastmybatis
 *
 * @author thc
 */
public class MybatisAdapterFastmybatis extends MybatisAdapterDefault {
    protected static final String CONFIG_LOCATION_DEFAULT = "mybatis/mybatis-config-default.xml";

    protected String configLocation = CONFIG_LOCATION_DEFAULT;

    protected FastmybatisConfig fastmybatisConfig;

    protected MapperLocationsBuilder mapperLocationsBuilder;

    protected List<String> mapperResources;

    /**
     * 构建Sql工厂适配器，使用默认的 typeAliases 和 mappers 配置
     */
    protected MybatisAdapterFastmybatis(BeanWrap dsWrap) {
        super(dsWrap);
    }

    /**
     * 构建Sql工厂适配器，使用属性配置
     */
    protected MybatisAdapterFastmybatis(BeanWrap dsWrap, Props dsProps) {
        super(dsWrap, dsProps);
    }

    @Override
    protected void initConfiguration(Environment environment) {
        super.initConfiguration(environment);
        this.fastmybatisConfig = new FastmybatisConfig();
        this.mapperLocationsBuilder = new MapperLocationsBuilder(fastmybatisConfig);
        this.mapperResources = new ArrayList<>(8);
        this.configLocation = dsProps.get("configLocation", CONFIG_LOCATION_DEFAULT);
        List<String> mappers = dsProps.getList("mappers");
        if (mappers == null || mappers.isEmpty()) {
            throw new IllegalArgumentException("yml文件缺少 mappers 参数");
        }
        Utils.injectProperties(fastmybatisConfig, dsProps);
    }


    @Override
    protected void addMapperByXml(String uri) {
        this.mapperResources.add(uri);
    }

    /**
     * 获取会话工厂
     */
    @Override
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            try {
                this.initXml(config);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            factory = factoryBuilder.build(getConfiguration());
        }
        return factory;
    }

    protected void initXml(Configuration configuration) throws IOException {
        List<MyBatisResource> myBatisResources = new ArrayList<>(16);
        if (mapperResources != null) {
            for (String mapperResource : mapperResources) {
                MyBatisResource myBatisResource = MyBatisResource.buildFromClasspath(mapperResource);
                myBatisResources.add(myBatisResource);
            }
        }
        Objects.requireNonNull(fastmybatisConfig);

        DataSource dataSource = this.getDataSource();
        String dialect = DbUtil.getDialect(dataSource);
        Collection<Class<?>> mapperClasses = config.getMapperRegistry().getMappers();
        MyBatisResource[] allMybatisMapperResources = mapperLocationsBuilder.build(new HashSet<>(mapperClasses), myBatisResources, dialect);
        for (MyBatisResource myBatisResource : allMybatisMapperResources) {
            try (InputStream inputStream = myBatisResource.getInputStream()) {
                String resource = myBatisResource.getFilepath();
                if (resource == null) {
                    resource = myBatisResource.getFilename();
                }
                XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                mapperParser.parse();
            }
        }
    }


}
