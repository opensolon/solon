package org.noear.solon.extend.sqltoy.configure;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.extend.sqltoy.SqlToyCRUDServiceForSolon;
import org.sagacity.sqltoy.SqlToyContext;
import org.sagacity.sqltoy.config.model.ElasticEndpoint;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.dao.impl.SqlToyLazyDaoImpl;
import org.sagacity.sqltoy.plugins.IUnifyFieldsHandler;
import org.sagacity.sqltoy.plugins.TypeHandler;
import org.sagacity.sqltoy.plugins.datasource.ConnectionFactory;
import org.sagacity.sqltoy.plugins.datasource.DataSourceSelector;
import org.sagacity.sqltoy.service.SqlToyCRUDService;
import org.sagacity.sqltoy.translate.cache.TranslateCacheManager;
import org.sagacity.sqltoy.utils.StringUtil;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.err;

@Configuration
public class SqltoyConfiguration {

    private ApplicationContext applicationContext = new ApplicationContext(){};

    @Inject("${sqltoy}")
    private SqlToyContextProperties properties;


    @Init
    public void init(){
        SolonApp app= Solon.global();
        if(app.cfg().isDebugMode()){
            properties.setDebug(true);
        }

    }
    // 增加一个辅助校验,避免不少新用户将spring.sqltoy开头写成sqltoy.开头
    // @Inject("${sqltoy.sqlResourcesDir:}")
    private String sqlResourcesDir;

    public void setSqlResourcesDir(String sqlResourcesDir) {
        this.sqlResourcesDir = sqlResourcesDir;
    }

    // 构建sqltoy上下文,并指定初始化方法和销毁方法
   // @Bean(name = "sqlToyContext", initMethod = "initialize", destroyMethod = "destroy")
   // @ConditionalOnMissingBean
   @Bean(name = "sqlToyContext")
   public SqlToyContext sqlToyContext() throws Exception {
        // 用辅助配置来校验是否配置错误
        if (StringUtil.isBlank(properties.getSqlResourcesDir()) && StringUtil.isNotBlank(sqlResourcesDir)) {
            throw new IllegalArgumentException(
                    "请检查sqltoy配置,是spring.sqltoy作为前缀,而不是sqltoy!\n正确范例: spring.sqltoy.sqlResourcesDir=classpath:com/sagframe/modules");
        }
        SqlToyContext sqlToyContext = new SqlToyContext();
        // 当发现有重复sqlId时是否抛出异常，终止程序执行
        sqlToyContext.setBreakWhenSqlRepeat(properties.isBreakWhenSqlRepeat());
        // sql 文件资源路径
        sqlToyContext.setSqlResourcesDir(properties.getSqlResourcesDir());
        if (properties.getSqlResources() != null && properties.getSqlResources().length > 0) {
            List<String> resList = new ArrayList<String>();
            for (String prop : properties.getSqlResources()) {
                resList.add(prop);
            }
            sqlToyContext.setSqlResources(resList);
        }
        // sql文件解析的编码格式,默认utf-8
        if (properties.getEncoding() != null) {
            sqlToyContext.setEncoding(properties.getEncoding());
        }

        // sqltoy 已经无需指定扫描pojo类,已经改为用的时候动态加载
        // pojo 扫描路径,意义不存在
        if (properties.getPackagesToScan() != null) {
            sqlToyContext.setPackagesToScan(properties.getPackagesToScan());
        }

        // 特定pojo类加载，意义已经不存在
        if (properties.getAnnotatedClasses() != null) {
            sqlToyContext.setAnnotatedClasses(properties.getAnnotatedClasses());
        }

        // 批量操作时(saveAll、updateAll)，每批次数量,默认200
        if (properties.getBatchSize() != null) {
            sqlToyContext.setBatchSize(properties.getBatchSize());
        }
        // 默认数据库fetchSize
        if (properties.getFetchSize() > 0) {
            sqlToyContext.setFetchSize(properties.getFetchSize());
        }
        // 分页查询单页最大记录数量(默认50000)
        if (properties.getPageFetchSizeLimit() != null) {
            sqlToyContext.setPageFetchSizeLimit(properties.getPageFetchSizeLimit());
        }

        // sql 检测间隔时长(单位秒)
        if (properties.getScriptCheckIntervalSeconds() != null) {
            sqlToyContext.setScriptCheckIntervalSeconds(properties.getScriptCheckIntervalSeconds());
        }

        // 缓存、sql文件在初始化后延时多少秒开始检测
        if (properties.getDelayCheckSeconds() != null) {
            sqlToyContext.setDelayCheckSeconds(properties.getDelayCheckSeconds());
        }

        // 是否debug模式
        if (properties.getDebug() != null) {
            sqlToyContext.setDebug(properties.getDebug());
        }

        // sql执行超过多长时间则打印提醒(默认30秒)
        if (properties.getPrintSqlTimeoutMillis() != null) {
            sqlToyContext.setPrintSqlTimeoutMillis(properties.getPrintSqlTimeoutMillis());
        }

        // sql函数转换器
        if (properties.getFunctionConverts() != null) {
            sqlToyContext.setFunctionConverts(properties.getFunctionConverts());
        }

        // 缓存翻译配置
        if (properties.getTranslateConfig() != null) {
            sqlToyContext.setTranslateConfig(properties.getTranslateConfig());
        }

        // 数据库保留字
        if (properties.getReservedWords() != null) {
            sqlToyContext.setReservedWords(properties.getReservedWords());
        }
        // 分页页号超出总页时转第一页，否则返回空集合
        sqlToyContext.setPageOverToFirst(properties.isPageOverToFirst());
        // 数据库方言
        sqlToyContext.setDialect(properties.getDialect());
        // sqltoy内置参数默认值修改
        sqlToyContext.setDialectConfig(properties.getDialectConfig());

        // update 2021-01-18 设置缓存类别,默认ehcache
        sqlToyContext.setCacheType(properties.getCacheType());
        // 设置公共统一属性的处理器
        String unfiyHandler = properties.getUnifyFieldsHandler();
        if (StringUtil.isNotBlank(unfiyHandler)) {
            try {
                IUnifyFieldsHandler handler = null;
                // 类
                if (unfiyHandler.contains(".")) {
                    handler = (IUnifyFieldsHandler) Class.forName(unfiyHandler).getDeclaredConstructor().newInstance();
                } // spring bean名称
                else if (applicationContext.containsBean(unfiyHandler)) {
                    handler = (IUnifyFieldsHandler) applicationContext.getBean(unfiyHandler);
                    if (handler == null) {
                        throw new ClassNotFoundException("项目中未定义unifyFieldsHandler=" + unfiyHandler + " 对应的bean!");
                    }
                }
                if (handler != null) {
                    sqlToyContext.setUnifyFieldsHandler(handler);
                }
            } catch (ClassNotFoundException cne) {
                err.println("------------------- 错误提示 ------------------------------------------- ");
                err.println("spring.sqltoy.unifyFieldsHandler=" + unfiyHandler + " 对应类不存在,错误原因:");
                err.println("--1.您可能直接copy了参照项目的配置文件,但没有将具体的类也同步copy过来!");
                err.println("--2.如您并不需要此功能，请将配置文件中注释掉spring.sqltoy.unifyFieldsHandler");
                err.println("------------------------------------------------");
                cne.printStackTrace();
                throw cne;
            }
        }

        // 设置elastic连接
        Elastic es = properties.getElastic();
        if (es != null && es.getEndpoints() != null && !es.getEndpoints().isEmpty()) {
            sqlToyContext.setDefaultElastic(es.getDefaultId());
            List<ElasticEndpoint> endpoints = new ArrayList<ElasticEndpoint>();
            for (ElasticConfig esconfig : es.getEndpoints()) {
                ElasticEndpoint ep = new ElasticEndpoint(esconfig.getUrl(), esconfig.getSqlPath());
                ep.setId(esconfig.getId());
                if (esconfig.getCharset() != null) {
                    ep.setCharset(esconfig.getCharset());
                }
                if (esconfig.getRequestTimeout() != null) {
                    ep.setRequestTimeout(esconfig.getRequestTimeout());
                }
                if (esconfig.getConnectTimeout() != null) {
                    ep.setConnectTimeout(esconfig.getConnectTimeout());
                }
                if (esconfig.getSocketTimeout() != null) {
                    ep.setSocketTimeout(esconfig.getSocketTimeout());
                }
                ep.setAuthCaching(esconfig.isAuthCaching());
                ep.setUsername(esconfig.getUsername());
                ep.setPassword(esconfig.getPassword());
                ep.setKeyStore(esconfig.getKeyStore());
                ep.setKeyStorePass(esconfig.getKeyStorePass());
                ep.setKeyStoreSelfSign(esconfig.isKeyStoreSelfSign());
                ep.setKeyStoreType(esconfig.getKeyStoreType());
                endpoints.add(ep);
            }
            // 这里已经完成了当没有设置默认节点时将第一个节点作为默认节点
            sqlToyContext.setElasticEndpoints(endpoints);
        }
        // 设置默认数据库
        if (properties.getDefaultDataSource() != null) {
            sqlToyContext.setDefaultDataSourceName(properties.getDefaultDataSource());
        }

        // 自定义缓存实现管理器
        String translateCacheManager = properties.getTranslateCacheManager();
        if (StringUtil.isNotBlank(translateCacheManager)) {
            // 缓存管理器的bean名称
            if (applicationContext.containsBean(translateCacheManager)) {
                sqlToyContext.setTranslateCacheManager(
                        (TranslateCacheManager) applicationContext.getBean(translateCacheManager));
            } // 包名和类名称
            else if (translateCacheManager.contains(".")) {
                sqlToyContext.setTranslateCacheManager((TranslateCacheManager) Class.forName(translateCacheManager)
                        .getDeclaredConstructor().newInstance());
            }
        }

        // 自定义typeHandler
        String typeHandler = properties.getTypeHandler();
        if (StringUtil.isNotBlank(typeHandler)) {
            if (applicationContext.containsBean(typeHandler)) {
                sqlToyContext.setTypeHandler((TypeHandler) applicationContext.getBean(typeHandler));
            } // 包名和类名称
            else if (typeHandler.contains(".")) {
                sqlToyContext.setTypeHandler(
                        (TypeHandler) Class.forName(typeHandler).getDeclaredConstructor().newInstance());
            }
        }

        // 自定义数据源选择器
        String dataSourceSelector = properties.getDataSourceSelector();
        if (StringUtil.isNotBlank(dataSourceSelector)) {
            if (applicationContext.containsBean(dataSourceSelector)) {
                sqlToyContext
                        .setDataSourceSelector((DataSourceSelector) applicationContext.getBean(dataSourceSelector));
            } // 包名和类名称
            else if (dataSourceSelector.contains(".")) {
                sqlToyContext.setDataSourceSelector(
                        (DataSourceSelector) Class.forName(dataSourceSelector).getDeclaredConstructor().newInstance());
            }
        }

        // 自定义数据库连接获取和释放的接口实现
        String connectionFactory = properties.getConnectionFactory();
        if (StringUtil.isNotBlank(connectionFactory)) {
            if (applicationContext.containsBean(connectionFactory)) {
                sqlToyContext.setConnectionFactory((ConnectionFactory) applicationContext.getBean(connectionFactory));
            } // 包名和类名称
            else if (connectionFactory.contains(".")) {
                sqlToyContext.setConnectionFactory(
                        (ConnectionFactory) Class.forName(connectionFactory).getDeclaredConstructor().newInstance());
            }
        }
        return sqlToyContext;
    }

    /**
     *
     * @return 返回预定义的通用Dao实例
     */
    @Bean(name = "sqlToyLazyDao")
    //@ConditionalOnMissingBean
    public SqlToyLazyDao sqlToyLazyDao() {
        return new SqlToyLazyDaoImpl();
    }

    /**
     *
     * @return 返回预定义的通用CRUD service实例
     */
    @Bean(name = "sqlToyCRUDService")
    //@ConditionalOnMissingBean
    public SqlToyCRUDService sqlToyCRUDService() {
        return new SqlToyCRUDServiceForSolon();
    }

}
