package com.dtflys.forest.solon.properties;

import com.dtflys.forest.callback.AddressSource;
import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.http.ForestAsyncMode;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.retryer.BackOffRetryer;
import com.dtflys.forest.ssl.SSLUtils;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 1.1
 */
@Inject("${forest}")
@Configuration
public class ForestConfigurationProperties {

    /**
     * Spring bean id of forest configuration
     */
    private String beanId;

    /**
     * Maximum number of conntections allowed
     */
    private int maxConnections = 500;

    /**
     * Maximum number of connections allowed per route
     */
    private int maxRouteConnections = 500;


    /**
     * Maximum number of requests queue
     */
    private int maxRequestQueueSize = 200;

    /**
     * Maximum number of async requests threads
     */
    private int maxAsyncThreadSize = 200;

    /**
     * Capacity of async requests queue
     */
    private int maxAsyncQueueSize = 100;

    /**
     * Parallel mode of async requests
     */
    private ForestAsyncMode asyncMode = ForestAsyncMode.PLATFORM;

    /**
     * Timeout in milliseconds
     */
    private int timeout = 3000;

    /**
     * Connect timeout in milliseconds
     */
    private Integer connectTimeout = null;

    /**
     * Read timeout in milliseconds
     */
    private Integer readTimeout = null;

    /**
     * Request charset
     */
    private String charset = "UTF-8";

    /**
     * Global default http scheme
     * <p>it can be:
     * <ul>
     *     <li>http</li>
     *     <li>https</li>
     * </ul>
     */
    private String baseAddressScheme;

    /**
     * Global default host
     */
    private String baseAddressHost;

    /**
     * Global default port
     */
    private Integer baseAddressPort;

    /**
     * The source of global default address
     */
    private Class<? extends AddressSource> baseAddressSource;

    /**
     * Class of retryer
     */
    private Class retryer = BackOffRetryer.class;

    /**
     * Max count of retry times
     */
    private Integer maxRetryCount = 0;

    /**
     * Max interval of retrying request
     */
    private long maxRetryInterval = 0;

    /**
     * Enable auto redirection
     */
    private boolean autoRedirection = true;

    /**
     * Enable print log of request/response
     */
    private boolean logEnabled = true;

    /**
     * Enable print log of request
     */
    private boolean logRequest = true;

    /**
     * Enable print log of response status
     */
    private boolean logResponseStatus = true;

    /**
     * Enable print log of response content
     */
    private boolean logResponseContent = false;

    /**
     * Class of log handler
     */
    private Class<? extends ForestLogHandler> logHandler = DefaultLogHandler.class;


    /**
     * Default SSL protocol for https requests
     */
    private String sslProtocol = SSLUtils.TLS_1_2;

    /**
     * Backend HTTP framework of forest, following backend can be chosen:
     * <ul>
     *     <li>httpclient</li>
     *     <li>okhttp3</li>
     * </ul>
     */
    private String backend = "okhttp3";

    /**
     * global variables
     */
    private Map<String, Object> variables = new HashMap<>();

    /**
     * Class list of interceptors
     */
    private List<Class<? extends Interceptor>> interceptors = new ArrayList<>();

    /**
     * Success When callback function: used to judge whether the request is successful
     */
    private Class<? extends SuccessWhen> successWhen;

    /**
     * Retry When callback function: used to determine whether to trigger a retry request
     */
    private Class<? extends RetryWhen> retryWhen;

    /**
     * SSL Key Stores
     */
    private List<ForestSSLKeyStoreProperties> sslKeyStores = new ArrayList<>();

    private ForestConvertProperties converters = new ForestConvertProperties();

    private Map<String, Class> filters = new HashMap<>();

/*
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
*/

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxRouteConnections() {
        return maxRouteConnections;
    }

    public void setMaxRouteConnections(int maxRouteConnections) {
        this.maxRouteConnections = maxRouteConnections;
    }

    public int getMaxRequestQueueSize() {
        return maxRequestQueueSize;
    }

    public void setMaxRequestQueueSize(int maxRequestQueueSize) {
        this.maxRequestQueueSize = maxRequestQueueSize;
    }

    public int getMaxAsyncThreadSize() {
        return maxAsyncThreadSize;
    }

    public void setMaxAsyncThreadSize(int maxAsyncThreadSize) {
        this.maxAsyncThreadSize = maxAsyncThreadSize;
    }

    public int getMaxAsyncQueueSize() {
        return maxAsyncQueueSize;
    }

    public void setMaxAsyncQueueSize(int maxAsyncQueueSize) {
        this.maxAsyncQueueSize = maxAsyncQueueSize;
    }

    public ForestAsyncMode getAsyncMode() {
        return asyncMode;
    }

    public void setAsyncMode(ForestAsyncMode asyncMode) {
        this.asyncMode = asyncMode;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public Integer getConnectTimeoutMillis() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public Integer getReadTimeoutMillis() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getBaseAddressScheme() {
        return baseAddressScheme;
    }

    public void setBaseAddressScheme(String baseAddressScheme) {
        this.baseAddressScheme = baseAddressScheme;
    }

    public String getBaseAddressHost() {
        return baseAddressHost;
    }

    public void setBaseAddressHost(String baseAddressHost) {
        this.baseAddressHost = baseAddressHost;
    }

    public Integer getBaseAddressPort() {
        return baseAddressPort;
    }

    public void setBaseAddressPort(Integer baseAddressPort) {
        this.baseAddressPort = baseAddressPort;
    }

    public Class<? extends AddressSource> getBaseAddressSource() {
        return baseAddressSource;
    }

    public void setBaseAddressSource(Class<? extends AddressSource> baseAddressSource) {
        this.baseAddressSource = baseAddressSource;
    }

    public Class getRetryer() {
        return retryer;
    }

    public void setRetryer(Class retryer) {
        this.retryer = retryer;
    }

    @Deprecated
    public int getRetryCount() {
        return maxRetryCount;
    }

    @Deprecated
    public void setRetryCount(int retryCount) {
        this.maxRetryCount = retryCount;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int retryCount) {
        this.maxRetryCount = retryCount;
    }


    public long getMaxRetryInterval() {
        return maxRetryInterval;
    }

    public void setMaxRetryInterval(long maxRetryInterval) {
        this.maxRetryInterval = maxRetryInterval;
    }

    public boolean isAutoRedirection() {
        return autoRedirection;
    }

    public void setAutoRedirection(boolean autoRedirection) {
        this.autoRedirection = autoRedirection;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public boolean isLogRequest() {
        return logRequest;
    }

    public void setLogRequest(boolean logRequest) {
        this.logRequest = logRequest;
    }

    public boolean isLogResponseStatus() {
        return logResponseStatus;
    }

    public void setLogResponseStatus(boolean logResponseStatus) {
        this.logResponseStatus = logResponseStatus;
    }

    public boolean isLogResponseContent() {
        return logResponseContent;
    }

    public void setLogResponseContent(boolean logResponseContent) {
        this.logResponseContent = logResponseContent;
    }

    public Class<? extends ForestLogHandler> getLogHandler() {
        return logHandler;
    }

    public void setLogHandler(Class<? extends ForestLogHandler> logHandler) {
        this.logHandler = logHandler;
    }

    public String getSslProtocol() {
        return sslProtocol;
    }

    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public List<Class<? extends Interceptor>> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<Class<? extends Interceptor>> interceptors) {
        this.interceptors = interceptors;
    }

    public Class<? extends SuccessWhen> getSuccessWhen() {
        return successWhen;
    }

    public void setSuccessWhen(Class<? extends SuccessWhen> successWhen) {
        this.successWhen = successWhen;
    }

    public Class<? extends RetryWhen> getRetryWhen() {
        return retryWhen;
    }

    public void setRetryWhen(Class<? extends RetryWhen> retryWhen) {
        this.retryWhen = retryWhen;
    }

    public List<ForestSSLKeyStoreProperties> getSslKeyStores() {
        return sslKeyStores;
    }

    public void setSslKeyStores(List<ForestSSLKeyStoreProperties> sslKeyStores) {
        this.sslKeyStores = sslKeyStores;
    }

    public ForestConvertProperties getConverters() {
        return converters;
    }

    public void setConverters(ForestConvertProperties converters) {
        this.converters = converters;
    }

    public Map<String, Class> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Class> filters) {
        this.filters = filters;
    }
}
