package org.noear.solon.extend.shiro.config;

import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.servlet.Cookie;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

import java.util.List;

/**
 * @author noear 2021/5/6 created
 */
@Configuration
public class ShiroWebConfiguration extends AbstractShiroWebConfiguration{
    public ShiroWebConfiguration() {
    }

    @Bean
    protected SubjectDAO subjectDAO() {
        return super.subjectDAO();
    }

    @Bean
    protected SessionStorageEvaluator sessionStorageEvaluator() {
        return super.sessionStorageEvaluator();
    }

    @Bean
    protected SessionFactory sessionFactory() {
        return super.sessionFactory();
    }

    @Bean
    protected SessionDAO sessionDAO() {
        return super.sessionDAO();
    }

    @Bean("sessionCookieTemplate")
    protected Cookie sessionCookieTemplate() {
        return super.sessionCookieTemplate();
    }

    @Bean("rememberMeCookieTemplate")
    protected Cookie rememberMeCookieTemplate() {
        return super.rememberMeCookieTemplate();
    }

    @Bean
    protected RememberMeManager rememberMeManager() {
        return super.rememberMeManager();
    }

    @Bean
    protected SubjectFactory subjectFactory() {
        return super.subjectFactory();
    }

    @Bean
    protected Authorizer authorizer() {
        return super.authorizer();
    }

    @Bean
    protected AuthenticationStrategy authenticationStrategy() {
        return super.authenticationStrategy();
    }

    @Bean
    protected Authenticator authenticator() {
        return super.authenticator();
    }

    @Bean
    protected SessionManager sessionManager() {
        return super.sessionManager();
    }

    @Bean
    protected SessionsSecurityManager securityManager(List<Realm> realms) {
        return super.securityManager(realms);
    }

//    @Bean
//    protected ShiroFilterChainDefinition shiroFilterChainDefinition() {
//        return super.shiroFilterChainDefinition();
//    }
//
//    @Bean
//    protected ShiroUrlPathHelper shiroUrlPathHelper() {
//        return super.shiroUrlPathHelper();
//    }
}
