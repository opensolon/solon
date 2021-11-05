package org.noear.solon.extend.sqltoy.configure;

import java.io.Serializable;
import java.util.Map;

public class SqlToyContextProperties implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8313800149129731930L;

    /**
     * 指定sql.xml 文件路径,多个路径用逗号分隔
     */
    private String sqlResourcesDir;

    /**
     * 缓存翻译的配置文件
     */
    private String translateConfig;

    /**
     * 针对不同数据库函数进行转换,非必须属性,close 表示关闭
     */
    private Object functionConverts;

    /**
     * 数据库方言，一般无需设置
     */
    private String dialect;

    /**
     * Sqltoy实体Entity包路径,非必须属性
     */
    private String[] packagesToScan;

    /**
     * 额外注解class类，已经没有必要
     */
    private String[] annotatedClasses;

    /**
     * 具体的sql.xml 文件资源
     */
    private String[] sqlResources;

    /**
     * es的配置
     */
    private Elastic elastic;

    /**
     * 是否开启debug模式(默认为false)
     */
    private Boolean debug;

    /**
     * 批量操作，每批次数量,默认200
     */
    private Integer batchSize;

    private int fetchSize = -1;

    /**
     * 分页最大单页数据量(默认是5万)
     */
    private Integer pageFetchSizeLimit;

    /**
     * 超时打印sql(毫秒,默认30秒)
     */
    private Integer printSqlTimeoutMillis;

    /**
     * sql文件脚本变更检测间隔时长(秒)
     */
    private Integer scriptCheckIntervalSeconds;

    /**
     * 缓存更新、sql脚本更新 延迟多少秒开始检测
     */
    private Integer delayCheckSeconds;

    private String encoding;

    /**
     * 分页页号超出总页时转第一页，否则返回空集合
     */
    private boolean pageOverToFirst = true;

    /**
     * 统一字段处理器
     */
    private String unifyFieldsHandler;

    /**
     * 数据库方言参数配置
     */
    private Map<String, String> dialectConfig;

    /**
     * sqltoy默认数据库
     */
    private String defaultDataSource;

    /**
     * 数据库保留字,用逗号分隔
     */
    private String reservedWords;

    /**
     * 缓存管理器
     */
    private String translateCacheManager;

    /**
     * 字段类型转换器
     */
    private String typeHandler;

    /**
     * 自定义数据源选择器
     */
    private String dataSourceSelector;

    /**
     * 缓存类型，默认solon，可选ehcache,caffeine
     */
    private String cacheType = "solon";

    /**
     * 当发现有重复sqlId时是否抛出异常，终止程序执行
     */
    private boolean breakWhenSqlRepeat = true;

    /**
     * 连接管理的实现扩展定义
     */
    private String connectionFactory;

    /**
     * @return the sqlResourcesDir
     */
    public String getSqlResourcesDir() {
        return sqlResourcesDir;
    }

    /**
     * @param sqlResourcesDir the sqlResourcesDir to set
     */
    public void setSqlResourcesDir(String sqlResourcesDir) {
        this.sqlResourcesDir = sqlResourcesDir;
    }

    /**
     * @return the translateConfig
     */
    public String getTranslateConfig() {
        return translateConfig;
    }

    /**
     * @param translateConfig the translateConfig to set
     */
    public void setTranslateConfig(String translateConfig) {
        this.translateConfig = translateConfig;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the batchSize
     */
    public Integer getBatchSize() {
        return batchSize;
    }

    /**
     * @param batchSize the batchSize to set
     */
    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Object getFunctionConverts() {
        return functionConverts;
    }

    /**
     * functionConverts=close 表示关闭
     *
     * @param functionConverts
     */
    public void setFunctionConverts(Object functionConverts) {
        this.functionConverts = functionConverts;
    }

    /**
     * @return the packagesToScan
     */
    public String[] getPackagesToScan() {
        return packagesToScan;
    }

    /**
     * @param packagesToScan the packagesToScan to set
     */
    public void setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    /**
     * @return the unifyFieldsHandler
     */
    public String getUnifyFieldsHandler() {
        return this.unifyFieldsHandler;
    }

    /**
     * @param unifyFieldsHandler the unifyFieldsHandler to set
     */
    public void setUnifyFieldsHandler(String unifyFieldsHandler) {
        this.unifyFieldsHandler = unifyFieldsHandler;
    }

    public Elastic getElastic() {
        return elastic;
    }

    public void setElastic(Elastic elastic) {
        this.elastic = elastic;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public Map<String, String> getDialectConfig() {
        return dialectConfig;
    }

    public void setDialectConfig(Map<String, String> dialectConfig) {
        this.dialectConfig = dialectConfig;
    }

    public Integer getPageFetchSizeLimit() {
        return pageFetchSizeLimit;
    }

    public void setPageFetchSizeLimit(Integer pageFetchSizeLimit) {
        this.pageFetchSizeLimit = pageFetchSizeLimit;
    }

    public String[] getAnnotatedClasses() {
        return annotatedClasses;
    }

    public void setAnnotatedClasses(String[] annotatedClasses) {
        this.annotatedClasses = annotatedClasses;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getPrintSqlTimeoutMillis() {
        return printSqlTimeoutMillis;
    }

    public void setPrintSqlTimeoutMillis(Integer printSqlTimeoutMillis) {
        this.printSqlTimeoutMillis = printSqlTimeoutMillis;
    }

    public Integer getScriptCheckIntervalSeconds() {
        return scriptCheckIntervalSeconds;
    }

    public void setScriptCheckIntervalSeconds(Integer scriptCheckIntervalSeconds) {
        this.scriptCheckIntervalSeconds = scriptCheckIntervalSeconds;
    }

    public Integer getDelayCheckSeconds() {
        return delayCheckSeconds;
    }

    public void setDelayCheckSeconds(Integer delayCheckSeconds) {
        this.delayCheckSeconds = delayCheckSeconds;
    }

    public String[] getSqlResources() {
        return sqlResources;
    }

    public void setSqlResources(String[] sqlResources) {
        this.sqlResources = sqlResources;
    }

    public String getDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDefaultDataSource(String defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    /**
     * @return the reservedWords
     */
    public String getReservedWords() {
        return reservedWords;
    }

    /**
     * @param reservedWords the reservedWords to set
     */
    public void setReservedWords(String reservedWords) {
        this.reservedWords = reservedWords;
    }

    /**
     * @return the translateCacheManager
     */
    public String getTranslateCacheManager() {
        return translateCacheManager;
    }

    /**
     * @param translateCacheManager the translateCacheManager to set
     */
    public void setTranslateCacheManager(String translateCacheManager) {
        this.translateCacheManager = translateCacheManager;
    }

    /**
     * @return the typeHandler
     */
    public String getTypeHandler() {
        return typeHandler;
    }

    /**
     * @param typeHandler the typeHandler to set
     */
    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    /**
     * @return the cacheType
     */
    public String getCacheType() {
        return cacheType;
    }

    /**
     * @param cacheType the cacheType to set
     */
    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public boolean isPageOverToFirst() {
        return pageOverToFirst;
    }

    public void setPageOverToFirst(boolean pageOverToFirst) {
        this.pageOverToFirst = pageOverToFirst;
    }

    /**
     * @return the dataSourceSelector
     */
    public String getDataSourceSelector() {
        return dataSourceSelector;
    }

    /**
     * @param dataSourceSelector the dataSourceSelector to set
     */
    public void setDataSourceSelector(String dataSourceSelector) {
        this.dataSourceSelector = dataSourceSelector;
    }

    /**
     * @return the fetchSize
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * @param fetchSize the fetchSize to set
     */
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public void setConnectionFactory(String connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public String getConnectionFactory() {
        return connectionFactory;
    }

    /**
     * @return the breakWhenSqlRepeat
     */
    public boolean isBreakWhenSqlRepeat() {
        return breakWhenSqlRepeat;
    }

    /**
     * @param breakWhenSqlRepeat the breakWhenSqlRepeat to set
     */
    public void setBreakWhenSqlRepeat(boolean breakWhenSqlRepeat) {
        this.breakWhenSqlRepeat = breakWhenSqlRepeat;
    }


}
