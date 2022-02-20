package com.fujieid.jap.solon;

import com.fujieid.jap.simple.SimpleConfig;
import me.zhyd.oauth.config.AuthConfig;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Map;

@Configuration
@Inject("${jap}")
public class JapProps {

    private boolean separate;
    private String basePath;
    private boolean sso;
    private String domain;
    private SimpleConfig simple;
    private Map<String, AuthConfig> credentials;
    private List<String> callbacks;

    public boolean isSeparate() {
        return separate;
    }

    public void setSeparate(boolean separate) {
        this.separate = separate;
    }

    public boolean isSso() {
        return sso;
    }

    public void setSso(boolean sso) {
        this.sso = sso;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public SimpleConfig getSimple() {
        return this.simple;
    }

    public void setSimple(SimpleConfig simple) {
        this.simple = simple;
    }

    public String getBasePath() {
        return this.basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Map<String, AuthConfig> getCredentials() {
        return this.credentials;
    }

    public void setCredentials(Map<String, AuthConfig> credentials) {
        this.credentials = credentials;
    }

    public List<String> getCallbacks() {
        return this.callbacks;
    }

    public void setCallbacks(List<String> callbacks) {
        this.callbacks = callbacks;
    }
}
