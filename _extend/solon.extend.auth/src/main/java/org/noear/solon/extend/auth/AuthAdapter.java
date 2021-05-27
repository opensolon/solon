package org.noear.solon.extend.auth;

/**
 * @author noear 2021/5/27 created
 */
public class AuthAdapter {
    private String loginUrl;
    private String loginProcessingUrl;
    private String usernameParameter;
    private String passwordParameter;
    private String logoutUrl;
    private String excludeMatchers;

    public AuthAdapter loginUrl(String url){
        loginUrl = url;
        return this;
    }

    public AuthAdapter loginProcessingUrl(String url){
        loginProcessingUrl = url;
        return this;
    }

    public AuthAdapter usernameParameter(String name){
        usernameParameter = name;
        return this;
    }

    public AuthAdapter passwordParameter(String name){
        passwordParameter = name;
        return this;
    }

    public AuthAdapter logoutUrl(String url){
        logoutUrl = url;
        return this;
    }

    public AuthAdapter excludeMatchers(String expr){
        excludeMatchers = expr;
        return this;
    }



}
