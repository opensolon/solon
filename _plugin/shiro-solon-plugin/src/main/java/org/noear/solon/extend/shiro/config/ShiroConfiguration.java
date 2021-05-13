package org.noear.solon.extend.shiro.config;

import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;


/**
 * @author noear
 * @since 1.3
 */
public class ShiroConfiguration {

    public SessionsSecurityManager securityManager() {
        return new DefaultSecurityManager(iniRealmFromLocation());
    }

    protected Realm iniRealmFromLocation() {
        String iniLocation = "classpath:shiro.ini";
        Ini ini = Ini.fromResourcePath(iniLocation);
        return new IniRealm(ini);
    }
}
