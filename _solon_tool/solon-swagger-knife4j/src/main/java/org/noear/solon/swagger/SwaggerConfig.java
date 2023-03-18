package org.noear.solon.swagger;

import java.util.Map;

public class SwaggerConfig {
    /**
     * swagger配置文件
     */
    private String propPath;

    /**
     * http返回状态
     */
    private Map httpCode;

    /**
     * http返回状态200时的通用返回格式
     */
    private Class<?> commonRet;

    public void setPropPath(String propPath) {
        this.propPath = propPath;
    }

    public String getPropPath() {
        return propPath;
    }

    public void setHttpCode(Map httpCode) {
        this.httpCode = httpCode;
    }

    public Map getHttpCode() {
        return httpCode;
    }

    public void setCommonRet(Class<?> commonRet) {
        this.commonRet = commonRet;
    }

    public Class<?> getCommonRet() {
        return commonRet;
    }

    public SwaggerConfig() {
        this.propPath = "swagger.properties";
        this.httpCode = new SwaggerHttpCode().getHttpCodeKv();
    }
}