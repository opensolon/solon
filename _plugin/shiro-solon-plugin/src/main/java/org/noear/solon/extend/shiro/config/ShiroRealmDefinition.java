package org.noear.solon.extend.shiro.config;

import org.apache.shiro.realm.Realm;

import java.util.List;

/**
 * @author noear
 * @since 1.3
 */
public interface ShiroRealmDefinition {
    List<Realm> getRealmList();
}
