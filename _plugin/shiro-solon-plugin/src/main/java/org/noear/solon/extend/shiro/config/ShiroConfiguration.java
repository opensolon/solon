package org.noear.solon.extend.shiro.config;

import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.SimpleSessionFactory;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.noear.solon.annotation.Configuration;

import java.util.List;

/**
 * @author noear
 * @since 1.3
 */
@Configuration
public class ShiroConfiguration {
    public ShiroConfiguration() {
    }

    protected SessionsSecurityManager securityManager(List<Realm> realms) {
        SessionsSecurityManager securityManager = createSecurityManager();
        securityManager.setAuthenticator(authenticator());
        securityManager.setAuthorizer(authorizer());
        securityManager.setRealms(realms);
        securityManager.setSessionManager(sessionManager());
//        securityManager.setEventBus(eventBus);

//        if (cacheManager != null) {
//            securityManager.setCacheManager(cacheManager);
//        }

        return securityManager;
    }

    protected SessionManager sessionManager() {
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setSessionFactory(sessionFactory());
//        sessionManager.setDeleteInvalidSessions(sessionManagerDeleteInvalidSessions);
        return sessionManager;
    }


    protected SessionsSecurityManager createSecurityManager() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setSubjectDAO(subjectDAO());
        securityManager.setSubjectFactory(subjectFactory());

        RememberMeManager rememberMeManager = rememberMeManager();
        if (rememberMeManager != null) {
            securityManager.setRememberMeManager(rememberMeManager);
        }

        return securityManager;
    }

    protected RememberMeManager rememberMeManager() {
        return null;
    }

    protected SubjectDAO subjectDAO() {
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator());
        return subjectDAO;
    }

    protected SessionStorageEvaluator sessionStorageEvaluator() {
        return new DefaultSessionStorageEvaluator();
    }

    protected SubjectFactory subjectFactory() {
        return new DefaultSubjectFactory();
    }


    protected SessionFactory sessionFactory() {
        return new SimpleSessionFactory();
    }

    protected SessionDAO sessionDAO() {
        return new MemorySessionDAO();
    }

    protected Authorizer authorizer() {
        ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();

//        if (permissionResolver != null) {
//            authorizer.setPermissionResolver(permissionResolver);
//        }
//
//        if (rolePermissionResolver != null) {
//            authorizer.setRolePermissionResolver(rolePermissionResolver);
//        }

        return authorizer;
    }

    protected AuthenticationStrategy authenticationStrategy() {
        return new AtLeastOneSuccessfulStrategy();
    }

    protected Authenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(authenticationStrategy());
        return authenticator;
    }

    protected Realm iniRealmFromLocation(String iniLocation) {
        Ini ini = Ini.fromResourcePath(iniLocation);
        return new IniRealm( ini );
    }
}
