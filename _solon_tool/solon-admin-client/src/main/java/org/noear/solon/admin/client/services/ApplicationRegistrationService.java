package org.noear.solon.admin.client.services;

import com.google.gson.Gson;
import lombok.val;
import okhttp3.*;
import org.noear.solon.admin.client.config.IClientProperties;
import org.noear.solon.admin.client.data.Application;
import org.noear.solon.admin.client.utils.NetworkUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.io.IOException;
import java.net.URL;

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

    public boolean register() throws IOException {
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
            return response.isSuccessful();
        }
    }

    public boolean unregister() throws IOException {
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
            return response.isSuccessful();
        }
    }

}
