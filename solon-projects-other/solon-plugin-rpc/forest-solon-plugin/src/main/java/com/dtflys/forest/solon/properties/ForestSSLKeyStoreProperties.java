package com.dtflys.forest.solon.properties;

import java.security.KeyStore;

/**
 * @author noear
 * @since 1.11
 */
public class ForestSSLKeyStoreProperties {

    protected String id;

    protected String type = "jks";

    protected String file;

    protected String keystorePass;

    protected String certPass;

    protected KeyStore trustStore;

    protected String protocols;

    protected String cipherSuites;

    protected String trustManager;

    protected String hostnameVerifier;

    protected String sslSocketFactoryBuilder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    public String getCertPass() {
        return certPass;
    }

    public void setCertPass(String certPass) {
        this.certPass = certPass;
    }

    public KeyStore getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(KeyStore trustStore) {
        this.trustStore = trustStore;
    }

    public String getProtocols() {
        return protocols;
    }

    public void setProtocols(String protocols) {
        this.protocols = protocols;
    }

    public String getCipherSuites() {
        return cipherSuites;
    }

    public void setCipherSuites(String cipherSuites) {
        this.cipherSuites = cipherSuites;
    }

    public String getTrustManager() {
        return trustManager;
    }

    public void setTrustManager(String trustManager) {
        this.trustManager = trustManager;
    }

    public String getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setHostnameVerifier(String hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public String getSslSocketFactoryBuilder() {
        return sslSocketFactoryBuilder;
    }

    public void setSslSocketFactoryBuilder(String sslSocketFactoryBuilder) {
        this.sslSocketFactoryBuilder = sslSocketFactoryBuilder;
    }
}