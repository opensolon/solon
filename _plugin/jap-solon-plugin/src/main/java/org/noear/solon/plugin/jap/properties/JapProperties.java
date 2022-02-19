package org.noear.solon.plugin.jap.properties;

import com.fujieid.jap.simple.SimpleConfig;
import com.fujieid.jap.social.SocialConfig;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

@Configuration
@Inject("${jap}")
public class JapProperties {

    private Boolean sso;

    private String domain;

    private SocialConfig social;

    private SimpleConfig simple;

    public Boolean getSso() {
        return sso;
    }

    public void setSso(Boolean sso) {
        this.sso = sso;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public SocialConfig getSocial() {
        return social;
    }

    public void setSocial(SocialConfig social) {
        this.social = social;
    }

    public SimpleConfig getSimple() {
        return simple;
    }

    public void setSimple(SimpleConfig simple) {
        this.simple = simple;
    }
}
