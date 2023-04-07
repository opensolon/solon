package io.github.majusko.pulsar2.solon.properties;

import java.util.HashSet;
import java.util.Set;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;


/**
 * pulsar2 基础配置,会与Producer生产者、Consumer消费者配置组合
 * @author Administrator
 *
 */
@Inject("${solon.pulsar2}")
@Configuration
public class PulsarProperties {
    private String serviceUrl = "pulsar://localhost:6650";
    private Integer ioThreads = 10;
    private Integer listenerThreads = 10;
    private boolean enableTcpNoDelay = false;
    private Integer keepAliveIntervalSec = 20;
    private Integer connectionTimeoutSec = 10;
    private Integer operationTimeoutSec = 15;
    private Integer startingBackoffIntervalMs = 100;
    private Integer maxBackoffIntervalSec = 10;
    private String consumerNameDelimiter = "";//消费者名称分隔符
    private String namespace = "default";
    private String tenant = "public";
    private String tlsTrustCertsFilePath = null;
    private Set<String> tlsCiphers = new HashSet<>();
    private Set<String> tlsProtocols = new HashSet<>();
    private String tlsTrustStorePassword = null;
    private String tlsTrustStorePath = null;
    private String tlsTrustStoreType = null;
    private boolean useKeyStoreTls = false;
    private boolean allowTlsInsecureConnection = false;
    private boolean enableTlsHostnameVerification = false;
    private String tlsAuthCertFilePath = null;
    private String tlsAuthKeyFilePath = null;
    private String tokenAuthValue = null;
    private String oauth2IssuerUrl = null;
    private String oauth2CredentialsUrl = null;
    private String oauth2Audience = null;
    private boolean autoStart = true;
    private boolean allowInterceptor = false;
    private String listenerName = null;

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public Integer getIoThreads() {
        return ioThreads;
    }

    public void setIoThreads(Integer ioThreads) {
        this.ioThreads = ioThreads;
    }

    public Integer getListenerThreads() {
        return listenerThreads;
    }

    public void setListenerThreads(Integer listenerThreads) {
        this.listenerThreads = listenerThreads;
    }

    public boolean isEnableTcpNoDelay() {
        return enableTcpNoDelay;
    }

    public void setEnableTcpNoDelay(boolean enableTcpNoDelay) {
        this.enableTcpNoDelay = enableTcpNoDelay;
    }

    public Integer getKeepAliveIntervalSec() {
        return keepAliveIntervalSec;
    }

    public void setKeepAliveIntervalSec(Integer keepAliveIntervalSec) {
        this.keepAliveIntervalSec = keepAliveIntervalSec;
    }

    public Integer getConnectionTimeoutSec() {
        return connectionTimeoutSec;
    }

    public void setConnectionTimeoutSec(Integer connectionTimeoutSec) {
        this.connectionTimeoutSec = connectionTimeoutSec;
    }

    public Integer getOperationTimeoutSec() {
        return operationTimeoutSec;
    }

    public void setOperationTimeoutSec(Integer operationTimeoutSec) {
        this.operationTimeoutSec = operationTimeoutSec;
    }

    public Integer getStartingBackoffIntervalMs() {
        return startingBackoffIntervalMs;
    }

    public void setStartingBackoffIntervalMs(Integer startingBackoffIntervalMs) {
        this.startingBackoffIntervalMs = startingBackoffIntervalMs;
    }

    public Integer getMaxBackoffIntervalSec() {
        return maxBackoffIntervalSec;
    }

    public void setMaxBackoffIntervalSec(Integer maxBackoffIntervalSec) {
        this.maxBackoffIntervalSec = maxBackoffIntervalSec;
    }

    public String getConsumerNameDelimiter() {
        return consumerNameDelimiter;
    }

    public void setConsumerNameDelimiter(String consumerNameDelimiter) {
        this.consumerNameDelimiter = consumerNameDelimiter;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getTlsTrustCertsFilePath() {
        return tlsTrustCertsFilePath;
    }

    public void setTlsTrustCertsFilePath(String tlsTrustCertsFilePath) {
        this.tlsTrustCertsFilePath = tlsTrustCertsFilePath;
    }

    public Set<String> getTlsCiphers() {
        return tlsCiphers;
    }

    public void setTlsCiphers(Set<String> tlsCiphers) {
        this.tlsCiphers = tlsCiphers;
    }

    public Set<String> getTlsProtocols() {
        return tlsProtocols;
    }

    public void setTlsProtocols(Set<String> tlsProtocols) {
        this.tlsProtocols = tlsProtocols;
    }

    public String getTlsTrustStorePassword() {
        return tlsTrustStorePassword;
    }

    public void setTlsTrustStorePassword(String tlsTrustStorePassword) {
        this.tlsTrustStorePassword = tlsTrustStorePassword;
    }

    public String getTlsTrustStorePath() {
        return tlsTrustStorePath;
    }

    public void setTlsTrustStorePath(String tlsTrustStorePath) {
        this.tlsTrustStorePath = tlsTrustStorePath;
    }

    public String getTlsTrustStoreType() {
        return tlsTrustStoreType;
    }

    public void setTlsTrustStoreType(String tlsTrustStoreType) {
        this.tlsTrustStoreType = tlsTrustStoreType;
    }

    public boolean isUseKeyStoreTls() {
        return useKeyStoreTls;
    }

    public void setUseKeyStoreTls(boolean useKeyStoreTls) {
        this.useKeyStoreTls = useKeyStoreTls;
    }

    public boolean isAllowTlsInsecureConnection() {
        return allowTlsInsecureConnection;
    }

    public void setAllowTlsInsecureConnection(boolean allowTlsInsecureConnection) {
        this.allowTlsInsecureConnection = allowTlsInsecureConnection;
    }

    public boolean isEnableTlsHostnameVerification() {
        return enableTlsHostnameVerification;
    }

    public void setEnableTlsHostnameVerification(boolean enableTlsHostnameVerification) {
        this.enableTlsHostnameVerification = enableTlsHostnameVerification;
    }

    public String getTlsAuthCertFilePath() {
        return tlsAuthCertFilePath;
    }

    public void setTlsAuthCertFilePath(String tlsAuthCertFilePath) {
        this.tlsAuthCertFilePath = tlsAuthCertFilePath;
    }

    public String getTlsAuthKeyFilePath() {
        return tlsAuthKeyFilePath;
    }

    public void setTlsAuthKeyFilePath(String tlsAuthKeyFilePath) {
        this.tlsAuthKeyFilePath = tlsAuthKeyFilePath;
    }

    public String getTokenAuthValue() {
        return tokenAuthValue;
    }

    public void setTokenAuthValue(String tokenAuthValue) {
        this.tokenAuthValue = tokenAuthValue;
    }

    public String getOauth2IssuerUrl() {
        return oauth2IssuerUrl;
    }

    public void setOauth2IssuerUrl(String oauth2IssuerUrl) {
        this.oauth2IssuerUrl = oauth2IssuerUrl;
    }

    public String getOauth2CredentialsUrl() {
        return oauth2CredentialsUrl;
    }

    public void setOauth2CredentialsUrl(String oauth2CredentialsUrl) {
        this.oauth2CredentialsUrl = oauth2CredentialsUrl;
    }

    public String getOauth2Audience() {
        return oauth2Audience;
    }

    public void setOauth2Audience(String oauth2Audience) {
        this.oauth2Audience = oauth2Audience;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public boolean isAllowInterceptor() {
        return allowInterceptor;
    }

    public void setAllowInterceptor(boolean allowInterceptor) {
        this.allowInterceptor = allowInterceptor;
    }

    public String getListenerName() {
        return listenerName;
    }

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }
}