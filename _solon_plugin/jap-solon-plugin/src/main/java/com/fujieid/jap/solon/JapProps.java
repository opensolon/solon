package com.fujieid.jap.solon;

import com.fujieid.jap.core.config.JapConfig;
import com.fujieid.jap.simple.SimpleConfig;
import me.zhyd.oauth.config.AuthConfig;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author 颖
 * @author work
 * @since 1.6
 */
@Configuration
@Inject("${jap}")
public class JapProps {

    String authPath;
    String accountPath;
    String issuer;

    JapConfig japConfig;
    SimpleConfig simpleConfig;

    Map<String, AuthConfig> credentials;
    List<String> nexts;

    public String getIssuer() {
        return this.issuer;
    }

    public String getAuthPath() {
        return this.authPath == null ? "/auth" : this.authPath;
    }

    public String getAccountPath() {
        return this.accountPath == null ? "/account" : this.accountPath;
    }

    public Map<String, AuthConfig> getCredentials() {
        return this.credentials;
    }

    public List<String> getNexts() {
        return this.nexts == null ? new LinkedList<>() : this.nexts;
    }

    public JapConfig getJapConfig() {
        return this.japConfig;
    }

    public SimpleConfig getSimpleConfig() {
        return this.simpleConfig;
    }

}
