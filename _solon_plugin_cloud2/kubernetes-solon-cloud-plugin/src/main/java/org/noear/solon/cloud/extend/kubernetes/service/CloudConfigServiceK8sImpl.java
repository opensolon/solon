package org.noear.solon.cloud.extend.kubernetes.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.util.ClientBuilder;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigService;
import org.noear.solon.cloud.utils.IntervalUtils;

import java.util.TimerTask;

/**
 * @author noear
 * @since 1.10
 */
public class CloudConfigServiceK8sImpl extends TimerTask implements CloudConfigService {
    final ApiClient client;
    final CoreV1Api configApi;

    private long refreshInterval;

    public CloudConfigServiceK8sImpl(CloudProps cloudProps) {
        try {
            client = ClientBuilder.defaultClient();
            configApi = new CoreV1Api(client);

            refreshInterval = IntervalUtils.getInterval(cloudProps.getConfigRefreshInterval("5s"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    public long getRefreshInterval() {
        return refreshInterval;
    }

    @Override
    public Config pull(String group, String name) {
        try {
            V1ConfigMap v1ConfigMap = configApi.readNamespacedConfigMap(name, group, null);

            if(v1ConfigMap == null){
                return null;
            }

            StringBuilder kvs = new StringBuilder();
            v1ConfigMap.getData().forEach((k, v) -> {
                kvs.append(k).append("=").append(v).append("\n");
            });

            return new Config(group, name, kvs.toString(), 0);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean push(String group, String name, String value) {
        return false;
    }

    @Override
    public boolean remove(String group, String name) {
        return false;
    }

    @Override
    public void attention(String group, String name, CloudConfigHandler observer) {

    }

    @Override
    public void run() {

    }
}
