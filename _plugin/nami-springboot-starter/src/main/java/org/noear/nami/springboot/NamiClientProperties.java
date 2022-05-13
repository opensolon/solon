package org.noear.nami.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * Naimi 配置项,主要用于生成配置提示
 */
@ConfigurationProperties("spring.nami")
public class NamiClientProperties {
    /**
     * 需要扫描的包,有多个时用","分割
     */
    private List<String> packages;
    /**
     * 自定义services地址，如：services.seviceA=https://serviceA-01.com,https://serviceA-02.com
     */
    private Map<String, List<String>> services;

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    public Map<String, List<String>> getServices() {
        return services;
    }

    public void setServices(Map<String, List<String>> services) {
        this.services = services;
    }
}
