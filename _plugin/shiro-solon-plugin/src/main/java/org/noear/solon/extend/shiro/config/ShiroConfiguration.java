package org.noear.solon.extend.shiro.config;

import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;

import java.net.URL;


/**
 * @author tomsun28
 * @since 2021/5/12 23:20
 */
public class ShiroConfiguration {
    public SessionsSecurityManager securityManager() {
        URL url = Utils.getResource("shiro.ini");

        if (url == null) {
            //
            //如果没有配置文件，则直接
            //
            return new DefaultSecurityManager();
        } else {
            return new DefaultSecurityManager(iniRealmFromLocation());
        }
    }

    protected Realm iniRealmFromLocation() {
        String iniLocation = "classpath:shiro.ini";
        Ini ini = Ini.fromResourcePath(iniLocation);
        return new IniRealm(ini);
    }
}
