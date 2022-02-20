package org.noear.solon.plugin.jap.properties;

import com.fujieid.jap.simple.SimpleConfig;
import com.fujieid.jap.social.SocialConfig;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

@Configuration
@Inject("${jap}")
public class JapProperties {

    private String basePath;

    private Boolean sso;

    private String domain;

    private SocialConfig social;

    private SimpleConfig simple;

    public Boolean getSso() {
        return this.sso;
    }

    public void setSso(Boolean sso) {
        this.sso = sso;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public SocialConfig getSocial() {
        return this.social;
    }

    public void setSocial(SocialConfig social) {
        this.social = social;
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
}
