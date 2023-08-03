package org.noear.solon.admin.server.services;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.noear.snack.core.TypeRef;
import org.noear.solon.admin.server.data.Application;
import org.noear.solon.admin.server.data.Detector;
import org.noear.solon.admin.server.utils.JsonUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author shaokeyibb
 * @since 2.3
 */
@Slf4j
@Component
public class ClientMonitorService {

    @Inject
    private OkHttpClient client;

    /**
     * 获取应用程序的监视器
     * @param application 应用程序
     * @return 应用程序的监视器
     */
    public Collection<Detector> getMonitors(Application application) {
        val clientUrl = application.getBaseUrl().replaceAll("/+$", "");
        try (Response response = client.newCall(new Request.Builder()
                .url(new URL(clientUrl + "/solon-admin/api/monitor/data"))
                .header("TOKEN", application.getToken())
                .get()
                .build()).execute()) {
            if (response.isSuccessful()) {
                String json = Objects.requireNonNull(response.body()).string();

                return JsonUtils.fromJson(json,  (new TypeRef<List<Detector>>(){}).getClass());
            }
            log.warn("Failed to get monitors of {} with status code {}", application, response.code());
        } catch (Exception ex) {
            log.error("Unexpected error occurred during getting monitors:", ex);
        }
        throw new RuntimeException("Failed to get monitors of " + application);
    }

}
