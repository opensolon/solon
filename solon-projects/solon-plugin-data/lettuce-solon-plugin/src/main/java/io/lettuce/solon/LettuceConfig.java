package io.lettuce.solon;

import io.lettuce.core.RedisCredentialsProvider;
import io.lettuce.core.SslVerifyMode;

import java.util.List;

import static io.lettuce.core.RedisURI.DEFAULT_REDIS_PORT;


/**
 * Lettuce配置
 *
 * @author Sorghum
 * @since 2.4
 */
public class LettuceConfig{

    private String host;

    private String socket;

    private String sentinelMasterId;

    private int port = DEFAULT_REDIS_PORT;

    private int database;

    private String clientName;

    private String username;

    private String password;

    private RedisCredentialsProvider credentialsProvider;

    private boolean ssl = false;

    private SslVerifyMode verifyMode = SslVerifyMode.FULL;

    private boolean startTls = false;

    private Long timeout = 60L;

    private List<LettuceSentinel> sentinels;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public String getSentinelMasterId() {
        return sentinelMasterId;
    }

    public void setSentinelMasterId(String sentinelMasterId) {
        this.sentinelMasterId = sentinelMasterId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public RedisCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public void setCredentialsProvider(RedisCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }


    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public SslVerifyMode getVerifyMode() {
        return verifyMode;
    }

    public void setVerifyMode(SslVerifyMode verifyMode) {
        this.verifyMode = verifyMode;
    }

    public boolean isStartTls() {
        return startTls;
    }

    public void setStartTls(boolean startTls) {
        this.startTls = startTls;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public List<LettuceSentinel> getSentinels() {
        return sentinels;
    }

    public void setSentinels(List<LettuceSentinel> sentinels) {
        this.sentinels = sentinels;
    }

}
