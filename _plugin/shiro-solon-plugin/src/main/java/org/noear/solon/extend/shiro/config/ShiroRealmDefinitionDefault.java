package org.noear.solon.extend.shiro.config;

import org.apache.shiro.realm.Realm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 1.3
 */
public class ShiroRealmDefinitionDefault implements ShiroRealmDefinition {
    private final List<Realm> realmList = new ArrayList<>();

    public void addRealm(Realm realm) {
        realmList.add(realm);
    }

    @Override
    public List<Realm> getRealmList() {
        return realmList;
    }
}
