package io.swagger.solon.models;


import io.swagger.models.Scheme;
import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.In;
import io.swagger.models.auth.SecuritySchemeDefinition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Swagger 摘要
 * */
public class SwaggerDocket {
    private String version = "2.0";
    private List<Scheme> schemes = new ArrayList<>();
    private String groupName = "default";
    private String host;
    private String basePath;


    private Map<String,String> basicAuth = new LinkedHashMap<>();

    private Class<?> globalResult;
    private Map<Integer,String> globalResponseCodes = new LinkedHashMap<>();
    private boolean globalResponseInData = false;

    private ApiInfo info = new ApiInfo();
    private List<ApiResource> apis = new ArrayList<>();
    private Map<String, SecuritySchemeDefinition> securityDefinitions = new LinkedHashMap<>();

    public String version() {
        return version;
    }

    public String host() {
        return host;
    }
    public SwaggerDocket host(String host){
        this.host = host;
        return this;
    }

    public List<Scheme> schemes() {
        return schemes;
    }
    public SwaggerDocket schemes(Scheme... schemes) {
        for (Scheme scheme : schemes) {
            this.schemes.add(scheme);
        }
        return this;
    }

    public String groupName() {
        return groupName;
    }
    public SwaggerDocket groupName(String groupName){
        this.groupName = groupName;
        return this;
    }

    public String basePath() {
        return basePath;
    }

    /**
     * 格式：admin#123456,user#654321,张三#abc
     * */
    public SwaggerDocket basePath(String basePath){
        this.basePath = basePath;
        return this;
    }

    public Map<String,String> basicAuth() {
        return basicAuth;
    }
    public SwaggerDocket basicAuth(String username, String password) {
        this.basicAuth.put(username, password);
        return this;
    }

    public List<ApiResource> apis() {
        return apis;
    }
    public SwaggerDocket apis(String basePackage){
        this.apis.add(new ApiResource(basePackage));

        return this;
    }

    public SwaggerDocket apis(ApiResource apiResource){
        this.apis.add(apiResource);

        return this;
    }

    public ApiInfo info(){
        return info;
    }
    public SwaggerDocket info(ApiInfo info){
        this.info = info;
        return this;
    }

    public boolean globalResponseInData(){
        return globalResponseInData;
    }
    public SwaggerDocket globalResponseInData(boolean globalResponseInData){
        this.globalResponseInData = globalResponseInData;
        return this;
    }

    public Map<Integer,String> globalResponseCodes(){
        return globalResponseCodes;
    }
    public SwaggerDocket globalResponseCodes(Map<Integer,String> globalResponseCodes) {
        if (globalResponseCodes != null) {
            this.globalResponseCodes.putAll(globalResponseCodes);
        }
        return this;
    }


    public Class<?> globalResult(){
        return globalResult;
    }

    public SwaggerDocket globalResult(Class<?> clz){
        globalResult = clz;
        return this;
    }

    public Map<String, SecuritySchemeDefinition> securityDefinitions(){
        return securityDefinitions;
    }

    public SwaggerDocket securityDefinition(String name, SecuritySchemeDefinition securityDefinition) {
        securityDefinitions.put(name, securityDefinition);
        return this;
    }

    public SwaggerDocket securityDefinitionInHeader(String name) {
        securityDefinitions.put(name, new ApiKeyAuthDefinition().in(In.HEADER));
        return this;
    }

    public SwaggerDocket securityDefinitionInQuery(String name) {
        securityDefinitions.put(name, new ApiKeyAuthDefinition().in(In.QUERY));
        return this;
    }
}
