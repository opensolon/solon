package org.noear.solon.admin.client.services;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.admin.client.config.ClientProperties;
import org.noear.solon.admin.client.data.Application;
import org.noear.solon.admin.client.data.EnvironmentInformation;
import org.noear.solon.admin.client.utils.JsonUtils;
import org.noear.solon.admin.client.utils.NetworkUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Result;

import java.io.IOException;
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
                .token(properties.getToken())
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

            assertResponse("register application", response, false);
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

            assertResponse("unregister application", response, false);
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

            assertResponse("send heartbeat", response, true);
        } catch (Exception ex) {
            log.error("Unexpected error occurred during the heartbeat sending:", ex);
        }
    }

    /**
     * 确认响应是否成功
     *
     * @param type     请求类型
     * @param response 响应
     * @param traceLog true 成功后打印 trace 日志，false 打印 info 日志
     */
    private void assertResponse(String type, Response response, boolean traceLog) throws IOException {
        if (!response.isSuccessful()) {
            log.error("Failed to {} to Solon Admin server. response: {}", type, response);
        }
        String res = new String(response.body().bytes());
        Result<?> result = ONode.load(res).toObject(Result.class);
        if (result.getCode() == 200) {
            if (traceLog) {
                log.trace("Successfully {} to Solon Admin server.", type);
            } else {
                log.info("Successfully {} to Solon Admin server.", type);
            }
        } else {
            log.error("Failed to {} to Solon Admin server. adminResponse: {}", type, res);
        }
    }

}
