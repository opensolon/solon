package org.noear.solon.docs;

import org.noear.solon.Utils;
import org.noear.solon.docs.models.*;

import java.util.*;

/**
 * 文档摘要
 * */
public class DocDocket {
    private String version = "2.0";
    private List<ApiScheme> schemes = new ArrayList<>();
    private String groupName = "default";
    private String host;
    private String basePath;


    private Map<String, String> basicAuth = new LinkedHashMap<>();

    private Class<?> globalResult;
    private Map<Integer, String> globalResponseCodes = new LinkedHashMap<>();
    private boolean globalResponseInData = false;
    private Set<Object> globalParams = new LinkedHashSet<>();

    private ApiInfo info = new ApiInfo();
    private List<ApiResource> apis = new ArrayList<>();

    /**
     * 外部文件
     * */
    private ApiExternalDocs externalDocs;
    /**
     * 供应商扩展
     * */
    private Map<String, Object> vendorExtensions = new LinkedHashMap<>();

    private Map<String, Object> securityExtensions = new LinkedHashMap<>();



    public String version() {
        return version;
    }

    public DocDocket version(String version) {
        this.version = version;
        return this;
    }


    public String host() {
        return host;
    }

    public DocDocket host(String host) {
        this.host = host;
        return this;
    }

    public List<ApiScheme> schemes() {
        return schemes;
    }

    public DocDocket schemes(String... schemes) {
        for (String s : schemes) {
            ApiScheme scheme = ApiScheme.forValue(s);
            if (scheme != null) {
                this.schemes.add(scheme);
            }
        }
        return this;
    }

    public String groupName() {
        return groupName;
    }

    public DocDocket groupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String basePath() {
        return basePath;
    }

    /**
     * 格式：admin#123456,user#654321,张三#abc
     */
    public DocDocket basePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public Map<String, String> basicAuth() {
        return basicAuth;
    }

    public DocDocket basicAuth(String username, String password) {
        this.basicAuth.put(username, password);
        return this;
    }

    public DocDocket basicAuth(BasicAuth basicAuth) {
        if (basicAuth != null) {
            if (basicAuth.isEnable() && Utils.isNotEmpty(basicAuth.getUsername())) {
                this.basicAuth.put(basicAuth.getUsername(), basicAuth.getPassword());
            }
        }
        return this;
    }

    public List<ApiResource> apis() {
        return apis;
    }

    public DocDocket apis(String basePackage) {
        this.apis.add(new ApiResource(basePackage));

        return this;
    }

    public DocDocket apis(ApiResource apiResource) {
        this.apis.add(apiResource);

        return this;
    }

    public ApiInfo info() {
        return info;
    }

    public DocDocket info(ApiInfo info) {
        this.info = info;
        return this;
    }

    public boolean globalResponseInData() {
        return globalResponseInData;
    }

    public DocDocket globalResponseInData(boolean globalResponseInData) {
        this.globalResponseInData = globalResponseInData;
        return this;
    }

    public Map<Integer, String> globalResponseCodes() {
        return globalResponseCodes;
    }

    public DocDocket globalResponseCodes(Map<Integer, String> globalResponseCodes) {
        if (globalResponseCodes != null) {
            this.globalResponseCodes.putAll(globalResponseCodes);
        }
        return this;
    }


    public Class<?> globalResult() {
        return globalResult;
    }

    public DocDocket globalResult(Class<?> clz) {
        globalResult = clz;
        return this;
    }

    public Set<Object> globalParams(){
        return globalParams;
    }

    public DocDocket globalParams(Collection<Object> globalParams) {
        if (globalParams != null) {
            this.globalParams.addAll(globalParams);
        }
        return this;
    }

    public DocDocket globalParams(Object param) {
        this.globalParams.add(param);
        return this;
    }

    public ApiExternalDocs externalDocs() {
        return externalDocs;
    }

    public DocDocket externalDocs(ApiExternalDocs externalDocs) {
        this.externalDocs = externalDocs;
        return this;
    }

    public DocDocket externalDocs(String description, String url) {
        this.externalDocs = new ApiExternalDocs();
        return this;
    }

    public Map<String, Object> vendorExtensions() {
        return vendorExtensions;
    }

    public DocDocket vendorExtensions(Map<String, Object> vendorExtensions) {
        if (vendorExtensions != null) {
            this.vendorExtensions.putAll(vendorExtensions);
        }
        return this;
    }

    public DocDocket vendorExtensions(String name, Object value) {
        this.vendorExtensions.put(name, value);
        return this;
    }

    public DocDocket vendorExtensions(List<ApiVendorExtension> extensions) {
        for (ApiVendorExtension ext : extensions) {
            this.vendorExtensions.put(ext.getName(), ext.getValue());
        }

        return this;
    }

    public Map<String, Object> securityExtensions() {
        return securityExtensions;
    }

    public DocDocket securityExtensions(Map<String, Object> securityExtensions) {
        if (securityExtensions != null) {
            this.securityExtensions.putAll(securityExtensions);
        }
        return this;
    }

    public DocDocket securityExtensions(String name, Object value) {
        this.securityExtensions.put(name, value);
        return this;
    }
}
