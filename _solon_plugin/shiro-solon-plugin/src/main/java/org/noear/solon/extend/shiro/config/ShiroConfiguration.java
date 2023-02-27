package org.noear.solon.extend.shiro.config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.util.ResourceUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * @author tomsun28
 * @since 2021/5/12 23:20
 */
@Configuration
public class ShiroConfiguration {

    @Inject
    AuthorizingRealm authRealm;

    @Init
    public void init() {
        List<Realm> realmList = new ArrayList<>();

        Realm iniRealm = iniRealmFromLocation();
        if (iniRealm != null) {
            realmList.add(iniRealm);
        }

        if (authRealm != null) {
            realmList.add(authRealm);
        }

        SecurityUtils.setSecurityManager(new DefaultSecurityManager(realmList));
    }

    protected Realm iniRealmFromLocation() {
        if (ResourceUtil.getResource("shiro.ini") == null) {
            return null;
        }

        String iniLocation = "classpath:shiro.ini";
        Ini ini = Ini.fromResourcePath(iniLocation);
        return new IniRealm(ini);
    }
}
