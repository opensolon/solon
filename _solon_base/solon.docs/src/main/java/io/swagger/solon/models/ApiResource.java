package io.swagger.solon.models;


/**
 * Swagger 资源信息
 * */
public class ApiResource {

    private String basePackage;

    public ApiResource() {

    }

    public ApiResource(String basePackage) {
        this.basePackage = basePackage;
    }

    public String basePackage() {
        return basePackage;
    }

    public ApiResource basePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }
}
