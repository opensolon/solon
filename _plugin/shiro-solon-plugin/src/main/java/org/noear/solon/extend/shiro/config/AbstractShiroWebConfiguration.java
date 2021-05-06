package org.noear.solon.extend.shiro.config;

import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2021/5/6 created
 */
public class AbstractShiroWebConfiguration extends AbstractShiroConfiguration{
    @Inject("${shiro.sessionManager.sessionIdCookieEnabled:true}")
    protected boolean sessionIdCookieEnabled;
    @Inject("${shiro.sessionManager.sessionIdUrlRewritingEnabled:false}")
    protected boolean sessionIdUrlRewritingEnabled;
    @Inject("${shiro.userNativeSessionManager:false}")
    protected boolean useNativeSessionManager;
    @Inject("${shiro.sessionManager.cookie.name}")
    protected String sessionIdCookieName = ShiroHttpSession.DEFAULT_SESSION_ID_NAME;
    @Inject("${shiro.sessionManager.cookie.maxAge}")
    protected int sessionIdCookieMaxAge=SimpleCookie.DEFAULT_MAX_AGE;
    @Inject("${shiro.sessionManager.cookie.domain}")
    protected String sessionIdCookieDomain;
    @Inject("${shiro.sessionManager.cookie.path}")
    protected String sessionIdCookiePath;
    @Inject("${shiro.sessionManager.cookie.secure:false}")
    protected boolean sessionIdCookieSecure;
    @Inject("${shiro.sessionManager.cookie.sameSite}")
    protected Cookie.SameSiteOptions sessionIdCookieSameSite = Cookie.SameSiteOptions.LAX;
    @Inject("${shiro.rememberMeManager.cookie.name}")
    protected String rememberMeCookieName = CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME;
    @Inject("${shiro.rememberMeManager.cookie.maxAge}")
    protected int rememberMeCookieMaxAge = Cookie.ONE_YEAR;
    @Inject("${shiro.rememberMeManager.cookie.domain}")
    protected String rememberMeCookieDomain;
    @Inject("${shiro.rememberMeManager.cookie.path}")
    protected String rememberMeCookiePath;
    @Inject("${shiro.rememberMeManager.cookie.secure:false}")
    protected boolean rememberMeCookieSecure;

    protected Cookie.SameSiteOptions rememberMeSameSite = Cookie.SameSiteOptions.LAX;

    public AbstractShiroWebConfiguration() {
    }

    protected SessionManager nativeSessionManager() {
        DefaultWebSessionManager webSessionManager = new DefaultWebSessionManager();
        webSessionManager.setSessionIdCookieEnabled(this.sessionIdCookieEnabled);
        webSessionManager.setSessionIdUrlRewritingEnabled(this.sessionIdUrlRewritingEnabled);
        webSessionManager.setSessionIdCookie(this.sessionCookieTemplate());
        webSessionManager.setSessionFactory(this.sessionFactory());
        webSessionManager.setSessionDAO(this.sessionDAO());
        webSessionManager.setDeleteInvalidSessions(this.sessionManagerDeleteInvalidSessions);
        return webSessionManager;
    }

    protected Cookie sessionCookieTemplate() {
        return this.buildCookie(this.sessionIdCookieName, this.sessionIdCookieMaxAge, this.sessionIdCookiePath, this.sessionIdCookieDomain, this.sessionIdCookieSecure, this.sessionIdCookieSameSite);
    }

    protected Cookie rememberMeCookieTemplate() {
        return this.buildCookie(this.rememberMeCookieName, this.rememberMeCookieMaxAge, this.rememberMeCookiePath, this.rememberMeCookieDomain, this.rememberMeCookieSecure, this.rememberMeSameSite);
    }

    protected Cookie buildCookie(String name, int maxAge, String path, String domain, boolean secure) {
        return this.buildCookie(name, maxAge, path, domain, secure, Cookie.SameSiteOptions.LAX);
    }

    protected Cookie buildCookie(String name, int maxAge, String path, String domain, boolean secure, Cookie.SameSiteOptions sameSiteOption) {
        Cookie cookie = new SimpleCookie(name);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setSecure(secure);
        cookie.setSameSite(sameSiteOption);
        return cookie;
    }

    protected SessionManager sessionManager() {
        return (this.useNativeSessionManager ? this.nativeSessionManager() : new ServletContainerSessionManager());
    }

    protected RememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(this.rememberMeCookieTemplate());
        return cookieRememberMeManager;
    }

    protected SubjectFactory subjectFactory() {
        return new DefaultWebSubjectFactory();
    }

    protected SessionStorageEvaluator sessionStorageEvaluator() {
        return new DefaultWebSessionStorageEvaluator();
    }

    protected SessionsSecurityManager createSecurityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSubjectDAO(this.subjectDAO());
        securityManager.setSubjectFactory(this.subjectFactory());
        securityManager.setRememberMeManager(this.rememberMeManager());
        return securityManager;
    }

//    protected ShiroFilterChainDefinition shiroFilterChainDefinition() {
//        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
//        chainDefinition.addPathDefinition("/**", "authc");
//        return chainDefinition;
//    }
//
//    protected ShiroUrlPathHelper shiroUrlPathHelper() {
//        return new ShiroUrlPathHelper();
//    }
}
