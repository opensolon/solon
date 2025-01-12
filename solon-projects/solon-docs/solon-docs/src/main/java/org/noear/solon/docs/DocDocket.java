/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.docs;

import org.noear.solon.Utils;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.docs.models.*;

import java.io.Serializable;
import java.net.URI;
import java.util.*;

/**
 * 文档摘要
 *
 * @author noear
 * @since 2.2
 * */
public class DocDocket implements Serializable {
    private boolean enable = true;
    private String version = "2.0";
    private List<ApiScheme> schemes = new ArrayList<>();
    private String groupName = "default";
    private String host;
    private String basePath;
    private transient Serializer<String> serializer;
    private DocUpstream upstream;

    //基础鉴权
    private Map<String, String> basicAuth = new LinkedHashMap<>();

    //全局结果
    private Class<?> globalResult;
    //全局响应代码描述
    private Map<Integer, String> globalResponseCodes = new LinkedHashMap<>();
    //全局响应放到data下面
    private boolean globalResponseInData = false;
    //全局参数
    private Set<Object> globalParams = new LinkedHashSet<>();

    //接口信息
    private ApiInfo info = new ApiInfo();
    //接口资源
    private List<ApiResource> apis = new ArrayList<>();

    //外部文件
    private ApiExternalDocs externalDocs;
    //供应商扩展
    private Map<String, Object> vendorExtensions = new LinkedHashMap<>();
    //安全扩展
    private Map<String, Object> securityExtensions = new LinkedHashMap<>();

    /**
     * 是否启用
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * 配置是否启用
     */
    public DocDocket enable(boolean enable) {
        this.enable = enable;
        return this;
    }

    /**
     * 版本号
     */
    public String version() {
        return version;
    }

    /**
     * 配置版本号
     */
    public DocDocket version(String version) {
        this.version = version;
        return this;
    }


    /**
     * 主机
     */
    public String host() {
        return host;
    }

    /**
     * 配置主机
     */
    public DocDocket host(String host) {
        this.host = host;
        return this;
    }

    /**
     * 协议架构（http, https）
     */
    public List<ApiScheme> schemes() {
        return schemes;
    }


    /**
     * 配置协议架构（http, https）
     */
    public DocDocket schemes(String... schemes) {
        for (String s : schemes) {
            ApiScheme scheme = ApiScheme.forValue(s);
            if (scheme != null) {
                this.schemes.add(scheme);
            }
        }
        return this;
    }

    /**
     * 分组名字
     */
    public String groupName() {
        return groupName;
    }

    /**
     * 配置分组名字
     */
    public DocDocket groupName(String groupName) {
        this.groupName = groupName;
        return this;
    }


    /**
     * 基础路径
     */
    public String basePath() {
        return basePath;
    }

    /**
     * 配置基础路径
     */
    public DocDocket basePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    /**
     * 基础鉴权（格式：admin#123456,user#654321,张三#abc）
     */
    public Map<String, String> basicAuth() {
        return basicAuth;
    }

    /**
     * 配置基础鉴权
     */
    public DocDocket basicAuth(String username, String password) {
        this.basicAuth.put(username, password);
        return this;
    }

    /**
     * 配置基础鉴权
     */
    public DocDocket basicAuth(BasicAuth basicAuth) {
        if (basicAuth != null) {
            if (basicAuth.isEnable() && Utils.isNotEmpty(basicAuth.getUsername())) {
                this.basicAuth.put(basicAuth.getUsername(), basicAuth.getPassword());
            }
        }
        return this;
    }

    /**
     * 接口资源
     */
    public List<ApiResource> apis() {
        return apis;
    }

    /**
     * 配置接口资源
     */
    public DocDocket apis(String basePackage) {
        this.apis.add(new ApiResource(basePackage));

        return this;
    }

    /**
     * 配置接口资源
     */
    public DocDocket apis(ApiResource apiResource) {
        this.apis.add(apiResource);

        return this;
    }

    /**
     * 接口信息
     */
    public ApiInfo info() {
        return info;
    }

    /**
     * 配置接口信息
     */
    public DocDocket info(ApiInfo info) {
        this.info = info;
        return this;
    }

    /**
     * 获取上游
     */
    public DocUpstream upstream() {
        return upstream;
    }

    /**
     * 配置上游
     */
    public DocDocket upstream(DocUpstream upstream) {
        this.upstream = upstream;
        return this;
    }

    /**
     * 配置上游
     */
    public DocDocket upstream(String target, String contextPath, String path) {
        if (target.indexOf("://") < 0) {
            target = "lb://" + target; //支持："http://...", "lb://service-name", "service-name"
        }

        this.upstream = new DocUpstream(URI.create(target), contextPath, path);
        return this;
    }

    /**
     * 全局响应到data下面
     */
    public boolean globalResponseInData() {
        return globalResponseInData;
    }


    /**
     * 配置全局响应到data下面
     */
    public DocDocket globalResponseInData(boolean globalResponseInData) {
        this.globalResponseInData = globalResponseInData;
        return this;
    }

    /**
     * 全局响应代码描述（401,403...）
     */
    public Map<Integer, String> globalResponseCodes() {
        return globalResponseCodes;
    }

    /**
     * 配置全局响应代码描述（401,403...）
     */
    public DocDocket globalResponseCodes(Map<Integer, String> globalResponseCodes) {
        if (globalResponseCodes != null) {
            this.globalResponseCodes.putAll(globalResponseCodes);
        }
        return this;
    }


    /**
     * 全局结果类
     */
    public Class<?> globalResult() {
        return globalResult;
    }

    /**
     * 配置全局结果类
     */
    public DocDocket globalResult(Class<?> clz) {
        globalResult = clz;
        return this;
    }

    /**
     * 全局参数
     */
    public Set<Object> globalParams() {
        return globalParams;
    }

    /**
     * 配置全局参数
     */
    public DocDocket globalParams(Collection<Object> globalParams) {
        if (globalParams != null) {
            this.globalParams.addAll(globalParams);
        }
        return this;
    }

    /**
     * 配置全局参数
     */
    public DocDocket globalParams(Object param) {
        this.globalParams.add(param);
        return this;
    }

    /**
     * 外部文档
     */
    public ApiExternalDocs externalDocs() {
        return externalDocs;
    }


    /**
     * 配置外部文档
     */
    public DocDocket externalDocs(ApiExternalDocs externalDocs) {
        this.externalDocs = externalDocs;
        return this;
    }

    /**
     * 配置外部文档
     */
    public DocDocket externalDocs(String description, String url) {
        this.externalDocs = new ApiExternalDocs();
        return this;
    }

    /**
     * 供应商扩展
     */
    public Map<String, Object> vendorExtensions() {
        return vendorExtensions;
    }


    /**
     * 配置供应商扩展
     */
    public DocDocket vendorExtensions(Map<String, Object> vendorExtensions) {
        if (vendorExtensions != null) {
            this.vendorExtensions.putAll(vendorExtensions);
        }
        return this;
    }


    /**
     * 配置供应商扩展
     */
    public DocDocket vendorExtensions(String name, Object value) {
        this.vendorExtensions.put(name, value);
        return this;
    }


    /**
     * 配置供应商扩展
     */
    public DocDocket vendorExtensions(List<ApiVendorExtension> extensions) {
        for (ApiVendorExtension ext : extensions) {
            this.vendorExtensions.put(ext.getName(), ext.getValue());
        }

        return this;
    }

    /**
     * 安全扩展
     */
    public Map<String, Object> securityExtensions() {
        return securityExtensions;
    }

    /**
     * 配置安全扩展
     */
    public DocDocket securityExtensions(Map<String, Object> securityExtensions) {
        if (securityExtensions != null) {
            this.securityExtensions.putAll(securityExtensions);
        }
        return this;
    }

    /**
     * 配置安全扩展
     */
    public DocDocket securityExtensions(String name, Object value) {
        this.securityExtensions.put(name, value);
        return this;
    }

    /**
     * 序列化
     */
    public Serializer<String> serializer() {
        return serializer;
    }

    /**
     * 配置序列化
     */
    public void serializer(Serializer<String> serializer) {
        this.serializer = serializer;
    }
}