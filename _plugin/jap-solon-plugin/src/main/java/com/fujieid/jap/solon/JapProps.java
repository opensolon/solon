package com.fujieid.jap.solon;

import com.fujieid.jap.core.config.JapConfig;
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

    private String basePath;
    private String issuer;

    private JapConfig japConfig;
    private SimpleConfig simpleConfig;

    private Map<String, AuthConfig> credentials;
    private List<String> callbacks;

    public String getIssuer() {
        return this.issuer;
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

    public JapConfig getJapConfig() {
        return this.japConfig;
    }

    public SimpleConfig getSimpleConfig() {
        return this.simpleConfig;
    }

}
