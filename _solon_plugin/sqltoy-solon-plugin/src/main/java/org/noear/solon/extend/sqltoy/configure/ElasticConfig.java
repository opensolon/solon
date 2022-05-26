package org.noear.solon.extend.sqltoy.configure;

import java.io.Serializable;

/**
 * @description 提供es基于http连接的配置，2021年开始有大量基于jdbc的模式，可不再使用
 * @author zhongxuchen
 * @version v1.0,Date:2020年2月20日
 */
public class ElasticConfig implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5753295867761297803L;

    /**
     * 连接赋予的id
     */
    private String id;

    /**
     * 连接url地址
     */
    private String url;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 相对路径
     */
    private String sqlPath;

    /**
     * 是否禁止抢占式身份认证
     */
    private boolean authCaching=true;

    /**
     * 证书类型
     */
    private String keyStoreType;

    /**
     * 证书文件
     */
    private String keyStore;

    /**
     * 证书秘钥
     */
    private String keyStorePass;

    /**
     * 证书是否自签名
     */
    private boolean keyStoreSelfSign = true;

    private Integer requestTimeout;

    private Integer connectTimeout;

    private Integer socketTimeout;

    /**
     * 字符集,默认UTF-8
     */
    private String charset;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSqlPath() {
        return sqlPath;
    }

    public void setSqlPath(String sqlPath) {
        this.sqlPath = sqlPath;
    }

    /**
     * @return the keyStoreType
     */
    public String getKeyStoreType() {
        return keyStoreType;
    }

    /**
     * @param keyStoreType the keyStoreType to set
     */
    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    /**
     * @return the keyStore
     */
    public String getKeyStore() {
        return keyStore;
    }

    /**
     * @param keyStore the keyStore to set
     */
    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    /**
     * @return the keyStorePass
     */
    public String getKeyStorePass() {
        return keyStorePass;
    }

    /**
     * @param keyStorePass the keyStorePass to set
     */
    public void setKeyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
    }

    /**
     * @return the keyStoreSelfSign
     */
    public boolean isKeyStoreSelfSign() {
        return keyStoreSelfSign;
    }

    /**
     * @param keyStoreSelfSign the keyStoreSelfSign to set
     */
    public void setKeyStoreSelfSign(boolean keyStoreSelfSign) {
        this.keyStoreSelfSign = keyStoreSelfSign;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @return the authCaching
     */
    public boolean isAuthCaching() {
        return authCaching;
    }

    /**
     * @param authCaching the authCaching to set
     */
    public void setAuthCaching(boolean authCaching) {
        this.authCaching = authCaching;
    }

}
