package org.noear.solon.admin.client.services;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.*;
import org.noear.solon.admin.client.config.IClientProperties;
import org.noear.solon.admin.client.data.Application;
import org.noear.solon.admin.client.utils.NetworkUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.net.URL;

@Slf4j
@Component
public class ApplicationRegistrationService {

    @Inject
    private Gson gson;

    @Inject
    private OkHttpClient client;

    @Inject("${solon.app.name}")
    private String applicationName;

    @Inject
    private IClientProperties properties;

    public void register() {
        log.info("Attempting to register this client as an application with Solon Admin server...");
        val serverUrl = this.properties.getServerUrl().replaceAll("/+$", "");
        try (Response response = client.newCall(new Request.Builder()
                .url(new URL(serverUrl + "/api/application/register"))
                .put(RequestBody.create(this.gson.toJson(
                        Application.builder()
                                .name(this.applicationName)
                                .baseUrl(NetworkUtils.getHostAndPort())
                                .metadata(properties.getMetadata())
                                .build()
                ), MediaType.parse("application/json")))
                .build()).execute()) {
            if (response.isSuccessful()) {
                log.info("Successfully register application to Solon Admin server.");
                return;
            }
            log.error("Failed to register application to Solon Admin server. Response: {}", response);
        } catch (Exception ex) {
            log.error("Unexpected error occurred during the application registration:", ex);
        }
    }

    public void unregister() {
        log.info("Attempting to unregister this client from Solon Admin server...");
        val serverUrl = this.properties.getServerUrl().replaceAll("/+$", "");
        try (Response response = client.newCall(new Request.Builder()
                .url(new URL(serverUrl + "/api/application/unregister"))
                .delete(RequestBody.create(this.gson.toJson(
                        Application.builder()
                                .name(this.applicationName)
                                .baseUrl(NetworkUtils.getHostAndPort())
                                .build()
                ), MediaType.parse("application/json")))
                .build()).execute()) {
            if (response.isSuccessful()) {
                log.info("Successfully unregister application from Solon Admin server.");
                return;
            }
            log.error("Failed to unregister application to Solon Admin server. Response: {}", response);
        } catch (Exception ex) {
            log.error("Unexpected error occurred during the application de-registration:", ex);
        }
    }

}
