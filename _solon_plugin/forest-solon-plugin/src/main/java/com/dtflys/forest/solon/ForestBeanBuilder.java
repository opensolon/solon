package com.dtflys.forest.solon;

import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.SolonForestProperties;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.auto.DefaultAutoConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.interceptor.SolonInterceptorFactory;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.reflection.SolonObjectFactory;
import com.dtflys.forest.solon.properties.ForestConfigurationProperties;
import com.dtflys.forest.solon.properties.ForestConvertProperties;
import com.dtflys.forest.solon.properties.ForestConverterItemProperties;
import com.dtflys.forest.solon.properties.ForestSSLKeyStoreProperties;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.ssl.SSLSocketFactoryBuilder;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.StringUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.TrustManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 1.11
 * */
public class ForestBeanBuilder {
    private SolonForestProperties properties;
    private SolonObjectFactory forestObjectFactory;

    private SolonInterceptorFactory forestInterceptorFactory;

    private ForestConfigurationProperties forestConfigurationProperties;

    public ForestBeanBuilder(
            ForestConfigurationProperties forestConfigurationProperties,
            SolonForestProperties properties,
            SolonObjectFactory forestObjectFactory,
            SolonInterceptorFactory forestInterceptorFactory) {
        this.forestConfigurationProperties = forestConfigurationProperties;
        this.properties = properties;
        this.forestObjectFactory = forestObjectFactory;
        this.forestInterceptorFactory = forestInterceptorFactory;
    }


    public ForestConfiguration build() {
        String id = forestConfigurationProperties.getBeanId();
        if (StringUtils.isBlank(id)) {
            id = "forestConfiguration";
            forestConfigurationProperties.setBeanId(id);
        }

        ForestConfiguration forestConfiguration = ForestConfiguration.createConfiguration();

        Class<? extends ForestLogHandler> logHandlerClass = forestConfigurationProperties.getLogHandler();
        ForestLogHandler logHandler = null;
        if (logHandlerClass != null) {
            try {
                logHandler = logHandlerClass.newInstance();
            } catch (InstantiationException e) {
                throw new ForestRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        }

        forestConfiguration.setMaxAsyncThreadSize(forestConfigurationProperties.getMaxAsyncThreadSize());
        forestConfiguration.setMaxAsyncQueueSize(forestConfigurationProperties.getMaxAsyncQueueSize());
        forestConfiguration.setMaxConnections(forestConfigurationProperties.getMaxConnections());
        forestConfiguration.setMaxRouteConnections(forestConfigurationProperties.getMaxRouteConnections());
        forestConfiguration.setAsyncMode(forestConfigurationProperties.getAsyncMode());
        forestConfiguration.setTimeout(forestConfigurationProperties.getTimeout());
        forestConfiguration.setConnectTimeout(forestConfigurationProperties.getConnectTimeoutMillis());
        forestConfiguration.setReadTimeout(forestConfigurationProperties.getReadTimeoutMillis());
        forestConfiguration.setCharset(forestConfigurationProperties.getCharset());
        forestConfiguration.setRetryer(forestConfigurationProperties.getRetryer());
        forestConfiguration.setMaxRetryCount(forestConfigurationProperties.getMaxRetryCount());
        forestConfiguration.setMaxRetryInterval(forestConfigurationProperties.getMaxRetryInterval());
        forestConfiguration.setAutoRedirection(forestConfigurationProperties.isAutoRedirection());
        forestConfiguration.setLogEnabled(forestConfigurationProperties.isLogEnabled());
        forestConfiguration.setLogRequest(forestConfigurationProperties.isLogRequest());
        forestConfiguration.setLogResponseStatus(forestConfigurationProperties.isLogResponseStatus());
        forestConfiguration.setLogResponseContent(forestConfigurationProperties.isLogResponseContent());
        forestConfiguration.setLogHandler(logHandler);
        forestConfiguration.setBackendName(forestConfigurationProperties.getBackend());
        forestConfiguration.setBaseAddressScheme(forestConfigurationProperties.getBaseAddressScheme());
        forestConfiguration.setBaseAddressHost(forestConfigurationProperties.getBaseAddressHost());
        forestConfiguration.setBaseAddressPort(forestConfigurationProperties.getBaseAddressPort());
        forestConfiguration.setBaseAddressSourceClass(forestConfigurationProperties.getBaseAddressSource());
        forestConfiguration.setSuccessWhenClass(forestConfigurationProperties.getSuccessWhen());
        forestConfiguration.setRetryWhenClass(forestConfigurationProperties.getRetryWhen());
        forestConfiguration.setInterceptors(forestConfigurationProperties.getInterceptors());
        forestConfiguration.setSslProtocol(forestConfigurationProperties.getSslProtocol());
        forestConfiguration.setVariables(forestConfigurationProperties.getVariables());


        List<ForestSSLKeyStoreProperties> sslKeyStorePropertiesList = forestConfigurationProperties.getSslKeyStores();
        Map<String, SSLKeyStore> sslKeystoreMap = new LinkedHashMap<>();
        for (ForestSSLKeyStoreProperties keyStoreProperties : sslKeyStorePropertiesList) {
            registerSSLKeyStoreBean(sslKeystoreMap, keyStoreProperties);
        }

        forestConfiguration.setSslKeyStores(sslKeystoreMap);



        ForestConfiguration configuration = forestConfiguration;
        configuration.setProperties(properties);
        configuration.setForestObjectFactory(forestObjectFactory);
        configuration.setInterceptorFactory(forestInterceptorFactory);

        Map<String, Class> filters = forestConfigurationProperties.getFilters();
        for (Map.Entry<String, Class> entry : filters.entrySet()) {
            String filterName = entry.getKey();
            Class filterClass = entry.getValue();
            configuration.registerFilter(filterName, filterClass);
        }

        ForestConvertProperties convertProperties = forestConfigurationProperties.getConverters();
        if (convertProperties != null) {
            registerConverter(configuration, ForestDataType.TEXT, convertProperties.getText());
            registerConverter(configuration, ForestDataType.JSON, convertProperties.getJson());
            registerConverter(configuration, ForestDataType.XML, convertProperties.getXml());
            registerConverter(configuration, ForestDataType.BINARY, convertProperties.getBinary());
            registerConverter(configuration, ForestDataType.PROTOBUF, convertProperties.getProtobuf());
        }

        return configuration;
    }


    private void registerConverter(ForestConfiguration configuration, ForestDataType dataType, ForestConverterItemProperties converterItemProperties) {
        if (converterItemProperties == null) {
            return;
        }

        Class type = converterItemProperties.getType();

        if (type != null) {
            ForestConverter converter = null;
            try {
                Constructor<?>[] constructors = type.getConstructors();
                for (Constructor<?> constructor : constructors) {
                    Parameter[] params = constructor.getParameters();
                    if (params.length == 0) {
                        converter = (ForestConverter) constructor.newInstance(new Object[0]);
                        break;
                    } else {
                        Object[] args = new Object[params.length];
                        Class[] pTypes = constructor.getParameterTypes();
                        for (int i = 0; i < params.length; i++) {
                            Class pType = pTypes[i];
                            if (ForestConfiguration.class.isAssignableFrom(pType)) {
                                args[i] = configuration;
                            } else if (DefaultAutoConverter.class.isAssignableFrom(pType)) {
                                args[i] = configuration.getConverter(ForestDataType.AUTO);
                            }
                        }
                        converter = (ForestConverter) constructor.newInstance(args);
                    }
                }

                Map<String, Object> parameters = converterItemProperties.getParameters();

                ClassWrap classWrap = ClassWrap.get(type);

                for (Map.Entry<String, FieldWrap> kv : classWrap.getFieldAllWraps().entrySet()) {
                    String name = kv.getKey();
                    Object value = parameters.get(name);
                    FieldWrap fw = kv.getValue();

                    if (value != null) {
                        try {
                            fw.setValue(converter, value, false);
                        } catch (Throwable e) {
                            e = Utils.throwableUnwrap(e);

                            if (e instanceof IllegalAccessException) {
                                throw new ForestRuntimeException("An error occurred during setting the property " + type.getName() + "." + name, e);
                            }

                            if (e instanceof InvocationTargetException) {
                                throw new ForestRuntimeException("An error occurred during setting the property " + type.getName() + "." + name, e);
                            }

                            throw new RuntimeException(e);
                        }

                    }
                }
                configuration.getConverterMap().put(dataType, converter);
            } catch (InstantiationException e) {
                throw new ForestRuntimeException("[Forest] Convert type '" + type.getName() + "' cannot be initialized!", e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException("[Forest] Convert type '" + type.getName() + "' cannot be initialized!", e);
            } catch (InvocationTargetException e) {
                throw new ForestRuntimeException("[Forest] Convert type '" + type.getName() + "' cannot be initialized!", e);
            }
        }
    }

    public void registerSSLKeyStoreBean(Map<String, SSLKeyStore> map, ForestSSLKeyStoreProperties sslKeyStoreProperties) {
        String id = sslKeyStoreProperties.getId();
        if (StringUtils.isBlank(id)) {
            throw new ForestRuntimeException("[Forest] Property 'id' of SSL keystore can not be empty or blank");
        }
        if (map.containsKey(id)) {
            throw new ForestRuntimeException("[Forest] Duplicate SSL keystore id '" + id + "'");
        }


        SSLKeyStore sslKeyStore = createSSLKeyStoreBean(id,
                sslKeyStoreProperties.getType(),
                sslKeyStoreProperties.getFile(),
                sslKeyStoreProperties.getKeystorePass(),
                sslKeyStoreProperties.getCertPass(),
                sslKeyStoreProperties.getProtocols(),
                sslKeyStoreProperties.getCipherSuites(),
                sslKeyStoreProperties.getTrustManager(),
                sslKeyStoreProperties.getHostnameVerifier(),
                sslKeyStoreProperties.getSslSocketFactoryBuilder());
        map.put(id, sslKeyStore);
    }

    public static SSLKeyStore createSSLKeyStoreBean(String id,
                                                    String keystoreType,
                                                    String filePath,
                                                    String keystorePass,
                                                    String certPass,
                                                    String protocolsStr,
                                                    String cipherSuitesStr,
                                                    String trustManagerClass,
                                                    String hostnameVerifierClass,
                                                    String sslSocketFactoryBuilderClass) {


        TrustManager trustManager = Utils.newInstance(trustManagerClass);
        HostnameVerifier hostnameVerifier = Utils.newInstance(hostnameVerifierClass);
        SSLSocketFactoryBuilder sslSocketFactoryBuilder = Utils.newInstance(sslSocketFactoryBuilderClass);

        SSLKeyStore sslKeyStore = new SSLKeyStore(id, keystoreType, filePath, keystorePass, certPass, trustManager, hostnameVerifier, sslSocketFactoryBuilder);

        if (StringUtils.isNotEmpty(protocolsStr)) {
            String[] strs = protocolsStr.split("[ /t]*,[ /t]*");
            String[] protocols = new String[strs.length];
            for (int i = 0; i < strs.length; i++) {
                protocols[i] = strs[i].trim();
            }
            sslKeyStore.setProtocols(protocols);
        }
        if (StringUtils.isNotEmpty(cipherSuitesStr)) {
            String[] strs = cipherSuitesStr.split("[ /t]*,[ /t]*");
            String[] cipherSuites = new String[strs.length];
            for (int i = 0; i < strs.length; i++) {
                cipherSuites[i] = strs[i].trim();
            }
            sslKeyStore.setCipherSuites(cipherSuites);
        }
        return sslKeyStore;
    }
}
