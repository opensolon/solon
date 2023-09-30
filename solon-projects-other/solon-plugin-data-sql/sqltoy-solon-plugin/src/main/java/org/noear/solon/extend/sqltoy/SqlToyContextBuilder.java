package org.noear.solon.extend.sqltoy;

import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.extend.sqltoy.configure.Elastic;
import org.noear.solon.extend.sqltoy.configure.ElasticConfig;
import org.noear.solon.extend.sqltoy.configure.SqlToyContextProperties;
import org.noear.solon.extend.sqltoy.configure.SqlToyContextTaskPoolProperties;
import org.noear.solon.extend.sqltoy.impl.SolonConnectionFactory;
import org.sagacity.sqltoy.SqlToyContext;
import org.sagacity.sqltoy.config.model.ElasticEndpoint;
import org.sagacity.sqltoy.integration.AppContext;
import org.sagacity.sqltoy.integration.ConnectionFactory;
import org.sagacity.sqltoy.plugins.*;
import org.sagacity.sqltoy.plugins.datasource.DataSourceSelector;
import org.sagacity.sqltoy.plugins.formater.SqlFormater;
import org.sagacity.sqltoy.plugins.secure.DesensitizeProvider;
import org.sagacity.sqltoy.plugins.secure.FieldsSecureProvider;
import org.sagacity.sqltoy.translate.cache.TranslateCacheManager;
import org.sagacity.sqltoy.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.err;

/**
 * SqlToyContext 构建器（用于拆分 XPluginImp 类）
 *
 * @author 夜の孤城
 * @author noear
 * @since 1.8
 */
class SqlToyContextBuilder {
    SqlToyContextProperties properties;
    AppContext appContext;
    public SqlToyContextBuilder(SqlToyContextProperties properties, AppContext appContext){
        this.properties = properties;
        this.appContext = appContext;
    }

    public SqlToyContext build() throws Exception {
        if (StringUtil.isBlank(properties.getSqlResourcesDir())) {
            properties.setSqlResourcesDir("classpath:sqltoy");
            // throw new IllegalArgumentException(
            //        "请检查sqltoy配置,是sqltoy作为前缀,而不是spring.sqltoy!\n正确范例: sqltoy.sqlResourcesDir=classpath:com/sagframe/modules");
        }

        SqlToyContext sqlToyContext = new SqlToyContext();

        // --------5.2 变化的地方----------------------------------
        // 注意appContext注入非常关键

        sqlToyContext.setAppContext(appContext);

        // 设置默认spring的connectFactory
        sqlToyContext.setConnectionFactory(new SolonConnectionFactory());

        // 分布式id产生器实现类
        // sqlToyContext.setDistributeIdGeneratorClass("org.sagacity.sqltoy.integration.impl.SpringRedisIdGenerator");
        sqlToyContext.setDistributeIdGeneratorClass("org.noear.solon.extend.sqltoy.impl.SolonRedisIdGenerator");

        // 针对Caffeine缓存指定实现类型
        // sqlToyContext.setTranslateCaffeineManagerClass("org.sagacity.sqltoy.translate.cache.impl.TranslateCaffeineManager");
        // caffeine 缓存转由solon cache代理
        sqlToyContext.setTranslateCaffeineManagerClass("org.noear.solon.extend.sqltoy.translate.SolonTranslateCacheManager");
        // 注入spring的默认mongoQuery实现类
        // sqlToyContext.setMongoQueryClass("org.sagacity.sqltoy.integration.impl.SpringMongoQuery");
        sqlToyContext.setMongoQueryClass("org.noear.solon.extend.sqltoy.impl.SolonMongoQuery");
        // --------end 5.2 -----------------------------------------

        // 当发现有重复sqlId时是否抛出异常，终止程序执行
        sqlToyContext.setBreakWhenSqlRepeat(properties.isBreakWhenSqlRepeat());

        // 开放设置默认单页记录数量
        sqlToyContext.setDefaultPageSize(properties.getDefaultPageSize());
        sqlToyContext.setDefaultPageOffset(properties.isDefaultPageOffset());

        // map 类型结果label是否自动转驼峰处理
        if (properties.getHumpMapResultTypeLabel() != null) {
            sqlToyContext.setHumpMapResultTypeLabel(properties.getHumpMapResultTypeLabel());
        }

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
        // 指定需要进行产品化跨数据库查询验证的数据库
        if (properties.getRedoDataSources() != null) {
            sqlToyContext.setRedoDataSources(properties.getRedoDataSources());
        }
        // 数据库方言
        sqlToyContext.setDialect(properties.getDialect());
        // sqltoy内置参数默认值修改
        sqlToyContext.setDialectConfig(properties.getDialectConfig());

        // update 2021-01-18 设置缓存类别,默认ehcache
        sqlToyContext.setCacheType(properties.getCacheType());
        sqlToyContext.setExecuteSqlBlankToNull(properties.isExecuteSqlBlankToNull());
        // 是否拆分merge into 为updateAll 和 saveAllIgnoreExist 两步操作(1、seata分布式事务不支持merge)
        sqlToyContext.setSplitMergeInto(properties.isSplitMergeInto());
        // getMetaData().getColumnLabel(i) 结果做大小写处理策略
        if (null != properties.getColumnLabelUpperOrLower()) {
            sqlToyContext.setColumnLabelUpperOrLower(properties.getColumnLabelUpperOrLower().toLowerCase());
        }
        sqlToyContext.setSecurePrivateKey(properties.getSecurePrivateKey());
        sqlToyContext.setSecurePublicKey(properties.getSecurePublicKey());

        // 修改多少条记录做特别提示
        sqlToyContext.setUpdateTipCount(properties.getUpdateTipCount());
        if (properties.getOverPageToFirst() != null) {
            sqlToyContext.setOverPageToFirst(properties.getOverPageToFirst());
        }
        // 设置公共统一属性的处理器
        String unfiyHandler = properties.getUnifyFieldsHandler();
        if (StringUtil.isNotBlank(unfiyHandler)) {
            try {
                IUnifyFieldsHandler handler = null;
                // 类
                if (unfiyHandler.contains(".")) {
                    handler = ClassUtil.newInstance(Class.forName(unfiyHandler));
                } // spring bean名称
                else if (appContext.containsBean(unfiyHandler)) {
                    handler = (IUnifyFieldsHandler) appContext.getBean(unfiyHandler);
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
            if (appContext.containsBean(translateCacheManager)) {
                sqlToyContext.setTranslateCacheManager(
                        (TranslateCacheManager) appContext.getBean(translateCacheManager));
            } // 包名和类名称
            else if (translateCacheManager.contains(".")) {
                sqlToyContext.setTranslateCacheManager(ClassUtil.tryInstance(translateCacheManager));
            }
        }

        // 自定义typeHandler
        String typeHandler = properties.getTypeHandler();
        if (StringUtil.isNotBlank(typeHandler)) {
            if (appContext.containsBean(typeHandler)) {
                sqlToyContext.setTypeHandler((TypeHandler) appContext.getBean(typeHandler));
            } // 包名和类名称
            else if (typeHandler.contains(".")) {
                sqlToyContext.setTypeHandler(ClassUtil.tryInstance(typeHandler));
            }
        }

        // 自定义数据源选择器
        String dataSourceSelector = properties.getDataSourceSelector();
        if (StringUtil.isNotBlank(dataSourceSelector)) {
            if (appContext.containsBean(dataSourceSelector)) {
                sqlToyContext
                        .setDataSourceSelector((DataSourceSelector) appContext.getBean(dataSourceSelector));
            } // 包名和类名称
            else if (dataSourceSelector.contains(".")) {
                sqlToyContext.setDataSourceSelector(ClassUtil.tryInstance(dataSourceSelector));
            }
        }

        // 自定义数据库连接获取和释放的接口实现
        String connectionFactory = properties.getConnectionFactory();
        if (StringUtil.isNotBlank(connectionFactory)) {
            if (appContext.containsBean(connectionFactory)) {
                sqlToyContext.setConnectionFactory((ConnectionFactory) appContext.getBean(connectionFactory));
            } // 包名和类名称
            else if (connectionFactory.contains(".")) {
                sqlToyContext.setConnectionFactory(ClassUtil.tryInstance(connectionFactory));
            }
        }

        // 自定义字段安全实现器
        String fieldsSecureProvider = properties.getFieldsSecureProvider();
        if (StringUtil.isNotBlank(fieldsSecureProvider)) {
            if (appContext.containsBean(fieldsSecureProvider)) {
                sqlToyContext.setFieldsSecureProvider(
                        (FieldsSecureProvider) appContext.getBean(fieldsSecureProvider));
            } // 包名和类名称
            else if (fieldsSecureProvider.contains(".")) {
                sqlToyContext.setFieldsSecureProvider(ClassUtil.tryInstance(fieldsSecureProvider));
            }
        }

        // 自定义字段脱敏处理器
        String desensitizeProvider = properties.getDesensitizeProvider();
        if (StringUtil.isNotBlank(desensitizeProvider)) {
            if (appContext.containsBean(desensitizeProvider)) {
                sqlToyContext
                        .setDesensitizeProvider((DesensitizeProvider) appContext.getBean(desensitizeProvider));
            } // 包名和类名称
            else if (desensitizeProvider.contains(".")) {
                sqlToyContext.setDesensitizeProvider(ClassUtil.tryInstance(desensitizeProvider));
            }
        }

        // 自定义sql中filter处理器
        String customFilterHandler = properties.getCustomFilterHandler();
        if (StringUtil.isNotBlank(customFilterHandler)) {
            if (appContext.containsBean(customFilterHandler)) {
                sqlToyContext.setCustomFilterHandler((FilterHandler) appContext.getBean(customFilterHandler));
            } // 包名和类名称
            else if (customFilterHandler.contains(".")) {
                sqlToyContext.setCustomFilterHandler(ClassUtil.tryInstance(customFilterHandler));
            }
        }

        // 自定义sql执行超时处理器
        String overTimeSqlHandler = properties.getOverTimeSqlHandler();
        if (StringUtil.isNotBlank(overTimeSqlHandler)) {
            if (appContext.containsBean(overTimeSqlHandler)) {
                sqlToyContext.setOverTimeSqlHandler((OverTimeSqlHandler) appContext.getBean(overTimeSqlHandler));
            } // 包名和类名称
            else if (overTimeSqlHandler.contains(".")) {
                sqlToyContext.setOverTimeSqlHandler(ClassUtil.tryInstance(overTimeSqlHandler));
            }
        }
        // 自定义sql拦截处理器
        String[] sqlInterceptors = properties.getSqlInterceptors();
        if (null != sqlInterceptors && sqlInterceptors.length > 0) {
            List<SqlInterceptor> sqlInterceptorList = new ArrayList<SqlInterceptor>();
            for (String interceptor : sqlInterceptors) {
                if (appContext.containsBean(interceptor)) {
                    sqlInterceptorList.add((SqlInterceptor) appContext.getBean(interceptor));
                } // 包名和类名称
                else if (interceptor.contains(".")) {
                    sqlInterceptorList
                            .add(((SqlInterceptor) Class.forName(interceptor).getDeclaredConstructor().newInstance()));
                }
            }
            sqlToyContext.setSqlInterceptors(sqlInterceptorList);
        }

        // 自定义sql格式化器
        String sqlFormater = properties.getSqlFormater();
        if (StringUtil.isNotBlank(sqlFormater)) {
            // 提供简化配置
            if (sqlFormater.equalsIgnoreCase("default") || sqlFormater.equalsIgnoreCase("defaultFormater")
                    || sqlFormater.equalsIgnoreCase("defaultSqlFormater")) {
                sqlFormater = "org.sagacity.sqltoy.plugins.formater.impl.DefaultSqlFormater";
            }
            if (appContext.containsBean(sqlFormater)) {
                sqlToyContext.setSqlFormater((SqlFormater) appContext.getBean(sqlFormater));
            } // 包名和类名称
            else if (sqlFormater.contains(".")) {
                sqlToyContext.setSqlFormater(
                        (SqlFormater) Class.forName(sqlFormater).getDeclaredConstructor().newInstance());
            }
        }

        // 自定义线程池
        SqlToyContextTaskPoolProperties taskPoolProperties= properties.getTaskExecutor();
        if(taskPoolProperties!=null){
            String taskExecutorName= taskPoolProperties.getTargetPoolName();
            if (StringUtil.isNotBlank(taskExecutorName)) {
                sqlToyContext.setTaskExecutorName(taskExecutorName);
            }
        }

        return sqlToyContext;
    }
}
