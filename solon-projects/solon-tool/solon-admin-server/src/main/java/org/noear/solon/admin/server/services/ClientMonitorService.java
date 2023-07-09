package org.noear.solon.admin.server.services;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.noear.solon.admin.server.data.Application;
import org.noear.solon.admin.server.data.Detector;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.net.URL;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Component
public class ClientMonitorService {

    @Inject
    private Gson gson;

    @Inject
    private OkHttpClient client;

    public Collection<Detector> getMonitors(Application application) {
        val clientUrl = application.getBaseUrl().replaceAll("/+$", "");
        try (Response response = client.newCall(new Request.Builder()
                .url(new URL(clientUrl + "/api/monitor/all"))
                .get()
                .build()).execute()) {
            if (response.isSuccessful()) {
                try (val reader = Objects.requireNonNull(response.body()).charStream()) {
                    return gson.<Collection<Detector>>fromJson(reader, Collection.class);
                }
            }
            log.warn("Failed to get monitors of {} with status code {}", application, response.code());
        } catch (Exception ex) {
            log.error("Unexpected error occurred during getting monitors:", ex);
        }
        throw new RuntimeException("Failed to get monitors of " + application);
    }

}
