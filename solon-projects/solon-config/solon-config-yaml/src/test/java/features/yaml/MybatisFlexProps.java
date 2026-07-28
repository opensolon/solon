package features.yaml;

import java.util.List;

/**
 * mybatis-flex 配置实体（保持 YAML 层次结构）
 *
 * <pre>
 * mybatis-flex:
 *   defaultDatasourceKey: db1
 *   typeAliasesPackage:
 *     - "demo4035.model"
 *   mapperLocations:
 *     - "demo4035.dso.mapper"
 *   configuration:
 *     cacheEnabled: false
 *     mapUnderscoreToCamelCase: true
 *   globalConfig:
 *     printBanner: false
 * </pre>
 *
 * @author noear
 * @since 4.0
 */
public class MybatisFlexProps {
    private String defaultDatasourceKey;
    private List<String> typeAliasesPackage;
    private List<String> mapperLocations;
    private Configuration configuration;
    private GlobalConfig globalConfig;

    public String getDefaultDatasourceKey() {
        return defaultDatasourceKey;
    }

    public void setDefaultDatasourceKey(String defaultDatasourceKey) {
        this.defaultDatasourceKey = defaultDatasourceKey;
    }

    public List<String> getTypeAliasesPackage() {
        return typeAliasesPackage;
    }

    public void setTypeAliasesPackage(List<String> typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    public List<String> getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(List<String> mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public static class Configuration {
        private Boolean cacheEnabled;
        private Boolean mapUnderscoreToCamelCase;

        public Boolean getCacheEnabled() {
            return cacheEnabled;
        }

        public void setCacheEnabled(Boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
        }

        public Boolean getMapUnderscoreToCamelCase() {
            return mapUnderscoreToCamelCase;
        }

        public void setMapUnderscoreToCamelCase(Boolean mapUnderscoreToCamelCase) {
            this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
        }
    }

    public static class GlobalConfig {
        private Boolean printBanner;

        public Boolean getPrintBanner() {
            return printBanner;
        }

        public void setPrintBanner(Boolean printBanner) {
            this.printBanner = printBanner;
        }
    }
}
