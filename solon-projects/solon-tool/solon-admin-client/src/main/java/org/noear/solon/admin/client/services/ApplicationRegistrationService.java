package org.noear.solon.admin.client.services;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.*;
import org.noear.solon.Solon;
import org.noear.solon.admin.client.config.ClientProperties;
import org.noear.solon.admin.client.data.Application;
import org.noear.solon.admin.client.data.EnvironmentInformation;
import org.noear.solon.admin.client.utils.JsonUtils;
import org.noear.solon.admin.client.utils.NetworkUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.net.URL;

/**
 * 应用程序注册服务
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Slf4j
@Component
public class ApplicationRegistrationService {
    @Inject
    private OkHttpClient client;

    @Inject("${solon.app.name}")
    private String applicationName;

    @Inject
    private ClientProperties properties;

    private Application.ApplicationBuilder getApplicationBuilder() {
        return Application.builder()
                .name(this.applicationName)
                .baseUrl(NetworkUtils.getHostAndPort());
    }

    /**
     * 获取当前应用程序信息
     *
     * @return 当前应用程序信息
     */
    public Application getCurrentApplication() {
        return getApplicationBuilder().build();
    }

    /**
     * 向 Solon Admin Server 注册当前应用程序
     */
    public void register() {
        log.info("Attempting to register this client as an application with Solon Admin server...");
        val serverUrl = this.properties.getServerUrl().replaceAll("/+$", "");
        String clientMetadata = Solon.cfg().getProp("solon.app").toString();
        // 向 Server 发送注册请求
        try (Response response = client.newCall(new Request.Builder()
                .url(new URL(serverUrl + "/solon-admin/api/application/register"))
                .put(RequestBody.create(MediaType.parse("application/json"),
                        JsonUtils.toJson(
                                getApplicationBuilder()
                                        .metadata(clientMetadata)
                                        .showSecretInformation(properties.isShowSecretInformation())
                                        .environmentInformation(EnvironmentInformation.create())
                                        .build()
                        )))
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

    /**
     * 向 Solon Admin Server 注销
     */
    public void unregister() {
        log.info("Attempting to unregister this client from Solon Admin server...");
        val serverUrl = this.properties.getServerUrl().replaceAll("/+$", "");
        // 向 Server 发送注销请求
        try (Response response = client.newCall(new Request.Builder()
                .url(new URL(serverUrl + "/solon-admin/api/application/unregister"))
                .delete(RequestBody.create(MediaType.parse("application/json"),
                        JsonUtils.toJson(getCurrentApplication())))
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

    /**
     * 向 Solon Admin Server 发送心跳
     */
    public void heartbeat() {
        log.trace("Attempting to send heartbeat to Solon Admin server...");
        val serverUrl = this.properties.getServerUrl().replaceAll("/+$", "");
        // 向 Server 发送心跳请求
        try (Response response = client.newCall(new Request.Builder()
                .url(new URL(serverUrl + "/solon-admin/api/application/heartbeat"))
                .post(RequestBody.create(MediaType.parse("application/json"),
                        JsonUtils.toJson(
                                getCurrentApplication()
                        )))
                .build()).execute()) {
            if (response.isSuccessful()) {
                log.trace("Successfully send heartbeat to Solon Admin server.");
                return;
            }
            log.error("Failed to send heartbeat to Solon Admin server. Response: {}", response);
        } catch (Exception ex) {
            log.error("Unexpected error occurred during the heartbeat sending:", ex);
        }
    }

}
