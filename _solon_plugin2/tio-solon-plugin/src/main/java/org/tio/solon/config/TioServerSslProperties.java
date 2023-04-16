package org.tio.solon.config;


/**
 *
 * @since 2.2
 * */
public class TioServerSslProperties {
    /**
     * ssl keyStore路径
     */
    private String keyStore;

    /**
     * ssl keyStore或trustStore密钥
     */
    private String password;

    /**
     * ssl trustStore路径
     */
    private String trustStore;


    @Override
    public String toString() {
        return "keyStore:" + keyStore + "\n trustStore:" + trustStore;
    }


    public String getKeyStore() {
        return keyStore;
    }


    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getTrustStore() {
        return trustStore;
    }


    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

}
