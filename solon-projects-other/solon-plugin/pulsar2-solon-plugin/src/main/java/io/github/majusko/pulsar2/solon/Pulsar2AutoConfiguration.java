package io.github.majusko.pulsar2.solon;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.ConsumerInterceptor;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

import com.google.common.base.Strings;

import io.github.majusko.pulsar2.solon.error.exception.ClientInitException;
import io.github.majusko.pulsar2.solon.properties.PulsarProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class Pulsar2AutoConfiguration {
    static final Logger log = LoggerFactory.getLogger(Pulsar2AutoConfiguration.class);

    @Bean
    @Condition(onMissingBean = PulsarClient.class)
    public PulsarClient pulsarClient(PulsarProperties pulsarProperties) throws PulsarClientException, ClientInitException, MalformedURLException {
        if (!Strings.isNullOrEmpty(pulsarProperties.getTlsAuthCertFilePath()) &&
            !Strings.isNullOrEmpty(pulsarProperties.getTlsAuthKeyFilePath()) &&
            !Strings.isNullOrEmpty(pulsarProperties.getTokenAuthValue())
        ) {
        	String eg = "You cannot use multiple auth options.";
            log.error(eg);
        	throw new ClientInitException(eg);
        }

        final ClientBuilder pulsarClientBuilder = PulsarClient.builder()
            .serviceUrl(pulsarProperties.getServiceUrl())
            .ioThreads(pulsarProperties.getIoThreads())
            .listenerThreads(pulsarProperties.getListenerThreads())
            .enableTcpNoDelay(pulsarProperties.isEnableTcpNoDelay())
            .keepAliveInterval(pulsarProperties.getKeepAliveIntervalSec(), TimeUnit.SECONDS)
            .connectionTimeout(pulsarProperties.getConnectionTimeoutSec(), TimeUnit.SECONDS)
            .operationTimeout(pulsarProperties.getOperationTimeoutSec(), TimeUnit.SECONDS)
            .startingBackoffInterval(pulsarProperties.getStartingBackoffIntervalMs(), TimeUnit.MILLISECONDS)
            .maxBackoffInterval(pulsarProperties.getMaxBackoffIntervalSec(), TimeUnit.SECONDS)
            .useKeyStoreTls(pulsarProperties.isUseKeyStoreTls())
            .tlsTrustCertsFilePath(pulsarProperties.getTlsTrustCertsFilePath())
            .tlsCiphers(pulsarProperties.getTlsCiphers())
            .tlsProtocols(pulsarProperties.getTlsProtocols())
            .tlsTrustStorePassword(pulsarProperties.getTlsTrustStorePassword())
            .tlsTrustStorePath(pulsarProperties.getTlsTrustStorePath())
            .tlsTrustStoreType(pulsarProperties.getTlsTrustStoreType())
            .allowTlsInsecureConnection(pulsarProperties.isAllowTlsInsecureConnection())
            .enableTlsHostnameVerification(pulsarProperties.isEnableTlsHostnameVerification());

        if (!Strings.isNullOrEmpty(pulsarProperties.getTlsAuthCertFilePath()) &&
            !Strings.isNullOrEmpty(pulsarProperties.getTlsAuthKeyFilePath())) {
            pulsarClientBuilder.authentication(AuthenticationFactory
                .TLS(pulsarProperties.getTlsAuthCertFilePath(), pulsarProperties.getTlsAuthKeyFilePath()));
        }

        if (!Strings.isNullOrEmpty(pulsarProperties.getTokenAuthValue())) {
            pulsarClientBuilder.authentication(AuthenticationFactory
                .token(pulsarProperties.getTokenAuthValue()));
        }

        if (!Strings.isNullOrEmpty(pulsarProperties.getOauth2Audience()) &&
            !Strings.isNullOrEmpty(pulsarProperties.getOauth2IssuerUrl()) &&
            !Strings.isNullOrEmpty(pulsarProperties.getOauth2CredentialsUrl())) {
            final URL issuerUrl = new URL(pulsarProperties.getOauth2IssuerUrl());
            final URL credentialsUrl = new URL(pulsarProperties.getOauth2CredentialsUrl());

            pulsarClientBuilder.authentication(AuthenticationFactoryOAuth2
                .clientCredentials(issuerUrl, credentialsUrl, pulsarProperties.getOauth2Audience()));
        }
        if (!Strings.isNullOrEmpty(pulsarProperties.getListenerName())) {
            pulsarClientBuilder.listenerName(pulsarProperties.getListenerName());
        }

        return pulsarClientBuilder.build();
    }
}
