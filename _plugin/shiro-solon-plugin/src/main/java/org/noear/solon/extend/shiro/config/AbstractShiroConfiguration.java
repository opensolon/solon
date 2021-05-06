package org.noear.solon.extend.shiro.config;

import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.config.Ini;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.support.DefaultEventBus;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.SimpleSessionFactory;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.noear.solon.annotation.Inject;

/**
 * @author noear
 * @since 1.3
 */
public class AbstractShiroConfiguration {
    @Inject(required = false)
    protected CacheManager cacheManager;
    @Inject(required = false)
    protected RolePermissionResolver rolePermissionResolver;
    @Inject(required = false)
    protected PermissionResolver permissionResolver;
    @Inject
    protected EventBus eventBus = new DefaultEventBus();

    @Inject("${shiro.sessionManager.deleteInvalidSessions:true}")
    protected boolean sessionManagerDeleteInvalidSessions;

    public AbstractShiroConfiguration() {
    }

    protected SessionsSecurityManager securityManager(ShiroRealmDefinition realmDefinition) {
        SessionsSecurityManager securityManager = this.createSecurityManager();
        securityManager.setAuthenticator(this.authenticator());
        securityManager.setAuthorizer(this.authorizer());
        securityManager.setRealms(realmDefinition.getRealmList());
        securityManager.setSessionManager(this.sessionManager());
        securityManager.setEventBus(this.eventBus);
        if (this.cacheManager != null) {
            securityManager.setCacheManager(this.cacheManager);
        }

        return securityManager;
    }

    protected SessionManager sessionManager() {
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionDAO(this.sessionDAO());
        sessionManager.setSessionFactory(this.sessionFactory());
        sessionManager.setDeleteInvalidSessions(this.sessionManagerDeleteInvalidSessions);
        return sessionManager;
    }

    protected SessionsSecurityManager createSecurityManager() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setSubjectDAO(this.subjectDAO());
        securityManager.setSubjectFactory(this.subjectFactory());
        RememberMeManager rememberMeManager = this.rememberMeManager();
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
        subjectDAO.setSessionStorageEvaluator(this.sessionStorageEvaluator());
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
        if (this.permissionResolver != null) {
            authorizer.setPermissionResolver(this.permissionResolver);
        }

        if (this.rolePermissionResolver != null) {
            authorizer.setRolePermissionResolver(this.rolePermissionResolver);
        }

        return authorizer;
    }

    protected AuthenticationStrategy authenticationStrategy() {
        return new AtLeastOneSuccessfulStrategy();
    }

    protected Authenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(this.authenticationStrategy());
        return authenticator;
    }

    protected Realm iniRealmFromLocation(String iniLocation) {
        Ini ini = Ini.fromResourcePath(iniLocation);
        return new IniRealm(ini);
    }
}
