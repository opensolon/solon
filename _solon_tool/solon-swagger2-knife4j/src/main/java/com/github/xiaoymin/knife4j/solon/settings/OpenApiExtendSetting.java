package com.github.xiaoymin.knife4j.solon.settings;

/**
 * @author noear
 * @since 2.3
 */
public class OpenApiExtendSetting {
    private String language = "zh-CN";
    private boolean enableSwaggerModels = true;
    private String swaggerModelName = "Swagger Models";
    private boolean enableReloadCacheParameter = false;
    private boolean enableAfterScript = true;
    private boolean enableDocumentManage = true;
    private boolean enableVersion = false;
    private boolean enableRequestCache = true;
    private boolean enableFilterMultipartApis = false;
    private String enableFilterMultipartApiMethodType = "POST";
    private boolean enableHost = false;
    private String enableHostText = "";
    private boolean enableDynamicParameter = false;
    private boolean enableDebug = true;
    private boolean enableFooter = true;
    private boolean enableFooterCustom = false;
    private String footerCustomContent;
    private boolean enableSearch = true;
    private boolean enableOpenApi = true;
    private boolean enableHomeCustom = false;
    private String homeCustomLocation;
    private boolean enableGroup = true;

    public OpenApiExtendSetting() {
    }

    public boolean isEnableGroup() {
        return this.enableGroup;
    }

    public void setEnableGroup(boolean enableGroup) {
        this.enableGroup = enableGroup;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isEnableRequestCache() {
        return this.enableRequestCache;
    }

    public void setEnableRequestCache(boolean enableRequestCache) {
        this.enableRequestCache = enableRequestCache;
    }

    public boolean isEnableFilterMultipartApis() {
        return this.enableFilterMultipartApis;
    }

    public void setEnableFilterMultipartApis(boolean enableFilterMultipartApis) {
        this.enableFilterMultipartApis = enableFilterMultipartApis;
    }

    public String getEnableFilterMultipartApiMethodType() {
        return this.enableFilterMultipartApiMethodType;
    }

    public void setEnableFilterMultipartApiMethodType(String enableFilterMultipartApiMethodType) {
        this.enableFilterMultipartApiMethodType = enableFilterMultipartApiMethodType;
    }

    public boolean isEnableHost() {
        return this.enableHost;
    }

    public void setEnableHost(boolean enableHost) {
        this.enableHost = enableHost;
    }

    public String getEnableHostText() {
        return this.enableHostText;
    }

    public void setEnableHostText(String enableHostText) {
        this.enableHostText = enableHostText;
    }

    public boolean isEnableSwaggerModels() {
        return this.enableSwaggerModels;
    }

    public void setEnableSwaggerModels(boolean enableSwaggerModels) {
        this.enableSwaggerModels = enableSwaggerModels;
    }

    public boolean isEnableDocumentManage() {
        return this.enableDocumentManage;
    }

    public void setEnableDocumentManage(boolean enableDocumentManage) {
        this.enableDocumentManage = enableDocumentManage;
    }

    public boolean isEnableVersion() {
        return this.enableVersion;
    }

    public void setEnableVersion(boolean enableVersion) {
        this.enableVersion = enableVersion;
    }

    public String getSwaggerModelName() {
        return this.swaggerModelName;
    }

    public void setSwaggerModelName(String swaggerModelName) {
        this.swaggerModelName = swaggerModelName;
    }

    public boolean isEnableAfterScript() {
        return this.enableAfterScript;
    }

    public void setEnableAfterScript(boolean enableAfterScript) {
        this.enableAfterScript = enableAfterScript;
    }

    public boolean isEnableReloadCacheParameter() {
        return this.enableReloadCacheParameter;
    }

    public void setEnableReloadCacheParameter(boolean enableReloadCacheParameter) {
        this.enableReloadCacheParameter = enableReloadCacheParameter;
    }

    public boolean isEnableDynamicParameter() {
        return this.enableDynamicParameter;
    }

    public void setEnableDynamicParameter(boolean enableDynamicParameter) {
        this.enableDynamicParameter = enableDynamicParameter;
    }

    public boolean isEnableDebug() {
        return this.enableDebug;
    }

    public void setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
    }

    public boolean isEnableFooter() {
        return this.enableFooter;
    }

    public void setEnableFooter(boolean enableFooter) {
        this.enableFooter = enableFooter;
    }

    public boolean isEnableFooterCustom() {
        return this.enableFooterCustom;
    }

    public void setEnableFooterCustom(boolean enableFooterCustom) {
        this.enableFooterCustom = enableFooterCustom;
    }

    public String getFooterCustomContent() {
        return this.footerCustomContent;
    }

    public void setFooterCustomContent(String footerCustomContent) {
        this.footerCustomContent = footerCustomContent;
    }

    public boolean isEnableSearch() {
        return this.enableSearch;
    }

    public void setEnableSearch(boolean enableSearch) {
        this.enableSearch = enableSearch;
    }

    public boolean isEnableOpenApi() {
        return this.enableOpenApi;
    }

    public void setEnableOpenApi(boolean enableOpenApi) {
        this.enableOpenApi = enableOpenApi;
    }

    public boolean isEnableHomeCustom() {
        return this.enableHomeCustom;
    }

    public void setEnableHomeCustom(boolean enableHomeCustom) {
        this.enableHomeCustom = enableHomeCustom;
    }

    public String getHomeCustomLocation() {
        return this.homeCustomLocation;
    }

    public void setHomeCustomLocation(String homeCustomLocation) {
        this.homeCustomLocation = homeCustomLocation;
    }
}
