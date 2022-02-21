package com.fujieid.jap.solon;

import com.fujieid.jap.simple.SimpleConfig;
import me.zhyd.oauth.config.AuthConfig;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Map;

/**
 * @author 颖
 * @author work
 */
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

    public boolean isSso() {
        return sso;
    }

    public String getDomain() {
        return this.domain;
    }

    public SimpleConfig getSimple() {
        return this.simple == null ? new SimpleConfig() : this.simple;
    }

    public String getBasePath() {
        return this.basePath == null ? "/auth" : this.basePath;
    }

    public Map<String, AuthConfig> getCredentials() {
        return this.credentials;
    }

    public List<String> getCallbacks() {
        return this.callbacks;
    }

}
